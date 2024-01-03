package com.adorastudios.materialminigames.presentation.sideDote

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SideDoteScreen(
    viewModel: SideDoteViewModel = viewModel(),
) {
    val state = viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        SideDote(
            modifier = Modifier
                .fillMaxSize()
                .background(if (state.value.defeat) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.background)
                .pointerInput(true) { detectTapGestures { viewModel.click() } },
            state = state.value,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .background(
                        if (state.value.defeat) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primaryContainer,
                    )
                    .padding(8.dp),
                text = when {
                    state.value.score < 10 -> "00${state.value.score}"
                    state.value.score < 100 -> "0${state.value.score}"
                    else -> "${state.value.score}"
                },
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Monospace,
                ),
                color = if (state.value.defeat) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
