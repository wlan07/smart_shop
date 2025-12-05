package com.example.smartshop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.smartshop.auth.LoginScreen
import com.example.smartshop.ui.HomeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = NavRoutes.LOGIN) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onSuccess = { navController.navigate(NavRoutes.HOME) { popUpTo(0) } }
            )
        }
        composable(NavRoutes.HOME) { HomeScreen(
            onLogout = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        ) }
    }
}
