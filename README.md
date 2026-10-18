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
