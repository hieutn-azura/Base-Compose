# Base Compose

A production-ready Android base project built with Jetpack Compose. Clone this repo whenever you start a new app — the architecture, DI, theming, preferences, and screens are already wired up.

---

## Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| DI | Koin 4.1.0 |
| Local Storage | `SharePreference` (typed delegates) + Room 2.7 |
| Image Loading | Coil 3 |
| Responsive Sizing | SDP / SSP (Intuit) |
| Networking | Retrofit + OkHttp + Gson |
| Firebase | Analytics · Remote Config · FCM |
| Navigation | Navigation Compose |
| Language | Kotlin 2.2 · KSP · JVM 17 |
| Build | AGP 9 · Gradle Version Catalog |

---

## Project Structure

```
app/src/main/java/com/hdt/basecompose/
├── app/
│   ├── AppProjectApplication.kt   ← Koin init
│   ├── IModule.kt                 ← DI modules (add ViewModels here)
│   ├── SharePreference.kt         ← Typed SharedPreferences wrapper
│   ├── PreferenceData.kt          ← Typed preference keys
│   └── RemoteConfig.kt            ← Firebase Remote Config wrapper
│
├── base/
│   ├── BaseActivity.kt            ← Abstract Compose activity (extend this)
│   ├── BaseDialog.kt              ← Material3 dialog composable
│   ├── ApiVersion.kt              ← API level helpers
│   ├── BundleExt.kt               ← Parcelable / Serializable bundle extensions
│   └── WindowExt.kt               ← Screen-on, fullscreen helpers
│
├── style/
│   ├── AppViewTheme.kt            ← MaterialTheme wrapper (light + dark)
│   ├── Color.kt                   ← Full color palette + opacity/adjustLevel utils
│   ├── ColorApp.kt                ← Semantic color aliases (AppColors object)
│   ├── AppTypography.kt           ← Full Material3 type scale
│   ├── Dimen.kt                   ← sdp() / ssp() Compose extensions
│   ├── ExtensionComponent.kt      ← Shimmer, gradients, dot indicator, borders
│   ├── ExtensionText.kt           ← TextView, InputValue, string validators
│   ├── ExtensionSize.kt           ← SpaceH/W/F, widthPercent, screen size queries
│   ├── State.kt                   ← UiState<T> sealed class
│   └── DevicePreviews.kt          ← Multi-device @Preview annotation
│
├── ui/
│   ├── splash/SplashActivity.kt   ← Launcher (2 s delay → Language or Main)
│   └── language/
│       ├── LanguageActivity.kt    ← Language picker (30 languages)
│       └── Language.kt            ← LanguageItem data class + list
│
├── widget/
│   └── LoadingOverlay.kt          ← Full-screen loading overlay composable
│
└── MainActivity.kt                ← Main shell — add your NavHost here
```

---

## App Flow

```
SplashActivity  (launcher)
    │
    ├─ first launch ──→ LanguageActivity ──→ MainActivity
    │
    └─ returning user ────────────────────→ MainActivity
```

---

## Starting a New Project from This Base

### 1. Clone

```bash
git clone https://github.com/hieu98/Base-Compose.git MyNewApp
cd MyNewApp
```

### 2. Rename the package

In Android Studio: right-click `com.hdt.basecompose` → **Refactor → Rename** → enter your package (e.g. `com.example.myapp`).

Then update `AndroidManifest.xml` namespace if it wasn't caught automatically.

### 3. Update application ID

Open `app/build.gradle.kts` and change both flavor `applicationId` values:

```kotlin
defaultConfig {
    applicationId = "com.example.myapp"   // ← your package
}
productFlavors {
    create("dev") {
        applicationIdSuffix = ".dev"       // → com.example.myapp.dev
    }
}
```

### 4. Replace Firebase config

