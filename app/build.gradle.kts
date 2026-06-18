plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)  // requires app/google-services.json — remove if not using Firebase
    id("org.jetbrains.kotlin.plugin.parcelize")
}

android {
    namespace = "com.hdt.basecompose"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.hdt.basecompose"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            manifestPlaceholders["ad_app_id"] = "ca-app-pub-3940256099942544~3347511713"
            resValue("string", "ad_app_id", "ca-app-pub-3940256099942544~3347511713")
            buildConfigField("boolean", "build_debug", "true")
        }
        create("product") {
            dimension = "env"
            manifestPlaceholders["ad_app_id"] = "ca-app-pub-3940256099942544~3347511713"
            resValue("string", "ad_app_id", "ca-app-pub-3940256099942544~3347511713")
            buildConfigField("boolean", "build_debug", "false")
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
        resValues = true
    }

    bundle {
        language {
            enableSplit = false
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Koin DI
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Coil 3
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    // SDP / SSP — scalable dimensions (used by native ad XML layouts)
    implementation(libs.intuit.sdp)
    implementation(libs.intuit.ssp)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // GSON (used by SharePreference for List<T> serialization)
    implementation(libs.gson)

    // Retrofit + OkHttp (networking)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Firebase — requires google-services.json in app/
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
    implementation(libs.firebase.messaging)

    // AzAds + mediation
    implementation(libs.azmoduleads)
    compileOnly(libs.multidex)
    implementation(libs.lottie.compose)
    implementation(libs.shimmer)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.ads)
    implementation(libs.mediation.facebook)
    implementation(libs.mediation.mintegral)
    // Pangle mediation excluded due to namespace conflict in pag-sdk 7.7.x — re-add when resolved
    // implementation(libs.mediation.pangle)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
