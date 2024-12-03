import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.project.secureauthentication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.secureauthentication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load API key from local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        // Retrieve the SENDGRID_API_KEY and FROM_EMAIL properties
        val sendGridApiKey = localProperties.getProperty("SENDGRID_API_KEY") ?: "MISSING_API_KEY"
        val fromEmail = localProperties.getProperty("FROM_EMAIL") ?: "MISSING_FROM_EMAIL"
        // Add them to the BuildConfig
        buildConfigField("String", "SENDGRID_API_KEY", "\"$sendGridApiKey\"")
        buildConfigField("String", "FROM_EMAIL", "\"$fromEmail\"")
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    //glide
    implementation (libs.glide)
    //hCAPTCHA
    implementation("com.github.hCaptcha.hcaptcha-android-sdk:sdk:4.0.2")
    //reCAPTCHA
    implementation("com.google.android.recaptcha:recaptcha:18.7.0-beta01")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    // Add the dependencies for the App Check libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    //sendgrid
    implementation("com.github.jakebreen:android-sendgrid:1.3.1")
    //okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}