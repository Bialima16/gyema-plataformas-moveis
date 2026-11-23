package br.com.gyema.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.tema.Laranja
import br.com.gyema.ui.tema.Superficie
import br.com.gyema.ui.tema.TextoSecundario
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Estados comuns das telas que leem o banco: carregando, erro e lista vazia.

@Composable
fun EstadoCarregando(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Laranja)
    }
}

@Composable
fun EstadoErro(mensagem: String, onTentarNovamente: () -> Unit, modifier: Modifier = Modifier) {
    EstadoMensagem(
        icone = Icons.Outlined.Warning,
        titulo = "Não foi possível carregar",
        texto = mensagem,
        modifier = modifier
    ) {
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onTentarNovamente,
            colors = ButtonDefaults.buttonColors(containerColor = Laranja),
            shape = RoundedCornerShape(10.dp)
        ) { Text("Tentar novamente", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun EstadoVazio(titulo: String, texto: String, modifier: Modifier = Modifier) {
    EstadoMensagem(Icons.Outlined.Inbox, titulo, texto, modifier)
}

@Composable
private fun EstadoMensagem(
    icone: ImageVector,
    titulo: String,
    texto: String,
    modifier: Modifier = Modifier,
    extra: @Composable () -> Unit = {}
) {
    Column(
        modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(64.dp).background(Superficie, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) { Icon(icone, null, tint = Laranja, modifier = Modifier.size(30.dp)) }
        Spacer(Modifier.height(16.dp))
        Text(titulo, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(texto, color = TextoSecundario, fontSize = 13.sp, textAlign = TextAlign.Center)
        extra()
    }
}

/** "Ana Lima Souza" → "AS". */
fun iniciais(nome: String): String {
    val partes = nome.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        partes.isEmpty() -> "?"
        partes.size == 1 -> partes[0].take(2).uppercase()
        else -> "${partes.first().first()}${partes.last().first()}".uppercase()
    }
}

/** Converte uma data do banco (ISO, em UTC) para o fuso do aparelho no [padrao] pedido, ex.: "dd/MM". */
fun formatarData(iso: String, padrao: String = "dd/MM/yyyy"): String =
    runCatching {
        OffsetDateTime.parse(iso).atZoneSameInstant(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(padrao))
    }.getOrDefault("")
