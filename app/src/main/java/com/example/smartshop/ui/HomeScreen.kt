package com.example.smartshop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartshop.data.ProductEntity
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val vm: ProductViewModel = viewModel(factory = ProductViewModelFactory(context.applicationContext))

    val products by vm.products.collectAsState()
    val stats by vm.stats.collectAsState()

    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser

    var showDialog by remember { mutableStateOf(false) }
    var editProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartShop") },
                actions = {
                    TextButton(onClick = {
                        auth.signOut()
                        onLogout()
                    }) {
                        Text("Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editProduct = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // Profile mini header
            Text(
                text = "Bonjour, ${user?.email ?: "Utilisateur"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            // Stats
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Statistiques", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Nombre total de produits : ${stats.totalCount}")
                    Text("Valeur totale du stock : ${"%.2f".format(stats.totalStockValue)}")
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Produits", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (products.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun produit. Cliquez sur + pour en ajouter.")
                }
            } else {
                LazyColumn {
                    items(products) { p ->
                        ProductRow(
                            product = p,
                            onClick = {
                                editProduct = p
                                showDialog = true
                            },
                            onDelete = { vm.delete(p) }
                        )
                    }
                }
            }

            errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }

        if (showDialog) {
            ProductDialog(
                initial = editProduct,
                onDismiss = { showDialog = false },
                onSave = { id, name, qty, price ->
                    vm.saveProduct(id, name, qty, price) { msg ->
                        errorMessage = msg
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
private fun ProductRow(
    product: ProductEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.SemiBold)
                Text("Quantité : ${product.quantity}")
                Text("Prix : ${product.price}")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Supprimer")
            }
        }
    }
}
