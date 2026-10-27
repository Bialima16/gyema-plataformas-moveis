package br.com.gyema.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.gyema.ui.tema.*

@Composable
fun TituloGyema(subtitulo: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("GYEMA", fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.White)
        Text("ACADEMIA UNIFOR", fontSize = 11.sp, color = TextoSuave)
        Spacer(Modifier.height(12.dp))
        Text(subtitulo, fontSize = 19.sp, color = Color(0xFFF4A16D), fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center)
    }
}

@Composable
fun BotaoGyema(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, secundario: Boolean = false) {
    Button(onClick = onClick, modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (secundario) Superficie else Laranja,
            contentColor = Color.White
        )) { Text(texto, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
}

@Composable
fun AvisoPendente(mensagem: String, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Integração pendente") },
        text = { Text(mensagem) }, confirmButton = {
            TextButton(onClick = onDismiss) { Text("Entendi") }
        })
}

@Composable
fun CabecalhoVoltar(titulo: String, onVoltar: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = onVoltar) { Text("‹ Voltar") }
        Text(titulo, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}