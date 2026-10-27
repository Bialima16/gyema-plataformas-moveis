package br.com.gyema.ui.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Fundo = Color(0xFF0B0F2E)
val Superficie = Color(0xFF161B3E)
val Laranja = Color(0xFFF34B00)
val Roxo = Color(0xFF24213E)
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