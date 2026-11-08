# Banco de dados do Gyema (Supabase)

O app usa o **Supabase**, que oferece um banco PostgreSQL, login por e-mail e API prontos. Tudo o que o banco precisa está nesta pasta:

| Arquivo | O que faz |
|---|---|
| `01_schema.sql` | Cria as tabelas, os gatilhos (cadastro automático do perfil, ocupação dos equipamentos) e as views usadas pelas telas |
| `02_seguranca_rls.sql` | Cria as regras de acesso: o aluno vê só os próprios dados e o instrutor gerencia a academia |
| `03_dados_iniciais.sql` | Insere as perguntas do teste de prontidão, os setores, os exercícios e os equipamentos que hoje aparecem nas telas |
| `04_ajustes_seguranca.sql` | Bloqueia a chamada direta das funções internas pela API (ajustes pedidos pelo Security Advisor) |

---

## 1. Criar o projeto no Supabase (só uma pessoa do grupo faz)

1. Acesse <https://supabase.com>, entre com o GitHub e clique em **New project**.
2. Nome: `gyema`. Região: **South America (São Paulo)**. Crie uma senha do banco e guarde-a.
3. Espere o projeto terminar de ser criado (uns 2 minutos).

## 2. Criar as tabelas

No menu lateral, abra **SQL Editor → New query**. Rode os três arquivos **nesta ordem**, colando um por vez e clicando em **Run**:

1. `01_schema.sql`
2. `02_seguranca_rls.sql`
3. `03_dados_iniciais.sql`
4. `04_ajustes_seguranca.sql`

Cada um deve terminar com *Success. No rows returned*. Para conferir, abra **Table Editor**: as tabelas `perfis`, `equipamentos`, `fichas_treino` e as outras devem aparecer.

> Se precisar recomeçar do zero, rode `00_resetar.sql` e depois os quatro arquivos de novo. As contas de usuário existentes não são apagadas, mas ficam sem perfil. Exclua essas contas em **Authentication → Users** e cadastre de novo.

## 3. Configurar o login

Em **Authentication → Sign In / Providers → Email**:

- **Durante o desenvolvimento**, desligue **Confirm email**. Assim o cadastro já entra direto no app, sem precisar abrir o e-mail.
- Se religar na apresentação final, o `signUpWith` termina **sem sessão** (`currentUserOrNull()` fica `null`). A tela de cadastro deve então avisar "confirme seu e-mail" e voltar para o login.

> O servidor de e-mail gratuito do Supabase envia poucos e-mails por hora. Se a recuperação de senha parar de chegar, espere um pouco.
>
> **Recuperar senha:** o `resetPasswordForEmail` envia o e-mail, mas o link abre no navegador (a *Site URL* do projeto). Para a apresentação basta mostrar o e-mail chegando. Para fechar o fluxo dentro do app, é preciso configurar um deep link (`gyema://`) em **Authentication → URL Configuration** e criar uma tela de "nova senha".

## 4. Conectar o app (cada pessoa do grupo faz no seu computador)

1. No Supabase, abra **Project Settings → API Keys** e copie a **Publishable key** (`sb_publishable_...`). Se o projeto mostrar só as chaves antigas, clique em **Create new API keys**. Copie também a **Project URL** (fica em **Project Settings → Data API**, no formato `https://xxxx.supabase.co`).
2. No Android Studio, abra o arquivo **`local.properties`** na raiz do projeto (ele já existe e tem o `sdk.dir`) e acrescente:

   ```properties
   SUPABASE_URL=https://xxxx.supabase.co
   SUPABASE_KEY=sb_publishable_xxxxxxxxxxxxxxxx
   ```
3. Faça **File → Sync Project with Gradle Files** e rode o app.

> O `local.properties` está no `.gitignore` e **não vai para o GitHub**. Mande a URL e a chave para o grupo por mensagem privada.
> **Nunca** coloque a *secret key* (`sb_secret_...`) nem a `service_role` no app: elas ignoram todas as regras de segurança.

Sem essas duas linhas o app continua compilando e abrindo normalmente. Só as funções que usam o banco vão avisar que ele não está configurado.

## 5. Testar a conexão

1. Abra o emulador.
2. No Android Studio, abra `app/src/androidTest/java/br/com/gyema/data/ConexaoSupabaseTest.kt`.
3. Clique com o botão direito no arquivo → **Run 'ConexaoSupabaseTest'**.

Os dois testes devem ficar verdes:

| Teste | Se falhar |
|---|---|
| `localPropertiesConfigurado` | Faltam as duas linhas no `local.properties`, ou o Gradle não foi sincronizado depois |
| `bancoRespondeComAsPerguntasDeProntidao` | Confira a URL e a chave, a internet do emulador e se os scripts 01 a 04 foram rodados |

### Criar um instrutor

Depois que a conta for criada pelo app, rode no **SQL Editor**:

```sql
update public.perfis set perfil = 'instrutor' where email = 'email.do.instrutor@unifor.br';
```

---

## Para quem vai programar login, cadastro e as telas

Toda a comunicação com o banco passa por **`Supabase.cliente`**, no arquivo `app/src/main/java/br/com/gyema/data/SupabaseConfig.kt`. Não crie outro cliente.

### Login, cadastro, recuperar senha e sair

```kotlin
import br.com.gyema.data.Supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

// Login
Supabase.cliente.auth.signInWith(Email) { email = "aluno@unifor.br"; password = "senha" }

// Quem está logado (null = ninguém)
val id = Supabase.cliente.auth.currentUserOrNull()?.id

// Recuperar senha
Supabase.cliente.auth.resetPasswordForEmail("aluno@unifor.br")

// Sair
Supabase.cliente.auth.signOut()
```

