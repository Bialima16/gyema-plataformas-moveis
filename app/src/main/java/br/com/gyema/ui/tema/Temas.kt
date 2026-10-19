package br.com.gyema.ui.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun GyemaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Laranja,
            background = Fundo,
            surface = Superficie,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        typography = GyemaTypography
    ) {
        Surface(color = Fundo, contentColor = Color.White) {
            content()
        }
    }
}