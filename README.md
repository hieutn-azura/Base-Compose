# Base Compose

A production-ready Android base project built with Jetpack Compose. Includes a full **UFO first-run flow** (Splash → Language → Onboarding → Feature), Firebase Remote Config, and AzAds native/interstitial/banner/reward ad infrastructure.

---

## Tech Stack

| Layer | Library | Version |
|---|---|---|
| UI | Jetpack Compose + Material 3 | BOM 2026.06 |
| Language | Kotlin | 2.4.0 |
| DI | Koin | 4.2.2 |
| Ads | AzAds (azmoduleads) | 1.2.6 |
| Ad Mediation | Facebook · Mintegral | — |
| Local Storage | `SharePreference` (typed delegates) + Room | 2.8.4 |
| Image Loading | Coil 3 | 3.5.0 |
| Responsive Sizing (XML) | SDP / SSP (Intuit) | 1.1.1 |
| Networking | Retrofit 3 + OkHttp 5 + Gson | — |
| Firebase | Analytics · Remote Config · FCM | BOM 34.15 |
| Navigation | Navigation Compose | 2.9.8 |
| Build | AGP 9.2 · Gradle Version Catalog · KSP | — |

---

## Project Structure

```
app/src/main/java/com/hdt/basecompose/
│
├── app/
│   ├── AppProjectApplication.kt   ← extends AdsMultiDexApplication; Koin + Firebase + AzAds init
│   ├── IModule.kt                 ← dataModule + viewModelModule (add ViewModels here)
│   ├── SharePreference.kt         ← Typed SharedPreferences wrapper (delegates)
│   ├── PreferenceData.kt          ← All preference keys: languageCode, isFinishFirstFlow,
│   │                                 countSessionApp, firstOpenApp
│   └── RemoteConfig.kt            ← Simple Firebase Remote Config wrapper
│
├── base/
│   ├── BaseActivity.kt            ← Extends AppCompatActivity; setContent {}, attachBaseContext
│   │                                 for locale, isApplyLanguage(), startActivity<T>()
│   ├── BaseDialog.kt
│   ├── BundleExt.kt
│   └── WindowExt.kt
│
├── ads/
│   ├── splash/
│   │   ├── AdSplashManager.kt     ← Manages interstitial / app-open / native splash ads
│   │   ├── NativeSplashManager.kt ← Native ad on splash screen
│   │   └── AdSplashCompleteListener.kt
│   ├── native/
│   │   ├── NativeAdsWrapper.kt    ← Wrapper + NativePlacement enum (SPLASH, LANGUAGE_1/2,
│   │   │                            LANGUAGE_LOADING, ONBOARDING, FEATURE, HOME, …)
│   │   └── NativeAdPreloadManager.kt
│   ├── interstitial/
│   │   ├── InterstitialAdManager.kt
│   │   └── InterstitialAdsWrapper.kt  ← InterstitialPlacement enum (INTER_ALL)
│   ├── banner/
│   │   └── BannerAdWrapper.kt         ← BannerPlacement enum (BANNER_ALL)
│   └── reward/
│       ├── RewardAdManager.kt
│       └── RewardAdsWrapper.kt        ← RewardPlacement enum (REWARD_ALL)
│
├── remoteconfig/
│   ├── RemoteInitializer.kt       ← init() + setupRemoteConfig(); exposes remoteAds / remoteLogic / remoteUi
│   ├── BaseRemoteConfiguration.kt ← SharedPreferences-backed RC base class
│   ├── analytics/
│   │   └── Analytics.kt           ← Firebase Analytics wrapper (track())
│   ├── config/
│   │   ├── RemoteKeys.kt          ← Sealed key types (Boolean/String/Long/…)
│   │   ├── RemoteAdsConfiguration.kt  ← isAdsEnable, all ad configs (splash, native, inter, banner, …)
│   │   ├── RemoteLogicConfiguration.kt ← languageLoadingConfig, splashTimeout, nativeFullConfig
│   │   └── RemoteUiConfiguration.kt
│   └── model/
│       ├── AdConfig.kt / AdNativeConfig.kt / AdBannerConfig.kt
│       ├── AdInterConfig.kt / AdRewardConfig.kt
│       ├── app_open/AppOpenAdConfig.kt
│       ├── language/LanguageLoading.kt    ← enableScreen, showTimeMs
│       ├── native_full/NativeFullConfig.kt
│       ├── onboarding/OnboardingScreen.kt ← isEnableScreen1–4
│       ├── splash/AdSplashConfig.kt / SplashConfig.kt / SplashTimeout.kt
│       └── type/SplashType.kt / LayoutNativeType.kt
│
├── ui/
│   ├── splash/
│   │   ├── SplashActivity.kt      ← Launcher; UMP consent + Remote Config fetch + ads init
│   │   │                            → LanguageLoadingActivity or Language1Activity or MainActivity
│   │   └── NativeSplashActivity.kt ← Full-screen native ad after splash
│   │
│   ├── language/
│   │   ├── Language.kt            ← listLanguage (37 languages w/ flag drawables),
│   │   │                            changeLanguage(), createContextLocale(), cachedDeviceLanguage
│   │   ├── LanguageItem.kt        ← data class (code, name, flagId, isChoose, isDefault)
│   │   ├── LanguageListContent.kt ← Shared Compose list with flag Image + checkmark
│   │   ├── Language1Activity.kt   ← Lfo1: list without Done button → Language2Activity
│   │   ├── Language2Activity.kt   ← Lfo2: list with Done button → LanguageApplyActivity
│   │   ├── LanguageLoadingActivity.kt ← Progress bar + disabled list → Language1Activity
│   │   └── LanguageApplyActivity.kt   ← Lottie animation → OnboardingActivity
│   │
│   ├── onboarding/
│   │   └── OnboardingActivity.kt  ← HorizontalPager (4 pages + optional full-screen native ad pages)
│   │                                 → Feature1Activity
│   │
│   ├── feature/
│   │   ├── FeatureModel.kt        ← data class + sampleFeatures list
│   │   ├── Feature1Activity.kt    ← 3-col grid, no Done button → Feature2Activity
│   │   └── Feature2Activity.kt    ← 3-col grid + Done button → MainActivity
│   │                                 (sets isFinishFirstFlow = true)
│   └── theme/
│       ├── Color.kt / Theme.kt / Type.kt
│
├── style/
│   ├── AppViewTheme.kt            ← MaterialTheme wrapper (light + dark)
│   ├── Color.kt                   ← Full color palette
│   ├── ColorApp.kt                ← Semantic aliases (AppColors object)
│   ├── AppTypography.kt           ← Material3 type scale
│   ├── ExtensionComponent.kt      ← Shimmer, dot indicator, gradients
│   ├── ExtensionText.kt           ← String validators, InputValue
│   ├── ExtensionSize.kt           ← SpaceH / SpaceW, screen size helpers
│   ├── State.kt                   ← UiState<T> sealed class
│   └── DevicePreviews.kt          ← Multi-device @Preview
│
├── utils/
│   └── extensions/
│       └── Extensions.kt          ← isInternetAvailable(), moveItemToPosition()
│
├── widget/
│   └── LoadingOverlay.kt
│
└── MainActivity.kt                ← Home shell — add NavHost / main feature here
```

