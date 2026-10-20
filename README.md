# Gyema

App de academia (Android, Jetpack Compose), projeto da disciplina de Desenvolvimento de plataformas móveis.

## Requisitos

- Android Studio atualizado
- **JDK 17** (use o jbr-17 embutido do Android Studio)
- Android SDK 35

## Versões do projeto

| Item | Versão |
|---|---|
| Android Gradle Plugin (AGP) | 8.6.0 |
| Gradle (wrapper) | 8.9 |
| Kotlin e plugin do Compose | 2.0.21 |
| Compose BOM | 2024.10.01 |
| compileSdk / targetSdk | 35 |
| minSdk | 26 |

Essas versões já estão nos arquivos do repositório. Não altere sem avisar o grupo.

## Como rodar

1. Clone o repositório e abra a pasta no Android Studio.
2. Em **Settings → Build, Execution, Deployment → Build Tools → Gradle**, selecione **jbr-17** em *Gradle JDK*.
3. Faça **File → Sync Project with Gradle Files**.
4. Crie um emulador de celular comum (sugestão: **Pixel 8**, API 34 ou 35) no Device Manager e rode o app.

> Evite o Pixel Fold e tablets: o design foi feito para celular e a tela fica esticada.

## Visualizando as telas sem rodar o app

Cada tela tem uma função `@Preview` no final do arquivo. Faça o build (**Ctrl+F9**), abra o arquivo e clique em **Split** ou **Design** no canto superior direito do editor.

## Estrutura de pastas

```
br.com.gyema
├── MainActivity.kt          # NavHost e barra inferior
└── ui
    ├── components           # Componentes compartilhados (cards, botões, barra inferior)
    ├── navegacao            # Rotas (Tela.kt)
    ├── telas                # Uma tela por arquivo (Tela_NomeDaTela.kt)
    └── tema                 # Cores, tipografia e GyemaTheme
```

## Convenções do grupo

- **Branches:** uma por participante, contendo apenas as telas dessa pessoa (`telas-nome`).
- **Commits:** padrão Conventional Commits, por exemplo `feat(telas): adiciona tela X`.
- **Autor do commit:** confira antes de commitar em computador compartilhado:
```
  git config user.name
  git config user.email
```
- Não commite `build/`, `.gradle/`, `.idea/` nem `local.properties`.

## Problemas comuns

| Erro | Causa e solução |
|---|---|
| `Unsupported class file major version 69` | O Gradle está rodando com um JDK novo demais (25). Troque o *Gradle JDK* para **jbr-17** em Settings → Build Tools → Gradle. |
| `Inconsistent JVM-target compatibility ... (17) and (21)` | O Kotlin estava usando o JDK da máquina. O `jvmTarget` já está fixado em 17 no `build.gradle.kts` do módulo `:app`. Faça um `git pull` e sincronize. |
| Erro em todos os arquivos após o Sync | Verifique se o `gradle-wrapper.properties` aponta para o **Gradle 8.9**. O Gradle 9 não é compatível com o AGP 8.6.0. |
| `android.useAndroidX` não habilitado | Confirme que `android.useAndroidX=true` está no `gradle.properties`. |
| Textos pretos no Preview | O preview precisa estar envolvido em `GyemaTheme { ... }`. |
| Preview não aparece | Faça o build (Ctrl+F9) e clique em Split ou Design. |
