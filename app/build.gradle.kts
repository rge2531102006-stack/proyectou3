plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

val localBuildRoot = providers.gradleProperty("localBuildRoot")
    .orElse(providers.environmentVariable("LOCALAPPDATA").map { "$it/ControldeGastos2/build" })
    .orElse("${System.getProperty("user.home")}/.gradle/ControldeGastos2/build")
layout.buildDirectory.set(layout.dir(localBuildRoot.map { file(it) }))

android {
    namespace = "com.example.controldegastos"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.controldegastos"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.biometric:biometric:1.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}