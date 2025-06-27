import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

kotlin {
    // Пользуемся вашей JDK 17
    jvmToolchain(17)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

android {
    namespace = "com.sfedu.bank_queue_android"      // ← ОБЯЗАТЕЛЬНО
    compileSdk = 35

    packagingOptions {
        resources {
            // если ты хочешь просто отбросить все дубликаты этого файла:
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
    }

    defaultConfig {
        applicationId = "com.sfedu.bank_queue_android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        // версия Compose Compiler, совместимая с Kotlin 1.8.22
        kotlinCompilerExtensionVersion = "1.4.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core & Compose
    implementation("androidx.compose.compiler:compiler:1.4.8")
    implementation(libs.androidx.core.ktx.v1101)
    implementation(libs.androidx.activity.compose.v172)
    implementation(libs.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.v100)
    implementation(libs.androidx.material3.android)
    debugImplementation(libs.ui.tooling)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose.v100)
    kapt(libs.hilt.android.compiler)
    kapt("androidx.room:room-compiler:2.5.2")

    // Retrofit + Gson + Coroutines
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle & ViewModel
    implementation(libs.androidx.lifecycle.runtime.ktx.v261)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(libs.ui.test.junit4)

    // okthttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Unit tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0")

    // Instrumentation tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Для Compose UI-тестов
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:<compose-version>")
    testImplementation(kotlin("test"))

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0")

    // — для сетевых тестов с MockWebServer:
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")
    // — если вы используете Moshi как JSON-конвертер:
    testImplementation("com.squareup.moshi:moshi:1.14.0")
    testImplementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    // — если вы используете Gson:
    // testImplementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // — сама библиотека Retrofit нужна, если вы напрямую создаёте Retrofit-экземпляр в тестах:
    testImplementation("com.squareup.retrofit2:retrofit:2.9.0")
}