plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.david.gameoflife"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs["debug"]
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
        freeCompilerArgs = listOf(
            "-Xinline-classes",
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xallow-unstable-dependencies"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = DependencyVersions.COMPOSE
    }
}

dependencies {
    // compose
    implementation("androidx.compose.ui:ui:${DependencyVersions.COMPOSE}")
    implementation("androidx.compose.material:material:${DependencyVersions.COMPOSE}")
    implementation("androidx.compose.ui:ui-tooling:${DependencyVersions.COMPOSE}")
    implementation("androidx.compose.runtime:runtime-livedata:${DependencyVersions.COMPOSE}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha04")
    implementation("androidx.activity:activity-compose:1.3.0-alpha07")

    // room
    implementation("androidx.room:room-runtime:${DependencyVersions.ROOM}")
    kapt("androidx.room:room-compiler:${DependencyVersions.ROOM}")
    implementation("androidx.room:room-ktx:${DependencyVersions.ROOM}")

    // navigation
    implementation("com.github.mvarnagiris:compose-navigation:${DependencyVersions.NAVIGATION}")

    //debug
    debugImplementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0")

    // testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}