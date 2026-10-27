package br.com.gyema.ui.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val TextoSuave = Color(0xFFC4BED9)
@Composable
fun GyemaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Laranja, background = Fundo, surface = Superficie,
            onBackground = Color.White, onSurface = Color.White
        ), content = content
    )
}