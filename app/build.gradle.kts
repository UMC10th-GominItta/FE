import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

val kakaoNativeAppKey: String = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""

android {
    namespace  = "com.gominitta.android"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.gominitta.android"
        minSdk        = 26
        targetSdk     = 37
        versionCode   = 1
        versionName   = "0.1.0-scaffold"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }

        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoNativeAppKey\"")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoNativeAppKey
    }

    signingConfigs {
        // 팀 공용 디버그 키스토어 — 모두 같은 키 해시로 빌드해 카카오 로그인 키 해시를 한 번만 등록하면 됨.
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.core.splashscreen)
    // Compose BOM — pins all androidx.compose.* versions
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.compose.core)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Background scheduling — 로컬 알림(걱정/세션 리마인드) 예약
    implementation(libs.androidx.work.runtime.ktx)

    // Lifecycle ViewModel/Runtime (required for @HiltViewModel + collectAsStateWithLifecycle in Compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // 손글씨 촬영 — 크롭·기울기·그림자 보정된 이미지를 서버 OCR에 넘긴다
    implementation(libs.mlkit.document.scanner)

    // Kakao 로그인 SDK
    implementation(libs.kakao.user)
    // Networking
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Debug tools
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