---

## UFO First-Run Flow

Controlled by `prefs.isFinishFirstFlow`. Runs once on first install; subsequent launches go straight to `MainActivity`.

```
SplashActivity
  │  UMP consent + Remote Config fetch (parallel, 30 s timeout)
  │  countSessionApp++ → isUfo() when countSessionApp == 1
  │
  ├─ isFinishFirstFlow = true ──────────────────────────→ MainActivity
  │
  └─ isFinishFirstFlow = false
       │
       ├─ remoteLogic.languageLoadingConfig.enableScreen = true
       │     └─→ LanguageLoadingActivity (progress bar + native ad)
       │              └─→ Language1Activity
       │
       └─ enableScreen = false
             └─→ Language1Activity
                   │  click any item (no Done button)
                   └─→ Language2Activity (same list + Done button)
                         │  Done → changeLanguage()
                         └─→ LanguageApplyActivity (Lottie + flag)
                               │  auto 2 s
                               └─→ OnboardingActivity (HorizontalPager, 4 pages)
                                     │  optional full-screen native ad pages (RC-gated)
                                     └─→ Feature1Activity (3-col grid, no Done)
                                           │  any item click
                                           └─→ Feature2Activity (3-col grid + Done)
                                                 │  Done → isFinishFirstFlow = true
                                                 └─→ MainActivity
```

---

## Remote Config

Three singleton configuration objects exposed as top-level properties:

```kotlin
remoteAds   // RemoteAdsConfiguration  — all ad configs + isAdsEnable
remoteLogic // RemoteLogicConfiguration — language loading, splash timeout, native full config
remoteUi    // RemoteUiConfiguration   — UI flags (empty by default, add as needed)
```

