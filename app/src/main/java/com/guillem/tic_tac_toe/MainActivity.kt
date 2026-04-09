package com.guillem.tic_tac_toe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//Hola
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = Screen.Home.route) {
                composable(Screen.Home.route) { HomeScreen(navController) }
                composable(Screen.Credits.route) { CreditsScreen(navController) }
                composable(Screen.Config.route) { ConfigScreen(navController) }
                composable("game/{mode}/{p1}/{p2}") { backStackEntry ->
                    val mode = backStackEntry.arguments?.getString("mode") ?: "2P"
                    val p1 = backStackEntry.arguments?.getString("p1") ?: "Jugador 1"
                    val p2 = backStackEntry.arguments?.getString("p2") ?: "CPU"
                    GameScreen(mode, p1, p2) { navController.popBackStack() }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("TIC TAC TOE", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.navigate(Screen.Config.route) }) { Text("Jugar") }
        Button(onClick = { navController.navigate(Screen.Credits.route) }) { Text("Crèdits") }
    }
}

@Composable
fun CreditsScreen(navController: NavHostController) {
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("Projecte: Tic Tac Toe", style = MaterialTheme.typography.headlineMedium)
        Text("Alumne: Guillem Fernández Benet")
        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) { Text("Tornar") }
    }
}

@Composable
fun ConfigScreen(navController: NavHostController) {
    var isVsCpu by remember { mutableStateOf(false) }
    var p1Name by remember { mutableStateOf("") }
    var p2Name by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), Arrangement.Center, Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Jugar contra la CPU")
            Checkbox(checked = isVsCpu, onCheckedChange = { isVsCpu = it })
        }
        TextField(value = p1Name, onValueChange = { p1Name = it }, label = { Text("Nom Jugador 1") })
        Spacer(Modifier.height(8.dp))
        if (!isVsCpu) {
            TextField(value = p2Name, onValueChange = { p2Name = it }, label = { Text("Nom Jugador 2") })
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val mode = if (isVsCpu) "1P" else "2P"
            val p2 = if (isVsCpu) "Robot" else p2Name
            navController.navigate(Screen.Game.createRoute(mode, p1Name, p2))
        }) { Text("Començar Partida") }
    }
}
