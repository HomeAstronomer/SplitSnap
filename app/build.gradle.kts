plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    id ("kotlin-parcelize")

}


android {
    namespace = "com.example.aisplitwise"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.aisplitwise"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        multiDexEnabled = true
    }
    signingConfigs {

        create("release") {
            // Make sure to secure these values using environment variables or other secure storage
            keyAlias = "my-key-alias"
            keyPassword = "@th@rv@N14"
            storeFile = file("../my-release-key.jks")
            storePassword = "@th@rv@N14"
        }
    }

    buildTypes {
        // Debug build configuration
       debug {

            // Disable code shrinking and obfuscation for debug builds
            isMinifyEnabled = false
            isShrinkResources = false

            // Enable debug-specific logging
            isDebuggable = true

            // Use debug signing configuration
            signingConfig = signingConfigs.getByName("debug")
        }

        // Release build configuration
       release {
            // Enable code shrinking and obfuscation to reduce APK size and protect the code
            isMinifyEnabled = true
            isShrinkResources = true

            // Use ProGuard rules or R8 for release builds
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Disable debug-specific logging and make the build non-debuggable
            isDebuggable = false

            // Use release signing configuration
            signingConfig = signingConfigs.getByName("release")

            // Aligns the final APK to optimize it for the Play Store
            isZipAlignEnabled = true
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {

    // Core Libraries
    implementation(libs.androidx.core.ktx)

    // KotlinX Serialization
    implementation(libs.kotlix.serialization.json)
    implementation(libs.kotlix.serialization.core)

    // Lifecycle and Compose Libraries
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Navigation and Material
    implementation(libs.androidx.material3)
    implementation(libs.navigation.compose)

    // Hilt Libraries
    implementation(libs.hilt.android)
    implementation(libs.hilt.android.gradle)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.play.services.location)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.androidx.navigation.compose)

    //Room Libraries
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging Libraries
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(platform(libs.firebase.bom))

    // Use the Firebase bundle
    implementation(libs.bundles.firebase)

    implementation(libs.coil)
    implementation(libs.coil.compose)


    implementation(libs.androidx.material.icons.extended)

    implementation(libs.lottie.compose)

    implementation(libs.gson)

    implementation(libs.mlkit.text.recognition)



//    implementation("com.google.android.gms:play-services-auth:21.2.0")


}