### Example — read a value

```kotlin
if (remoteLogic.languageLoadingConfig.enableScreen) {
    startActivityAndFinish<LanguageLoadingActivity>()
}

val adEnabled = remoteAds.adNativeLanguageConfig.enable
```

### Add a new Remote Config key

1. Open `remoteconfig/config/RemoteLogicConfiguration.kt` (or Ads / Ui).
2. Add a `data object` key and a property:

```kotlin
private data object MyNewFlag : RemoteKeys.BooleanKey("my_new_flag", false)
val myNewFlag: Boolean get() = MyNewFlag.get()
```

3. Save it in `sync()`:

```kotlin
override fun sync(remoteConfig: FirebaseRemoteConfig) {
    // existing saves …
    remoteConfig.saveToLocal(MyNewFlag)
}
```

---

## Ads

### Native ads (Compose screens)

Every screen that shows a native ad declares a `FrameLayout` + `ShimmerFrameLayout` via `AndroidView`, then passes them to `NativeAdsWrapper`:

```kotlin
class MyActivity : BaseActivity() {
    private var adContainerRef: FrameLayout? = null
    private var shimmerRef: ShimmerFrameLayout? = null

    private val nativeAdsWrapper by lazy {
        NativeAdsWrapper(
            activity = this,
            config = NativePlacement.HOME,
            lifecycleOwner = this,
            adContainer = { adContainerRef!! },
            shimmerView = { shimmerRef!! },
        )
    }

    @Composable
    override fun Content() {
        // …your UI…
        Box(Modifier.fillMaxWidth().height(250.dp)) {
            AndroidView(factory = { ShimmerFrameLayout(it).also { v -> shimmerRef = v } }, modifier = Modifier.matchParentSize())
            AndroidView(factory = { FrameLayout(it).also { v -> adContainerRef = v } }, modifier = Modifier.matchParentSize())
        }
        LaunchedEffect(Unit) {
            nativeAdsWrapper.setupNativeAd("my_tag")
            nativeAdsWrapper.requestAds()
        }
    }
}
```

Native ad layouts are XML-based (`res/layout/layout_native_*.xml`) and use SDP/SSP dimensions.

### Ad placements

| Placement | Screen |
|---|---|
| `SPLASH` | SplashActivity native ad |
| `LANGUAGE_LOADING` | LanguageLoadingActivity |
| `LANGUAGE_1` | Language1Activity |
| `LANGUAGE_2` | Language2Activity |
| `ONBOARDING` | OnboardingActivity (per-page) |
| `ONBOARDING_FULL_1/2` | Full-screen native in onboarding |
| `FEATURE` | Feature1Activity / Feature2Activity |
| `HOME` | MainActivity |

### Interstitial

```kotlin
InterstitialAdManager.loadInterAll(context)
InterstitialAdManager.showInterAll(activity) { /* onNextAction */ }
```

### Reward

```kotlin
RewardAdManager.loadRewardAll(activity)
RewardAdManager.showRewardAll(activity, onNextAction = { }, onAdNotReady = { })
```

### Banner

```kotlin
val banner = BannerAdWrapper(activity, BannerPlacement.BANNER_ALL, this) { myFrameLayout }
banner.setupBannerAd("my_banner")
banner.requestAds()
```

---

## Language Switching

`Language.kt` handles locale at both change-time and attach-time.

```kotlin
// Change language (saves to prefs + updates Locale)
Language.changeLanguage(context, "vi")

// Applied automatically on every Activity via BaseActivity.attachBaseContext()
```

`BaseActivity` applies the saved language automatically to every screen. Screens in the UFO flow that should NOT re-apply the language (to avoid recreation loops) override:

```kotlin
override fun isApplyLanguage() = false
```

---

## BaseActivity

All activities extend `BaseActivity` which provides:

```kotlin
// Navigate forward
startActivity<TargetActivity>()
startActivity<TargetActivity> { putExtra("key", value) }

// Navigate and close current
startActivityAndFinish<TargetActivity>()

// Single-top navigation
startActivitySingleTop<TargetActivity>()

// Keyboard state (observable Compose state)
val visible: Boolean = keyboardVisible
val height: Int = keyboardHeight

// Status bar icon color
setStatusBarAppearance(lightIcons = true)

// Permission request
requestPermission(Manifest.permission.CAMERA) { granted -> }
```

---