**Cadastro:** envie os dados dos 3 passos dentro de `data`. O banco cria sozinho a linha em `perfis` e as respostas do teste em `respostas_prontidao`, então não é preciso inserir nada nessas tabelas depois.

```kotlin
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

Supabase.cliente.auth.signUpWith(Email) {
    email = "aluno@unifor.br"
    password = "Senha@123"
    data = buildJsonObject {
        put("nome", "Ana Lima")
        put("cpf", "12345678900")            // só números
        put("matricula", "2412345")
        put("data_nascimento", "1995-06-27") // AAAA-MM-DD (converta o 27/06/1995 da tela)
        put("genero", "Feminino")
        put("peso_kg", "62.5")               // ponto, não vírgula
        put("altura_cm", "165")              // em centímetros
        put("contato_emergencia", "85999990000")
        putJsonObject("prontidao") {         // id da pergunta → resposta
            put("1", true)
            put("2", false)
        }
    }
}
```

Campos vazios ou em formato errado são ignorados, sem quebrar o cadastro. Toda conta nasce como **aluno**.

### Ler e gravar nas tabelas

Crie uma `data class` com `@Serializable` com as colunas que a tela usa, com os mesmos nomes do banco ou usando `@SerialName`:

```kotlin
@Serializable
data class Equipamento(
    val id: Int,
    val codigo: String,
    val nome: String,
    @SerialName("setor_nome") val setorNome: String,
    val status: String // "disponivel", "ocupado" ou "manutencao"
)

// Numa tela Compose:
var lista by remember { mutableStateOf<List<Equipamento>>(emptyList()) }
LaunchedEffect(Unit) {
    lista = Supabase.cliente.from("equipamentos_detalhados").select().decodeList<Equipamento>()
}
```

Para gravar, use `Supabase.cliente.from("tabela").insert(objeto)`. Para atualizar, `.update({ set("coluna", valor) }) { filter { eq("id", id) } }`. As funções são `suspend`: dentro de um botão, use `rememberCoroutineScope().launch { ... }`.

### Qual tabela ou view cada tela usa

| Tela | Tabela / view |
|---|---|
| Academia agora / Mapa da academia | `ocupacao_setores` (status de cada zona e contagens) |
| Equipamentos / Detalhes do equipamento | `equipamentos_detalhados`, `equipamento_exercicios`, `exercicios` |
| QR Code / Registro de utilização / Em utilização | `utilizacoes`: inserir `equipamento_id` e `usuario_id` para iniciar; preencher `fim` para finalizar. O banco calcula o `fim_previsto` |
| Reportar problema / Relatos (instrutor) | `relatos_problema` |
| Cadastrar / gerenciar equipamento (instrutor) | `equipamentos`, `setores` |
| Meus treinos / Detalhes do treino | `fichas_treino`, `itens_ficha` |
| Execução / Resumo | `execucoes_treino`, `series_executadas` |
| Evolução | `evolucao_carga_mensal`, `execucoes_treino` |
| Lista e detalhes de alunos / Nova ficha (instrutor) | `perfis` (filtre `perfil = aluno`), `fichas_treino`, `itens_ficha` |
| Comunidade / Publicação | `feed_posts` (já traz autor e contagens), `posts`, `curtidas`, `comentarios` |
| Grupos / Criar grupo | `grupos`, `membros_grupo` |
| Perfil | `perfis` (o aluno só enxerga a própria linha) |
| Nome de outro usuário | `perfis_publicos` (só nome, curso e perfil) |

O diagrama no fim desta página mostra como as tabelas se ligam. Em caso de dúvida sobre uma coluna, veja `01_schema.sql` ou o **Table Editor** do Supabase.

## Regras que o próprio banco garante

- Um aluno **não consegue** virar instrutor, ver dados de outro aluno nem editar equipamentos, nem que tente pelo código.
- Um equipamento em **manutenção** ou já **ocupado** recusa um novo uso. A mensagem de erro vem pronta ("Equipamento Supino Reto está ocupado.").
- Cada aluno usa **um equipamento por vez**. O uso termina sozinho depois do tempo máximo do equipamento.
- O status do equipamento (disponível/ocupado) é atualizado **automaticamente** quando alguém inicia ou finaliza um uso.
- **Dados de saúde e CPF** (teste de prontidão) são visíveis só para o próprio aluno e para os instrutores. Comunidade e grupos mostram apenas nome e curso.

> **Avisos esperados no Security Advisor:** as views `perfis_publicos` e `feed_posts` aparecem como *Security Definer View*. Isso é proposital: elas mostram o nome do autor de um post sem liberar a tabela `perfis` inteira, expõem só nome, curso e perfil e são bloqueadas para quem não está logado. As funções `eh_instrutor`, `eh_membro` e `encerrar_utilizacoes_expiradas` também aparecem, porque as regras de acesso e o app precisam delas.

## Diagrama das tabelas

```
auth.users ──1:1── perfis ──< respostas_prontidao >── perguntas_prontidao
                     │
                     ├──< utilizacoes >── equipamentos >── setores
                     │                        │
                     ├──< relatos_problema >──┘
                     │                        └──< equipamento_exercicios >── exercicios
                     │
                     ├──< fichas_treino (aluno_id / instrutor_id) ──< itens_ficha >── exercicios
                     ├──< execucoes_treino ──< series_executadas >── exercicios
                     │
                     ├──< posts ──< curtidas / comentarios
                     └──< grupos ──< membros_grupo
```