1. Go to [Firebase Console](https://console.firebase.google.com/) → your project → Add Android app.
2. Register the package names `com.example.myapp` and `com.example.myapp.dev`.
3. Download `google-services.json` and replace `app/google-services.json`.

> If you are not using Firebase, remove the `google.services` plugin from both `build.gradle.kts` files and delete the Firebase dependencies in `app/build.gradle.kts`.

### 5. Set the app name

```xml
<!-- app/src/main/res/values/strings.xml -->
<string name="app_name">My New App</string>
```

### 6. Replace launcher icons

Drop your icons into `app/src/main/res/mipmap-*/`.

### 7. Set your brand colors

Open `style/ColorApp.kt` and change the semantic aliases:

```kotlin
object AppColors {
    val Primary          = Blue600    // ← change to your brand color
    val PrimaryContainer = Blue100
    val Secondary        = Purple600
    // ...
}
```

Changes here automatically apply to the light **and** dark themes in `AppViewTheme.kt`.

---

## Common Tasks

### Add a new screen

1. Create `ui/myfeature/MyFeatureActivity.kt` extending `BaseActivity`:

```kotlin
class MyFeatureActivity : BaseActivity() {
    @Composable
    override fun Content() {
        // your composable here
    }
}
```

2. Register it in `AndroidManifest.xml`:

```xml
<activity android:name=".ui.myfeature.MyFeatureActivity" android:exported="false" />
```

3. Navigate to it from any other `BaseActivity`:

```kotlin
startActivity<MyFeatureActivity>()

// with data:
startActivity<MyFeatureActivity> { putExtra("key", value) }

// navigate and close current screen:
startActivityAndFinish<MyFeatureActivity>()
```

---

### Add a ViewModel

1. Create the ViewModel class:

```kotlin
class MyFeatureViewModel(private val prefs: PreferenceData) : ViewModel() {
    // ...
}
```

2. Register it in `app/IModule.kt`:

```kotlin
val viewModelModule = module {
    viewModel { MyFeatureViewModel(get()) }
}
```

3. Inject it in your activity:

```kotlin
class MyFeatureActivity : BaseActivity() {
    private val viewModel: MyFeatureViewModel by viewModel()
}
```

---

### Add a preference key

Open `app/PreferenceData.kt` and add a delegated property:

```kotlin
class PreferenceData(private val preference: SharePreference) {
    var myFlag: Boolean by preference.boolean("my_flag_key", false)
    var userId: String  by preference.string("user_id", "")
}
```

Reads and writes are automatic. For reactive access:

```kotlin
preference.flowOf("user_id").collect { value -> /* ... */ }
```

---

### Add a Room database

1. Create your entity and DAO:

```kotlin
@Entity(tableName = "items")
data class ItemEntity(@PrimaryKey val id: Int, val name: String)

@Dao
interface ItemDao {
    @Query("SELECT * FROM items") fun getAll(): Flow<List<ItemEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: ItemEntity)
}
```

2. Create the database class:

```kotlin
@Database(entities = [ItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
```

3. Provide it in `app/IModule.kt`:

```kotlin
val dataModule = module {
    single { SharePreference(androidContext()) }
    single { PreferenceData(get()) }
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app.db").build() }
    single { get<AppDatabase>().itemDao() }
}
```

---

### Use scalable dimensions (SDP / SSP)

Always use `sdp()` and `ssp()` instead of raw `dp` / `sp` so layouts scale correctly across screen sizes:

```kotlin
// Instead of:   Modifier.padding(16.dp)
// Write:
Modifier.padding(16.sdp())

// Instead of:   fontSize = 14.sp
// Write:
fontSize = 14.ssp()
```

---

### Use UiState in a ViewModel

```kotlin
class MyViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val state: StateFlow<UiState<List<String>>> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                UiState.Success(fetchData())
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
```

Collect in your composable:

```kotlin
val state by viewModel.state.collectAsStateWithLifecycle()

when (state) {
    is UiState.Loading -> LoadingOverlay(isLoading = true) {}
    is UiState.Success -> MyList(data = state.data)
    is UiState.Error   -> ErrorMessage(message = state.message)
    is UiState.Empty   -> EmptyScreen()
}
```

---

### Firebase Remote Config

Add your key constant in `app/RemoteConfig.kt`:

```kotlin
const val FEATURE_NEW_ONBOARDING = "feature_new_onboarding"
```

Fetch and read:

```kotlin
RemoteConfig.fetch { success ->
    val enabled = RemoteConfig.getBoolean(RemoteConfig.FEATURE_NEW_ONBOARDING)
}
```

---

## Build Variants

| Variant | Application ID | Use for |
|---|---|---|
| `devDebug` | `com.example.myapp.dev` | Daily development |
| `devRelease` | `com.example.myapp.dev` | QA / internal testing |
| `productRelease` | `com.example.myapp` | Production release |

---

## New Project Checklist

- [ ] Rename package from `com.hdt.basecompose` to your package
- [ ] Update `applicationId` in `app/build.gradle.kts`
- [ ] Replace `app/google-services.json` with your Firebase file
- [ ] Set app name in `res/values/strings.xml`
- [ ] Replace launcher icons in `res/mipmap-*/`
- [ ] Define brand colors in `style/ColorApp.kt`
- [ ] Add signing config to the release build type in `app/build.gradle.kts`
- [ ] Register new activities in `AndroidManifest.xml`
