plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.compose)


    id ("kotlin-parcelize")

}


android {
    namespace = "com.example.aisplitwise"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.example.aisplitwise"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        val mapsApiKey: String = System.getenv("MAPS_API_KEY") ?: ""
        resValue("string", "maps_api_key", mapsApiKey)
        manifestPlaceholders["MAPS_API_KEY"] = project.findProperty("MAPS_API_KEY") ?: ""

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility =JavaVersion.VERSION_17
                targetCompatibility =JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
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

    implementation(libs.play.services.auth)

    implementation(libs.bundles.maps)

}
