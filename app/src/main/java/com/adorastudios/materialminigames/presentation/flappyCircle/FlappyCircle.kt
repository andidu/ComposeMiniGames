package com.adorastudios.materialminigames.presentation.flappyCircle

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

@Composable
fun FlappyCircle(
    modifier: Modifier = Modifier,
    state: FlappyCircleState,
) {
    val colors = MaterialTheme.colorScheme
    Canvas(modifier = modifier) {
        val radius = state.unitSize / 4
        drawCircle(
            color = if (state.defeat) colors.error else colors.primary,
            radius = radius,
            center = Offset(
                x = state.env.width / 2,
                y = state.circle.positionY,
            ),
        )

        for (block in state.env.blocks) {
            drawRect(
                color = if (state.defeat) colors.onErrorContainer else colors.secondaryContainer,
                topLeft = Offset(block.positionX, block.top),
                size = Size(state.unitSize, state.env.height - block.top),
            )
            drawRect(
                color = if (state.defeat) colors.onErrorContainer else colors.secondaryContainer,
                topLeft = Offset(block.positionX, 0f),
                size = Size(state.unitSize, block.top - 4 * state.unitSize),
            )
        }
    }
}
