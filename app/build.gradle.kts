plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.kotlinweatherapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.kotlinweatherapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.1.10"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Core Retrofit and Gson Converter
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp Logging Interceptor
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    implementation("io.coil-kt.coil3:coil-compose:3.4.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.4.0")

    // Aligned to same version as lifecycle-runtime-ktx to prevent K2 compiler crash
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    implementation("androidx.compose.material:material-icons-extended")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Jetpack Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // MapLibre for Android (Open-source map integration)
    implementation("org.maplibre.gl:android-sdk:11.0.0")

    // Preferences DataStore for Settings
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Google Play Services for Location
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // WorkManager for background API calls
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // AppCompatDelegate (Instant Language Switching)
    implementation("androidx.appcompat:appcompat:1.6.1")
}

// FORCE Gradle to strictly use the Kotlin 2.1.10 standard libraries to prevent KSP crash
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib:2.1.10")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.1.10")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.10")
    }
}