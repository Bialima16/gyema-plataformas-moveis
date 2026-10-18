# Gyema — Academia Unifor

Aplicativo acadêmico em Kotlin com Android Views e XML. Abra esta pasta no Android Studio, sincronize o Gradle e execute o módulo `app`. Use Design/Split em `app/src/main/res/layout/` após a sincronização. Não há Compose ou novas dependências.

## Telas e arquivos

- `WelcomeActivity.kt` / `activity_welcome.xml`: entrada, apresentação e acesso ao login.
- `LoginActivity.kt` / `activity_login.xml`: validação de e-mail/senha, mostrar/ocultar senha e lembrar somente o e-mail.
- `PasswordRecoveryActivity.kt` / `activity_password_recovery.xml`: validação de e-mail para futura recuperação.
- Activities em `app/src/main/java/br/com/gyema/`; `AuthUi.kt` reúne validação de e-mail, avisos e insets de teclado/barras.
- Textos, cores e estilos em `res/values/` (incluindo `auth_model.xml`); fundos, ícones e símbolo em `res/drawable/`, com prefixo `model_` nos recursos importados.
- `AndroidManifest.xml` registra as telas e define Boas-vindas como launcher. A MainActivity original e seu XML foram preservados.

As três telas foram adaptadas dos layouts fornecidos em `proj-desenvolvimento--mobile`, seguindo a imagem do protótipo enviada na conversa. Os fundos e o símbolo vetorial desse modelo foram importados com prefixo `model_`; a origem oficial do logotipo não foi confirmada. A recuperação apresenta o cartão roxo sobre uma reprodução inerte e escurecida do Login, em Activity própria para preservar a navegação. A altura fixa de 804dp do modelo foi substituída pela área disponível, com rolagem para telas menores e teclado. A tela de sucesso não foi implementada: não há envio de e-mail, backend, autenticação ou sessão.

## Como testar

1. Abra o app: deve aparecer Boas-vindas. Toque em Entrar; use Voltar da tela e do Android para retornar. Repita com toques rápidos para conferir que não há telas duplicadas.
2. No Login, envie campos vazios, e-mail inválido e senha vazia. Com dados válidos, deve aparecer um aviso de integração pendente, sem autenticar.
3. Digite a senha e teste Mostrar/Ocultar. Marque Lembrar identificação, informe e-mail válido, saia e reabra o Login: apenas o e-mail deve permanecer. Desmarque e repita: o e-mail não deve ser lembrado. A senha não é persistida nem incluída no estado salvo da View.
4. Toque em Esqueceu sua senha. Teste e-mail vazio/inválido/válido. O caso válido informa explicitamente que nenhuma mensagem foi enviada. Voltar retorna ao Login existente.
5. Cadastro, Termos, Privacidade e Suporte TI devem informar recurso pendente.
6. Confira Design/Split e execução em tela pequena, paisagem, fonte ampliada e teclado aberto. Todos os formulários usam ScrollView; os controles têm altura mínima acessível e rótulos associados aos campos.

Configuração preservada: AGP 9.0.1, Gradle 9.3.0, SDK 37, Build Tools 36.0.0, minSdk 26 e bytecode Java 17. O arquivo existente `gradle/gradle-daemon-jvm.properties` seleciona JDK 25, disponível no JBR do Android Studio neste ambiente. Não foi alterado. O AGP pode avisar que foi testado até SDK 36.1.

```powershell
.\gradlew.bat --offline :app:assembleDebug :app:lintDebug
```

## Colaboração

Parta da main atualizada e crie sua branch (`git switch main`, `git pull --ff-only origin main`, `git switch -c feat/nome-da-tela`). Crie uma Activity e XML próprios, registre a Activity no Manifest e use prefixos claros nos recursos para reduzir conflitos. Não versione `local.properties`, caches ou builds.

## Validação desta implementação

- `:app:assembleDebug` passou offline; APK em `app/build/outputs/apk/debug/app-debug.apk`.
- XMLs analisados com parser; verificação estática confirmou um único launcher, três layouts com rolagem, retorno por `finish()` e persistência restrita ao e-mail.
- `:app:lintDebug` não concluiu: `com.android.tools.lint:lint-gradle:32.0.1` ausente no cache offline. Não foram adicionadas dependências nem atualizadas ferramentas.
- Não foram executados testes em dispositivo/emulador ou inspeção visual no Layout Editor. Os fluxos, teclado, fonte ampliada e aparência precisam ser conferidos seguindo o roteiro acima. Nenhum emulador foi aberto automaticamente.
- Permanecem os avisos de compatibilidade SDK 37/AGP e metadados do SDK; a API nativa de ajuste de insets também gera aviso de depreciação na compilação inicial, sem impedir o build.

## Resumo, exercício e equipamento ocupado

Novas telas baseadas na referência visual enviada, sem alterar Boas-vindas/Login:

| Activity | Layout em `app/src/main/res/layout/` |
| --- | --- |
| `WorkoutSummaryActivity` | `activity_workout_summary.xml` |
| `ExerciseDetailsActivity` | `activity_exercise_details.xml` |
| `EquipmentOccupiedActivity` | `activity_equipment_occupied.xml` |

Código em `app/src/main/java/br/com/gyema/`; textos/cores/estilos em `res/values/workout.xml`, seletores em `res/color/` e fundos/ícones em `res/drawable/workout_*`. As telas usam Views nativas e ScrollView com insets das barras do sistema, sem novas bibliotecas. O resumo usa exatamente os dados de demonstração da imagem; não representa um treino registrado.

A foto/vídeo original do supino não foi fornecida como arquivo separado. A área de mídia está identificada como pendente; o player não simula reprodução. A aba Vídeo mantém os cartões da referência; Músculos mostra a musculatura, e Descrição/Histórico indicam as integrações pendentes. A reorganização e o catálogo de alternativas também são pendentes; nenhum treino é alterado e a ocupação não é detectada automaticamente.

Para testar cada tela sem mudar o launcher: selecione a variante **debug**, abra **Run > Edit Configurations > app > Launch Options > Specified Activity** e escolha a Activity desejada. Instale/execute somente no dispositivo ou emulador que você selecionar. Depois restaure **Default Activity** para abrir Boas-vindas. As três Activities são exportadas apenas no Manifest de debug para permitir essa inspeção; em release são internas.

Com um dispositivo já conectado e o APK de debug instalado, também é possível usar:

```sh
adb shell am start -n br.com.gyema/.WorkoutSummaryActivity
adb shell am start -n br.com.gyema/.ExerciseDetailsActivity
adb shell am start -n br.com.gyema/.EquipmentOccupiedActivity
```

Na integração, a tela Meus Treinos deverá abrir o resumo/detalhes por Intent; o resumo e os controles de voltar usam `finish()` para retornar ao chamador. A tela de equipamento pode ser aberta pelo fluxo de treino quando houver informação real de ocupação. Não foi criado um destino fictício para Meus Treinos.

Roteiro: conferir textos e seis linhas do resumo; testar o retorno; alternar as quatro abas e girar a tela para conferir a seleção; tocar no player e nos controles de reorganização/alternativas para conferir os avisos; testar Manter a ordem e Voltar. Conferir os três XMLs em Design/Split, tela pequena, paisagem e fonte ampliada. Não houve validação visual ou execução em dispositivo nesta implementação e nenhum emulador foi iniciado automaticamente.

Validação das três novas telas: `:app:assembleDebug` passou offline. O parser XML e as verificações estáticas de IDs, registro das Activities e launcher passaram. O aviso existente de SDK 37 acima do suporte testado do AGP permanece; não foram alteradas versões ou dependências.
