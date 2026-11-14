# SpaceDrifters · Monitor de salud para astronautas

App Android para el **NASA Space Apps Challenge 2026**, reto *Create Health Monitoring Software for Astronauts on Space Missions*.

> **Prototipo, no es un dispositivo médico · Datos simulados.**

Este repositorio contiene de momento **solo la base del proyecto**: configuración, tema, navegación y pantallas vacías. Las funcionalidades se irán añadiendo encima.

## Requisitos

- **Android Studio** reciente (con soporte para Android Gradle Plugin 9.2).
- **Android SDK 37** instalado (*Settings → Languages & Frameworks → Android SDK*). Se compila contra el 37, aunque la app apunta a Android 36 y funciona desde Android 8.0 (API 26).
- **JDK 21** para Gradle. Android Studio ya trae uno; si compilas desde la terminal, Gradle lo descarga solo si no lo encuentra.

## Abrir el proyecto

1. `git clone` del repositorio.
2. En Android Studio: *File → Open* y elige la carpeta raíz del repositorio (la que contiene `settings.gradle.kts`).
3. Espera a que termine el *Gradle Sync*. Android Studio crea `local.properties` con la ruta de tu SDK; ese archivo no se sube a git.
4. Elige un emulador o un móvil y pulsa ▶ (configuración `app`).

## Compilar y probar desde la terminal

En Windows usa `gradlew.bat` (o `.\gradlew` en PowerShell); en macOS/Linux, `./gradlew`.

| Qué | Comando |
|---|---|
| Compilar el APK de depuración | `./gradlew assembleDebug` |
| Instalar en el emulador o móvil conectado | `./gradlew installDebug` |
| Tests unitarios (JVM, rápidos) | `./gradlew testDebugUnitTest` |
| Un solo test | `./gradlew testDebugUnitTest --tests "*.ExampleUnitTest"` |
| Tests instrumentados (necesitan emulador o móvil) | `./gradlew connectedDebugAndroidTest` |
| Lint | `./gradlew lintDebug` (informe en `app/build/reports/`) |

Antes de subir cambios, comprueba al menos `./gradlew assembleDebug testDebugUnitTest`.

## Tecnologías

- **Kotlin** + **Jetpack Compose** (Material 3), una sola Activity.
- La lógica pura de las pruebas cognitivas (`domain/cognitivo/`) está en **Java**, con sus tests JUnit en Java. Las pantallas siguen en Kotlin porque Compose solo funciona con Kotlin.
- **Navigation Compose** con rutas tipadas (`@Serializable`).
- **MVVM**: `ViewModel` + `StateFlow`. Inyección de dependencias **manual**, sin Hilt.
- **Room** (con KSP) para guardar datos en local. Sin backend ni red: la app funciona sin conexión.
- **kotlinx.serialization** para leer JSON (datos de ejemplo y reglas en `assets/`).

Las versiones de todas las librerías están en **`gradle/libs.versions.toml`**. Para añadir una librería, declárala ahí y úsala en `app/build.gradle.kts` como `libs.<nombre>`. Los repositorios (google, mavenCentral) solo se declaran en `settings.gradle.kts`.

## Estructura

Todo el código está en `app/src/main/java/com/example/nasa_spaceappchallenge2026_spacedrifters/`:

```
├── MainActivity.kt          Única Activity: aplica el tema y monta la navegación
├── SpaceDriftersApp.kt      Application: crea el AppContainer
├── di/
│   ├── AppContainer.kt      Crea una sola vez la base de datos, los repositorios, etc.
│   └── AppViewModelProvider.kt  Fábrica de todos los ViewModels
├── data/
│   ├── local/               Room: entidades, DAOs y AppDatabase   (vacío)
│   └── repository/          Repositorios: la UI solo habla con ellos (vacío)
├── domain/
│   └── model/               Modelos y lógica pura sin Android (motor de reglas, etc.) (vacío)
└── ui/
    ├── theme/               Colores, tipografía y tema (Color.kt, Type.kt, Theme.kt)
    ├── navigation/          Rutas.kt (rutas) y AppNavHost.kt (grafo de navegación)
    ├── components/          Piezas reutilizables: PantallaBase, AvisoPrototipo...
    └── screens/
        ├── rol/             Selector de rol (pantalla inicial)
        ├── astronauta/      Pantallas del astronauta
        └── medico/          Pantallas del médico de vuelo
```

Otros sitios importantes:

