package br.com.gyema.ui.telas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FundoChat = Color(0xFF0B1030)
private val BalaoChat = Color(0xFF171D40)
private val LaranjaChat = Color(0xFFF34B00)
private val MutadoChat = Color(0xFFA8A9C2)

@Composable
fun TelaChatbot(onVoltar: () -> Unit) {
    var pergunta by rememberSaveable { mutableStateOf("") }
    var resposta by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().background(FundoChat)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.dp).background(BalaoChat, RoundedCornerShape(9.dp))
                .clickable(onClick = onVoltar), contentAlignment = Alignment.Center) {
                Text("‹", color = Color.White, fontSize = 26.sp)
            }
            Spacer(Modifier.width(14.dp))
            Text("Assistente Gyema", color = Color.White, fontSize = 18.sp,
                fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)) {
            Box(Modifier.fillMaxWidth().background(BalaoChat, RoundedCornerShape(14.dp))
                .padding(15.dp)) {
                Text("Oi! Sou um assistente informativo do Gyema — posso ajudar com dúvidas sobre o app e a academia, mas não substituo seu instrutor.",
                    color = Color.White, fontSize = 13.sp, lineHeight = 19.sp)
            }
            Spacer(Modifier.height(7.dp))
            Text("14:32", color = MutadoChat, fontSize = 10.sp,
                modifier = Modifier.align(Alignment.End))
            Spacer(Modifier.height(24.dp))
            Text("PERGUNTAS FREQUENTES", color = MutadoChat, fontSize = 11.sp,
                fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Horários da academia", "Como funciona o app?", "Meu treino").forEach { titulo ->
                    Box(Modifier.background(BalaoChat, RoundedCornerShape(20.dp))
                        .clickable { pergunta = titulo }.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        Text(titulo, color = LaranjaChat, fontSize = 10.sp)
                    }
                }
            }
            if (resposta.isNotBlank()) {
                Spacer(Modifier.height(24.dp))
                Box(Modifier.fillMaxWidth().background(BalaoChat, RoundedCornerShape(12.dp))
                    .padding(14.dp)) {
                    Text(resposta, color = Color.White, fontSize = 13.sp)
                }
            }
        }
        Row(Modifier.fillMaxWidth().background(BalaoChat).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).background(FundoChat, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 13.dp)) {
                BasicTextField(value = pergunta, onValueChange = { pergunta = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                    cursorBrush = SolidColor(LaranjaChat),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (pergunta.isNotBlank()) {
                            resposta = "O assistente ainda depende da integração com o serviço de mensagens."
                            pergunta = ""
                        }
                    }),
                    decorationBox = { inner ->
                        Box {
                            if (pergunta.isBlank()) Text("Digite sua pergunta...",
                                color = MutadoChat, fontSize = 13.sp)
                            inner()
                        }
                    })
            }
            Spacer(Modifier.width(8.dp))
            Box(Modifier.size(38.dp).background(LaranjaChat, CircleShape)
                .clickable {
                    if (pergunta.isNotBlank()) {
                        resposta = "O assistente ainda depende da integração com o serviço de mensagens."
                        pergunta = ""
                    }
                }, contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar",
                    tint = Color.White, modifier = Modifier.size(19.dp))
            }
        }
    }
}
