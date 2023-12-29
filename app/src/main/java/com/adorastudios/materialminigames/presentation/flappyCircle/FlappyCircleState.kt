package com.adorastudios.materialminigames.presentation.flappyCircle

import androidx.compose.runtime.Immutable
import com.adorastudios.materialminigames.MainActivity
import kotlin.random.Random

data class FlappyCircleState(
    val circle: CircleData = CircleData(),
    val env: EnvData = EnvData(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val unitSize: Float = 0f,
    val defeat: Boolean = false,
)

data class CircleData(
    val positionY: Float = 0f,
    val speedY: Float = 0f,
)

@Immutable
data class EnvData(
    val speedX: Float = 0f,
    val height: Float = MainActivity.HEIGHT.toFloat(),
    val width: Float = MainActivity.WIDTH.toFloat(),
    val blocks: List<BlockData> = emptyList(),
)

data class BlockData(
    val positionX: Float,
    val top: Float,
    val passed: Boolean,
) {
    companion object {
        fun randomBlock(units: Int, width: Float, unitSize: Float): BlockData {
            return BlockData(
                positionX = width,
                top = (Random.nextInt(units - 6) + 5) * unitSize,
                passed = false,
            )
        }
    }
}