- `app/src/main/assets/`: JSON de configuración y datos. `config_mision.json` tiene el retraso con la Tierra y los umbrales de Vigilar/Alerta (en desviaciones típicas): cámbialos ahí, no en el código. Los datos de ejemplo y las reglas irán también aquí.
- `app/src/main/res/values/strings.xml`: textos en **castellano** (idioma por defecto).
- `app/src/main/res/values-en/strings.xml`: los mismos textos en **inglés**, para la entrega a la NASA.
- `app/src/test/`: tests unitarios (JVM). `app/src/androidTest/`: tests en dispositivo.

Las carpetas vacías tienen un `.gitkeep` para que existan en git; bórralo cuando metas el primer archivo.

## Dónde añadir cosas nuevas

### Una pantalla nueva

1. Crea `ui/screens/<zona>/MiPantallaScreen.kt`. Envuelve el contenido en `PantallaBase(titulo = ..., onAtras = ...)`, que ya pone la barra superior y el aviso de prototipo.
2. Declara su ruta en `ui/navigation/Rutas.kt`:
   ```kotlin
   @Serializable data object RutaMiPantalla
   // con argumentos:
   @Serializable data class RutaPerfil(val astronautaId: String)
   ```
3. Regístrala en `ui/navigation/AppNavHost.kt`:
   ```kotlin
   composable<RutaPerfil> { entrada ->
       val ruta = entrada.toRoute<RutaPerfil>()
       PerfilScreen(astronautaId = ruta.astronautaId, onAtras = { navController.popBackStack() })
   }
   ```
   Las pantallas no reciben el `NavController`, solo lambdas (`onAtras`, `onAbrirPerfil`...).

### Un ViewModel

1. Créalo junto a su pantalla (`ui/screens/<zona>/MiPantallaViewModel.kt`) y expón el estado como `StateFlow`.
2. Regístralo en `di/AppViewModelProvider.kt`:
   ```kotlin
   initializer { MiPantallaViewModel(app().container.miRepositorio) }
   ```
3. En la pantalla: `viewModel<MiPantallaViewModel>(factory = AppViewModelProvider.Factory)` y `collectAsStateWithLifecycle()`.

### Datos (Room)

1. Entidades y DAOs en `data/local/`, y la clase `AppDatabase` también ahí.
2. Un repositorio en `data/repository/` que use los DAOs.
3. Crea la base de datos y el repositorio en `di/AppContainer.kt` con `by lazy`.

### Textos

Nunca escribas texto visible directamente en el código: añádelo a `values/strings.xml` **y** a `values-en/strings.xml`, y úsalo con `stringResource(R.string.…)`.

### Colores y estilos

Usa `MaterialTheme.colorScheme` y `MaterialTheme.typography`. Para los estados (Normal, Vigilar, Alerta, Sin datos) usa `LocalEstadoColores.current`. Para cambiar la paleta, edita solo `ui/theme/Color.kt`.

**Tipografía** (`ui/theme/Type.kt`): las fuentes van dentro de la app (`res/font`) para que funcione sin conexión. **Space Grotesk** en títulos, **Inter** en el texto y **JetBrains Mono** en `labelMedium` (cifras y cuentas atrás). No pongas fuentes a mano en un `Text`: elige el estilo de `MaterialTheme.typography`. Sus licencias (SIL OFL) están en `assets/licencias/` y tienen que ir siempre con las fuentes.

**Logo e icono**: el icono de la app es vectorial y adaptativo (`drawable/ic_launcher_*.xml`). Para usar el logo dentro de una pantalla, usa `drawable/ic_logo.xml`. Los colores del icono están en `res/values/colors.xml` y son los mismos que los de `Color.kt`.

**Pantallas sin barra superior**: `PantallaBase(titulo = null)` quita la barra (para pantallas con su propia cabecera, como el selector de rol). El aviso de prototipo sigue saliendo.

## Reglas de diseño que todos seguimos

- Tema oscuro tipo control de misión (paleta en `ui/theme/Color.kt`).
- Los estados **nunca** se distinguen solo por el color: siempre llevan texto (Normal, Vigilar, Alerta, Sin datos). Un dato que falta se muestra como **Sin datos**, nunca como "bien".
- Botones y zonas táctiles de **48 dp** como mínimo.
- Todos los iconos llevan `contentDescription` (texto en `strings.xml`).
- El aviso "Prototipo, no es un dispositivo médico · Datos simulados" siempre visible (lo pone `PantallaBase`).