## Starting a New Project

### 1. Clone

```bash
git clone https://github.com/hieu98/Base-Compose.git MyNewApp
cd MyNewApp
```

### 2. Rename the package

In Android Studio: right-click `com.hdt.basecompose` → **Refactor → Rename** → enter your package (e.g. `com.example.myapp`).

### 3. Update application ID

```kotlin
// app/build.gradle.kts
defaultConfig {
    applicationId = "com.example.myapp"
}
productFlavors {
    create("dev")     { applicationIdSuffix = ".dev" }
    create("product") { /* production */ }
}
```

### 4. Replace Firebase config

1. Firebase Console → Add Android app → download `google-services.json`.
2. Replace `app/google-services.json`.
3. Register both `com.example.myapp` and `com.example.myapp.dev`.

### 5. Set app name & colors

```xml
<!-- res/values/strings.xml -->
<string name="app_name">My New App</string>
```

```kotlin
// style/ColorApp.kt
object AppColors {
    val Primary = Blue600   // ← your brand color
}
```

### 6. Replace launcher icons

Drop your icons into `app/src/main/res/mipmap-*/`.

---

## Common Tasks

### Add a new screen

```kotlin
class MyFeatureActivity : BaseActivity() {
    @Composable
    override fun Content() { /* your composable */ }
}
```

Register in `AndroidManifest.xml`:

```xml
<activity android:name=".ui.myfeature.MyFeatureActivity" android:exported="false" />
```

### Add a ViewModel

```kotlin
// 1. Create
class MyViewModel(private val prefs: PreferenceData) : ViewModel() { … }

// 2. Register in app/IModule.kt
val viewModelModule = module {
    viewModel { MyViewModel(get()) }
}

// 3. Inject
class MyActivity : BaseActivity() {
    private val viewModel: MyViewModel by viewModel()
}
```

### Add a preference key

```kotlin
// app/PreferenceData.kt
var myFlag: Boolean by preference.boolean("my_flag_key", false)
var userId: String  by preference.string("user_id", "")
```

### Add a Room entity

```kotlin
// 1. Entity + DAO
@Entity(tableName = "items")
data class ItemEntity(@PrimaryKey val id: Int, val name: String)

@Dao
interface ItemDao {
    @Query("SELECT * FROM items") fun getAll(): Flow<List<ItemEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: ItemEntity)
}

// 2. Database
@Database(entities = [ItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}

// 3. Provide in app/IModule.kt
single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app.db").build() }
single { get<AppDatabase>().itemDao() }
```

### Use UiState in a ViewModel

```kotlin
class MyViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<String>>>(UiState.Loading)
    val state: StateFlow<UiState<List<String>>> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try { UiState.Success(fetchData()) }
                           catch (e: Exception) { UiState.Error(e.message ?: "Error") }
        }
    }
}
```

```kotlin
// In composable
val state by viewModel.state.collectAsStateWithLifecycle()
when (state) {
    is UiState.Loading -> LoadingOverlay(isLoading = true) {}
    is UiState.Success -> MyList(data = (state as UiState.Success).data)
    is UiState.Error   -> Text((state as UiState.Error).message)
    is UiState.Empty   -> Text("Nothing here")
}
```

---

## Build Variants

| Variant | Application ID | Notes |
|---|---|---|
| `devDebug` | `com.hdt.basecompose.dev` | Daily development, test ad IDs |
| `devRelease` | `com.hdt.basecompose.dev` | Internal QA |
| `productRelease` | `com.hdt.basecompose` | Production store release |

---

## New Project Checklist

- [ ] Rename package `com.hdt.basecompose` → your package
- [ ] Update `applicationId` in `app/build.gradle.kts`
- [ ] Replace `app/google-services.json`
- [ ] Set app name in `res/values/strings.xml`
- [ ] Replace launcher icons in `res/mipmap-*/`
- [ ] Set brand colors in `style/ColorApp.kt`
- [ ] Replace placeholder ad unit IDs in `remoteconfig/model/Ad*Config.kt`
- [ ] Replace Facebook App ID / Client Token in `res/values/strings.xml`
- [ ] Add `mavenUser` / `mavenPassword` to `~/.gradle/gradle.properties` (for AzAds Nexus)
- [ ] Update onboarding titles in `res/values/strings.xml`
- [ ] Replace `sampleFeatures` in `ui/feature/FeatureModel.kt` with real features
- [ ] Add signing config to the release build type in `app/build.gradle.kts`
