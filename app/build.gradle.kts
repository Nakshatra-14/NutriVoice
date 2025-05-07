plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
}

android {
    namespace = "com.example.newscan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.newscan"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Retrofit and Moshi for JSON parsing
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")

    // Coroutine dependencies
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")

    // OkHttp Logging Interceptor for debugging
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.1")

    // Code Scanner library
    implementation("com.github.yuriy-budiyev:code-scanner:2.3.0")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.0")
    kapt("com.github.bumptech.glide:compiler:4.15.0") // Changed annotationProcessor to kapt for Kotlin

    // Room database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Lifecycle and ViewModel for database integration
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")

    // Lottie for animations
    implementation("com.airbnb.android:lottie:6.5.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}