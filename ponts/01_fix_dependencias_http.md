# Guia 01: Adicionar Dependências HTTP no Android

## 🎯 Objetivo
Habilitar comunicação HTTP entre o app Android e o backend SmartSaúde.

## 📁 Arquivo a Modificar
`/front/Esc--Maya-Yoshiko-Yamamoto.git/Neon/Login/app/build.gradle.kts`

## 🔧 Dependências Necessárias

Adicionar no bloco `dependencies`:

```kotlin
// HTTP Client - Retrofit
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// JSON Parsing
implementation("com.google.code.gson:gson:2.10.1")

// Coroutines para chamadas assíncronas
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
```

## 📋 Versão Final do build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.esclogin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.esclogin"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // ✅ NOVAS DEPENDÊNCIAS HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
```

## ⚠️ Permissão Internet

Adicionar no `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 🔄 Próximos Passos

1. ✅ Adicionar dependências (este guia)
2. 🔄 Criar classes de modelo (Request/Response)
3. 🔄 Implementar cliente API
4. 🔄 Integrar no botão de cadastro

## 🧪 Teste

Após adicionar as dependências, compile o projeto para garantir não há erros de dependência.
