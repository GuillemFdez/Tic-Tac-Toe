package com.guillem.tic_tac_toe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun GameScreen(mode: String, p1: String, p2: String, onBack: () -> Unit) {
    val viewModel = remember { GameViewModel() }
    val cellPositions = remember { mutableStateMapOf<Int, LayoutCoordinates>() }
    val currentPlayerName = if (viewModel.isPlayer1Turn) p1 else p2

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Torn de: $currentPlayerName", fontSize = 24.sp)
        Spacer(Modifier.height(20.dp))

        // Taulell
        Box(Modifier.size(300.dp).background(Color.LightGray)) {
            Column {
                for (row in 0..2) {
                    Row {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            Box(Modifier.size(100.dp).border(1.dp, Color.Black).onGloballyPositioned { cellPositions[index] = it },
                                contentAlignment = Alignment.Center) {
                                Text(viewModel.board[index], fontSize = 40.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(50.dp))

        // Fitxa arrossegable
        if (viewModel.winner == null && !viewModel.isDraw) {
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

        // Diàlegs de final de partida
        if (viewModel.winner != null || viewModel.isDraw) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(if (viewModel.isDraw) "Empat!" else "Guanyador: ${viewModel.winner}") },
                confirmButton = { Button(onClick = { viewModel.resetGame() }) { Text("Reiniciar") } },
                dismissButton = { Button(onClick = onBack) { Text("Sortir") } }
            )
        }
    }
}

@Composable
fun DraggableToken(symbol: String, onDrop: (Offset) -> Unit) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var globalPosition by remember { mutableStateOf(Offset.Zero) }

    Box(
        Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .size(80.dp)
            .background(if (symbol == "X") Color.Blue else Color.Red, CircleShape)
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
        Text(symbol, color = Color.White, fontSize = 30.sp)
    }
}
