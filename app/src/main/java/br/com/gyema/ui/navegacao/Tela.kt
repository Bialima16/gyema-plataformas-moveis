package br.com.gyema.ui.navegacao

sealed class Tela(val rota: String) {
    object BoasVindas : Tela("boas_vindas")
    object Login : Tela("login")
    object Recuperacao : Tela("recuperacao")
    object DetalhesExercicio : Tela("detalhes_exercicio")
    object EquipamentoOcupado : Tela("equipamento_ocupado")
    object ResumoTreino : Tela("resumo_treino")
}