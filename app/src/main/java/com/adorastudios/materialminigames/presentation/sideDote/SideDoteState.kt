package com.adorastudios.materialminigames.presentation.sideDote

import com.adorastudios.materialminigames.MainActivity
import kotlin.math.min
import kotlin.random.Random

data class SideDoteState(
    val dote: DoteData,
    val env: EnvData,
    val defeat: Boolean = false,
    val score: Int = 0,
    val bestScore: Int = 0,
    val unitSize: Float = 0f,
)

data class DoteData(
    val positionX: Float,
    val positionY: Float,
    val speedX: Float,
    val speedY: Float,
)

data class EnvData(
    val rightSpines: List<SpineData>,
    val leftSpines: List<SpineData>,
    val height: Float = MainActivity.HEIGHT.toFloat(),
    val width: Float = MainActivity.WIDTH.toFloat(),
)

data class SpineData(
    val positionY: Float,
) {
    companion object {
        fun randomSpines(
            height: Float,
            unitSize: Float,
            occupancyRate: Float = 0.5f,
        ): List<SpineData> {
            val total = (height / unitSize).toInt()

            val amount = min(total - 2, (Random.nextInt(3) + total * occupancyRate).toInt() - 2)

            val list = (0 until total).toList().shuffled().take(amount)

            return list.map {
                SpineData(it * unitSize)
            }
        }
    }
}
