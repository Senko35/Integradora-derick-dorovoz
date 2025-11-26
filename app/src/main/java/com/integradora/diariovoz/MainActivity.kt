package com.integradora.diariovoz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
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

                        // ================================
                        // LOGIN
                        // ================================
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

                        // ================================
                        // REGISTER
                        // ================================
                        composable("register") {
                            RegisterScreen(
                                onRegistroExitoso = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // ================================
                        // RECORD SCREEN (Pantalla 1)
                        // ================================
                        composable("record") {
                            RecordScreen(
                                onGoToSchedule = { audioPath ->
                                    navController.navigate("schedule/$audioPath")
                                }
                            )
                        }

                        // ================================
                        // SCHEDULE SCREEN (Pantalla 2)
                        // ================================
                        composable(
                            route = "schedule/{audioPath}",
                            arguments = listOf(
                                navArgument("audioPath") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val audioPath = backStackEntry.arguments?.getString("audioPath") ?: ""

                            ScheduleScreen(audioPath)
                        }
                    }
                }
            }
        }
    }
}
