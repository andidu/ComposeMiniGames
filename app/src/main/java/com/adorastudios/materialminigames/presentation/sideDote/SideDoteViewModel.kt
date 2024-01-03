package com.adorastudios.materialminigames.presentation.sideDote

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
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
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class SideDoteViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(
        SideDoteState(
            dote = DoteData(
                positionX = CENTER_X,
                positionY = CENTER_Y,
                speedX = DEFAULT_X_SPEED,
                speedY = -DEFAULT_Y_SPEED,
            ),
            env = EnvData(
                rightSpines = SpineData.randomSpines(MainActivity.HEIGHT.toFloat(), UNIT_SIZE),
                leftSpines = SpineData.randomSpines(MainActivity.HEIGHT.toFloat(), UNIT_SIZE),
            ),
            unitSize = UNIT_SIZE,
        ),
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (!state.value.defeat) {
                    update()
                    delay(UPDATE_INTERVAL)
                }
            }
        }
    }

    private suspend fun update() {
        val currentState = _state.value
        withContext(Dispatchers.Default) {
            val newYPosition = currentState.dote.positionY + currentState.dote.speedY
            val newXPosition = max(
                RADIUS,
                min(
                    currentState.env.width - RADIUS,
                    currentState.dote.positionX + currentState.dote.speedX,
                ),
            )
            val addScore =
                if (newXPosition <= RADIUS || newXPosition >= state.value.env.width - RADIUS) 1 else 0
            val newXSpeed = if (currentState.dote.speedX > 0) {
                if (newXPosition >= currentState.env.width - RADIUS) -DEFAULT_X_SPEED else DEFAULT_X_SPEED
            } else {
                if (newXPosition <= RADIUS) DEFAULT_X_SPEED else -DEFAULT_X_SPEED
            }

            val newRightSpines =
                if (newXPosition <= CENTER_X && currentState.dote.positionX > CENTER_X) {
                    SpineData.randomSpines(
                        MainActivity.HEIGHT.toFloat(),
                        UNIT_SIZE,
                        0.5f + (currentState.score / 10) * 0.1f,
                    )
                } else {
                    currentState.env.rightSpines
                }
            val newLeftSpines =
                if (newXPosition >= CENTER_X && currentState.dote.positionX < CENTER_X) {
                    SpineData.randomSpines(
                        MainActivity.HEIGHT.toFloat(),
                        UNIT_SIZE,
                        0.5f + (currentState.score / 10) * 0.1f,
                    )
                } else {
                    currentState.env.leftSpines
                }

            var defeat = false
            if (newYPosition <= UNIT_SIZE / 4 || newYPosition >= MainActivity.HEIGHT - UNIT_SIZE / 4) {
                defeat = true
            }
            if (newXPosition <= UNIT_SIZE) {
                val above =
                    currentState.env.leftSpines
                        .filter { it.positionY + UNIT_SIZE / 2 <= newYPosition }
                        .maxByOrNull { it.positionY }
                val below =
                    currentState.env.leftSpines
                        .filter { it.positionY + UNIT_SIZE / 2 >= newYPosition }
                        .minByOrNull { it.positionY }

                // top triangle line: y = x + positionY
                // bottom line: y = - x + positionY + UNIT_SIZE
                val leftDoteCoordinate =
                    Offset(newXPosition - RADIUS, newYPosition)
                val leftBottomDoteCoordinate =
                    Offset(
                        newXPosition - RADIUS_PROJECTION,
                        newYPosition + RADIUS_PROJECTION,
                    )
                val leftTopDoteCoordinate =
                    Offset(
                        newXPosition - RADIUS_PROJECTION,
                        newYPosition - RADIUS_PROJECTION,
                    )

                if (above != null) {
                    // check if most left dote is inside spine above
                    if (checkLeftIntersection(leftDoteCoordinate, above)) {
                        defeat = true
                    }
                    // check if most left-top dote is inside spine above
                    if (checkLeftIntersection(leftTopDoteCoordinate, above)) {
                        defeat = true
                    }
                }
                if (below != null) {
                    // check if most left dote is inside spine below
                    if (checkLeftIntersection(leftDoteCoordinate, below)) {
                        defeat = true
                    }
                    // check if most left-bottom dote is inside spine above
                    if (checkLeftIntersection(leftBottomDoteCoordinate, below)) {
                        defeat = true
                    }
                }
            }
            if (newXPosition >= MainActivity.WIDTH - UNIT_SIZE) {
                val above =
                    currentState.env.rightSpines
                        .filter { it.positionY + UNIT_SIZE / 2 <= newYPosition }
                        .maxByOrNull { it.positionY }
                val below =
                    currentState.env.rightSpines
                        .filter { it.positionY + UNIT_SIZE / 2 >= newYPosition }
                        .minByOrNull { it.positionY }

                // top triangle line: y = -x + positionY + WIDTH
                // bottom line: y = x + positionY + UNIT_SIZE - WIDTH
                val rightDoteCoordinate =
                    Offset(newXPosition + RADIUS, newYPosition)
                val rightBottomDoteCoordinate =
                    Offset(
                        newXPosition + RADIUS_PROJECTION,
                        newYPosition + RADIUS_PROJECTION,
                    )
                val rightTopDoteCoordinate =
                    Offset(
                        newXPosition + RADIUS_PROJECTION,
                        newYPosition - RADIUS_PROJECTION,
                    )

                if (above != null) {
                    // check if most right dote is inside spine above
                    if (checkRightIntersection(rightDoteCoordinate, above)) {
                        defeat = true
                    }
                    // check if most right-top dote is inside spine above
                    if (checkRightIntersection(rightTopDoteCoordinate, above)) {
                        defeat = true
                    }
                }
                if (below != null) {
                    // check if most left dote is inside spine below
                    if (checkRightIntersection(rightDoteCoordinate, below)) {
                        defeat = true
                    }
                    // check if most left-bottom dote is inside spine above
                    if (checkRightIntersection(rightBottomDoteCoordinate, below)) {
                        defeat = true
                    }
                }
            }

            withContext(Dispatchers.Main) {
                _state.update { state ->
                    state.copy(
                        dote = state.dote.copy(
                            positionX = newXPosition,
                            positionY = newYPosition,
                            speedX = newXSpeed,
                            speedY = state.dote.speedY + Y_ACS,
                        ),
                        env = state.env.copy(
                            rightSpines = newRightSpines,
                            leftSpines = newLeftSpines,
                        ),
                        score = state.score + addScore,
                        defeat = defeat,
                    )
                }
            }
        }
    }

    private fun checkLeftIntersection(coordinate: Offset, spine: SpineData): Boolean {
        return coordinate.y >= coordinate.x + spine.positionY &&
            coordinate.y <= -coordinate.x + spine.positionY + UNIT_SIZE
    }

    private fun checkRightIntersection(coordinate: Offset, spine: SpineData): Boolean {
        return coordinate.y >= -coordinate.x + spine.positionY + MainActivity.WIDTH &&
            coordinate.y <= coordinate.x + spine.positionY + UNIT_SIZE - MainActivity.WIDTH
    }

    fun click() {
        _state.update { state ->
            state.copy(
                dote = state.dote.copy(
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
        private val RADIUS = UNIT_SIZE / 4f
        private val RADIUS_PROJECTION = RADIUS / 1.41f
        private val Y_ACS = UNIT_SIZE / 40f
        private val DEFAULT_Y_SPEED = UNIT_SIZE / 3f
        private val DEFAULT_X_SPEED = UNIT_SIZE / 10f
        private val CENTER_X = MainActivity.WIDTH / 2f
        private val CENTER_Y = MainActivity.HEIGHT / 2f
    }
}
