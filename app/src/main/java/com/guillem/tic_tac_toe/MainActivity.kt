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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Inicialitzem el controlador de navegació per gestionar el canvi de pantalles
            val navController = rememberNavController()

            // Definim l'estructura de navegació (NavHost) i la pantalla inicial
            NavHost(navController, startDestination = Screen.Home.route) {
                // Ruta per a la pantalla de menú principal
                composable(Screen.Home.route) { HomeScreen(navController) }

                // Ruta per a la pantalla de crèdits
                composable(Screen.Credits.route) { CreditsScreen(navController) }

                // Ruta per a la pantalla de configuració de la partida
                composable(Screen.Config.route) { ConfigScreen(navController) }

                // Ruta per al joc, que rep paràmetres dinàmics (mode, nom p1, nom p2)
                composable("game/{mode}/{p1}/{p2}") { backStackEntry ->
                    val mode = backStackEntry.arguments?.getString("mode") ?: "2P"
                    val p1 = backStackEntry.arguments?.getString("p1") ?: "Jugador 1"
                    val p2 = backStackEntry.arguments?.getString("p2") ?: "CPU"
                    // Cridem a la pantalla del joc passant-li les dades recollides
                    GameScreen(mode, p1, p2) { navController.popBackStack() }
                }
            }
        }
    }
}

// Pantalla principal amb el títol i els botons d'accés
@Composable
fun HomeScreen(navController: NavHostController) {
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("TIC TAC TOE", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        // Navega cap a la configuració per començar a jugar
        Button(onClick = { navController.navigate(Screen.Config.route) }) { Text("Jugar") }
        // Navega cap a la informació de l'autor
        Button(onClick = { navController.navigate(Screen.Credits.route) }) { Text("Crèdits") }
    }
}

// Pantalla informativa sobre l'increïblement atractiu autor del projecte (jo)
@Composable
fun CreditsScreen(navController: NavHostController) {
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Text("Projecte: Tic Tac Toe", style = MaterialTheme.typography.headlineMedium)
        Text("Alumne: Guillem Fernández Benet")
        Spacer(Modifier.height(16.dp))
        // Botó per retrocedir a la pantalla anterior
        Button(onClick = { navController.popBackStack() }) { Text("Tornar") }
    }
}

// Pantalla on l'usuari tria contra qui juga i els noms dels jugadors
@Composable
fun ConfigScreen(navController: NavHostController) {
    // Estats per controlar si es juga contra la CPU i guardar els noms introduïts
    var isVsCpu by remember { mutableStateOf(false) }
    var p1Name by remember { mutableStateOf("") }
    var p2Name by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp), Arrangement.Center, Alignment.CenterHorizontally) {
        // Opció per activar o desactivar el mode contra la màquina
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Jugar contra la CPU")
            Checkbox(checked = isVsCpu, onCheckedChange = { isVsCpu = it })
        }

        // Camp de text per al primer jugador
        TextField(value = p1Name, onValueChange = { p1Name = it }, label = { Text("Nom Jugador 1") })
        Spacer(Modifier.height(8.dp))

        // Si no es contra la CPU, mostrem el camp per al segon jugador
        if (!isVsCpu) {
            TextField(value = p2Name, onValueChange = { p2Name = it }, label = { Text("Nom Jugador 2") })
        }

        Spacer(Modifier.height(16.dp))

        // Botó per iniciar la partida enviant tota la configuració a la pantalla del joc
        Button(onClick = {
            val mode = if (isVsCpu) "1P" else "2P"
            val p2 = if (isVsCpu) "Robot" else p2Name
            navController.navigate(Screen.Game.createRoute(mode, p1Name, p2))
        }) { Text("Començar Partida") }
    }
}