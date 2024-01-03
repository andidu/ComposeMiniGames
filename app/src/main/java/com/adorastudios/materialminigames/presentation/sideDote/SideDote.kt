package com.adorastudios.materialminigames.presentation.sideDote

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

@Composable
fun SideDote(
    modifier: Modifier = Modifier,
    state: SideDoteState,
) {
    val colors = MaterialTheme.colorScheme
    Canvas(modifier = modifier) {
        val radius = state.unitSize / 4
        drawCircle(
            color = if (state.defeat) colors.error else colors.primary,
            radius = radius,
            center = Offset(
                x = state.dote.positionX,
                y = state.dote.positionY,
            ),
        )

        for (spine in state.env.leftSpines) {
            drawPath(
                color = if (state.defeat) colors.onErrorContainer else colors.secondaryContainer,
                path = Path().apply {
                    moveTo(state.unitSize / 2, spine.positionY + state.unitSize / 2)
                    lineTo(0f, spine.positionY)
                    lineTo(0f, spine.positionY + state.unitSize)
                },
            )
        }
        for (spine in state.env.rightSpines) {
            drawPath(
                color = if (state.defeat) colors.onErrorContainer else colors.secondaryContainer,
                path = Path().apply {
                    moveTo(state.env.width - state.unitSize / 2, spine.positionY + state.unitSize / 2)
                    lineTo(state.env.width, spine.positionY)
                    lineTo(state.env.width, spine.positionY + state.unitSize)
                },
            )
        }
    }
}
