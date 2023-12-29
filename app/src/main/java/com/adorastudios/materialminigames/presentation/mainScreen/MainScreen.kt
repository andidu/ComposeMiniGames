package com.adorastudios.materialminigames.presentation.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adorastudios.materialminigames.R
import com.adorastudios.materialminigames.presentation.Screens
import com.adorastudios.materialminigames.presentation.flappyCircle.BlockData
import com.adorastudios.materialminigames.presentation.flappyCircle.CircleData
import com.adorastudios.materialminigames.presentation.flappyCircle.EnvData
import com.adorastudios.materialminigames.presentation.flappyCircle.FlappyCircle
import com.adorastudios.materialminigames.presentation.flappyCircle.FlappyCircleState

@Composable
fun MainScreen(
    navController: NavController,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        navController.navigate(Screens.FlappyCircle.route)
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1.5f),
                    text = stringResource(id = R.string.flappyCircleName),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                ) {
                    val width = with(LocalDensity.current) { maxWidth.toPx() }
                    val height = with(LocalDensity.current) { maxHeight.toPx() }
                    FlappyCircle(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.background),
                        state = FlappyCircleState(
                            circle = CircleData(
                                positionY = height / 2,
                            ),
                            env = EnvData(
                                height = height,
                                width = width,
                                blocks = listOf(
                                    BlockData.randomBlock(10, width - 3 * height / 10, height / 10),
                                ),
                            ),
                            unitSize = height / 10,
                        ),
                    )
                }
            }
        }
    }
}
