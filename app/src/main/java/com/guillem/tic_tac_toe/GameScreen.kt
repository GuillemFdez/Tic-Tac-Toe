package com.guillem.tic_tac_toe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun GameScreen(mode: String, p1: String, p2: String, onBack: () -> Unit) {
    val viewModel = remember { GameViewModel() }
    val cellPositions = remember { mutableStateMapOf<Int, LayoutCoordinates>() }
    val currentPlayerName = if (viewModel.isPlayer1Turn) p1 else p2

    // Definim un degradat per al fons de la pantalla
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF3949AB))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Targeta superior per indicar el torn actual
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Torn de: $currentPlayerName",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }

            // Contenidor principal del taulell amb ombra i cantonades arrodonides
            Surface(
                modifier = Modifier
                    .size(320.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp)),
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0..2) {
                                val index = row * 3 + col
                                // Cada casella del taulell
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .background(Color.Transparent)
                                        .border(0.5.dp, Color.Gray.copy(alpha = 0.3f))
                                        .onGloballyPositioned { cellPositions[index] = it },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Estilitzem les lletres X i O amb colors diferents
                                    Text(
                                        text = viewModel.board[index],
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (viewModel.board[index] == "X") Color(0xFFD32F2F) else Color(0xFF1976D2)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(60.dp))

            // Zona de la fitxa arressegable
            if (viewModel.winner == null && !viewModel.isDraw) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Arrossega la fitxa", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    DraggableToken(symbol = if (viewModel.isPlayer1Turn) "X" else "O") { dropOffset ->
                        val droppedIndex = cellPositions.entries.find { entry ->
                            val rect = entry.value.positionInRoot().let {
                                androidx.compose.ui.geometry.Rect(it.x, it.y, it.x + entry.value.size.width, it.y + entry.value.size.height)
                            }
                            rect.contains(dropOffset)
                        }?.key
                        droppedIndex?.let { viewModel.onDrop(it, p1, p2, mode == "1P") }
                    }
                }
            }
        }

        // Diàleg final
        if (viewModel.winner != null || viewModel.isDraw) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        text = if (viewModel.isDraw) "Empat!" else "🎉 Guanyador!",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    if (viewModel.winner != null) {
                        Text("Enhorabona ${viewModel.winner}, has guanyat la partida!")
                    } else {
                        Text("No hi ha més moviments possibles.")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.resetGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                    ) { Text("Tornar a jugar") }
                },
                dismissButton = {
                    TextButton(onClick = onBack) { Text("Sortir al menú", color = Color.Gray) }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun DraggableToken(symbol: String, onDrop: (Offset) -> Unit) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var globalPosition by remember { mutableStateOf(Offset.Zero) }

    // Colors vibrants per a les fitxes
    val tokenColor = if (symbol == "X") Color(0xFFF44336) else Color(0xFF2196F3)

    Box(
        Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .size(85.dp)
            // Ombra per donar sensació de profunditat en arrossegar
            .shadow(if (offset == Offset.Zero) 4.dp else 16.dp, CircleShape)
            .background(tokenColor, CircleShape)
            .clip(CircleShape)
            .onGloballyPositioned { globalPosition = it.positionInRoot() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        onDrop(globalPosition)
                        offset = Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offset += dragAmount
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    }
}