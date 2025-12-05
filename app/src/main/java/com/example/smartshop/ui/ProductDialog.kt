package com.example.smartshop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import com.example.smartshop.data.ProductEntity

@Composable
fun ProductDialog(
    initial: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (id: String?, name: String, qty: Int, price: Double) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name.orEmpty()) }
    var qtyText by remember { mutableStateOf(initial?.quantity?.toString().orEmpty()) }
    var priceText by remember { mutableStateOf(initial?.price?.toString().orEmpty()) }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(Modifier.padding(16.dp)) {
                Text(
                    if (initial == null) "Ajouter un produit" else "Modifier le produit",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { qtyText = it },
                    label = { Text("Quantité") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Prix") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        val qty = qtyText.toIntOrNull() ?: 0
                        val price = priceText.toDoubleOrNull() ?: 0.0
                        onSave(initial?.id, name.trim(), qty, price)
                    }) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
