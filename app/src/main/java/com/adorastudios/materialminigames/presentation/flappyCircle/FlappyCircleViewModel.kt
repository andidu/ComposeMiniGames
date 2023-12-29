package com.adorastudios.materialminigames.presentation.flappyCircle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adorastudios.materialminigames.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FlappyCircleViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(
        FlappyCircleState(
            circle = CircleData(
                positionY = (MainActivity.HEIGHT / 2).toFloat(),
                speedY = -DEFAULT_Y_SPEED,
            ),
            env = EnvData(
                speedX = UNIT_SIZE / 25,
                blocks = listOf(),
            ),
            unitSize = UNIT_SIZE,
        ),
    )
    val state = _state.asStateFlow()
    private var lastBlockCounterCreated = 0f

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var counter = 0f
                while (!state.value.defeat) {
                    update(counter)
                    counter += (state.value.env.speedX / UNIT_SIZE * 25)
                    delay(UPDATE_INTERVAL)
                }
            }
        }
    }

    private suspend fun update(counter: Float) {
        val currentState = _state.value
        withContext(Dispatchers.Default) {
            val newYPosition = currentState.circle.positionY + currentState.circle.speedY

            val blocks = mutableListOf<BlockData>()
            blocks.addAll(currentState.env.blocks)
            blocks.forEachIndexed { index, block ->
                blocks[index] = block.copy(
                    positionX = block.positionX - currentState.env.speedX,
                )
            }
            var newSpeedX = currentState.env.speedX
            var score = 0
            var defeat = false
            if (blocks.getOrNull(0)?.positionX?.isLower(-UNIT_SIZE) == true) {
                blocks.removeAt(0)
                newSpeedX += UNIT_SIZE / 25 * 0.01f
            }
            if (counter >= lastBlockCounterCreated) {
                lastBlockCounterCreated += 140
                blocks += BlockData.randomBlock(
                    units = UNITS_IN_HEIGHT,
                    unitSize = UNIT_SIZE,
                    width = MainActivity.WIDTH.toFloat(),
                )
            }

            if (newYPosition - RADIUS < 0 || newYPosition + RADIUS > MainActivity.HEIGHT) {
                defeat = true
            }
            for (block in blocks) {
                if (block.positionX > CENTER_X + RADIUS ||
                    block.positionX + UNIT_SIZE < CENTER_X - RADIUS
                ) {
                    continue
                }

                listOf(
                    CENTER_X to newYPosition - RADIUS,
                    CENTER_X + RADIUS_PROJECTION to newYPosition - RADIUS_PROJECTION,
                    CENTER_X + RADIUS to newYPosition,
                    CENTER_X + RADIUS_PROJECTION to newYPosition + RADIUS_PROJECTION,
                    CENTER_X to newYPosition + RADIUS,
                    CENTER_X - RADIUS_PROJECTION to newYPosition - RADIUS_PROJECTION,
                    CENTER_X - RADIUS to newYPosition,
                    CENTER_X - RADIUS_PROJECTION to newYPosition + RADIUS_PROJECTION,
                ).forEach {
                    if (it.first >= block.positionX && it.first <= block.positionX + UNIT_SIZE) {
                        if (it.second > block.top || it.second < block.top - UNIT_SIZE * 4) {
                            defeat = true
                            return@forEach
                        }
                    }
                }
                if (defeat) {
                    break
                }
            }

            blocks.forEachIndexed { index, block ->
                if (block.positionX + UNIT_SIZE < CENTER_X - RADIUS && !block.passed) {
                    blocks[index] = block.copy(
                        passed = true,
                    )
                    score += 1
                }
            }

            withContext(Dispatchers.Main) {
                _state.update { state ->
                    state.copy(
                        circle = state.circle.copy(
                            positionY = newYPosition,
                            speedY = state.circle.speedY + Y_ACS,
                        ),
                        env = state.env.copy(
                            blocks = blocks,
                            speedX = newSpeedX,
                        ),
                        score = state.score + score,
                        defeat = defeat,
                    )
                }
            }
        }
    }

    fun click() {
        _state.update { state ->
            state.copy(
                circle = state.circle.copy(
                    speedY = -DEFAULT_Y_SPEED,
                ),
            )
        }
    }

    companion object {
        private const val FPS = 30
        private const val UPDATE_INTERVAL = 1000L / FPS
        private const val UNITS_IN_HEIGHT = 20
        private val UNIT_SIZE = (MainActivity.HEIGHT / UNITS_IN_HEIGHT).toFloat()
        private val DIAMETER = UNIT_SIZE / 2f
        private val RADIUS = UNIT_SIZE / 4f
        private val RADIUS_PROJECTION = RADIUS / 1.41f
        private val Y_ACS = UNIT_SIZE / 40f
        private val DEFAULT_Y_SPEED = UNIT_SIZE / 3f
        private val CENTER_X = MainActivity.WIDTH / 2f
        private val CENTER_Y = MainActivity.HEIGHT / 2f
    }
}

private fun Float.isLower(value: Float): Boolean = this < value
