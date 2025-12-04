package com.integradora.diariovoz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.*
import com.integradora.diariovoz.screens.*
import com.integradora.diariovoz.theme.DiariovozTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DiariovozTheme {

                Surface(color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                onRegistrarClick = {
                                    navController.navigate("register")
                                },
                                onLoginSuccess = {
                                    navController.navigate("record") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onRegistroExitoso = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("record") {
                            RecordScreen(
                                onGoToSchedule = {
                                    navController.navigate("audiolist")
                                },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo("record") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("audiolist") {
                            AudioListScreen(navController)   // 👈 AGREGADO
                        }
                    }
                }
            }
        }
    }
}
