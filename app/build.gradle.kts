import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-parcelize")
    alias(libs.plugins.protobuf)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.musicshelf"
    compileSdk = 35

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }
    val spotifyClientId = localProperties.getProperty("SPOTIFY_CLIENT_ID") ?: ""
    val spotifyRedirectUri = localProperties.getProperty("SPOTIFY_REDIRECT_URI") ?: ""

    defaultConfig {
        applicationId = "com.example.musicshelf"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"$spotifyClientId\"")
        buildConfigField("String", "SPOTIFY_REDIRECT_URI", "\"$spotifyRedirectUri\"")
        manifestPlaceholders["spotifyRedirectScheme"] = "musicshelf"
        manifestPlaceholders["spotifyRedirectHost"] = "callback"
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
        buildConfig = true
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobufLite.get()}"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.text.google.fonts)

    // DataStore & Protobuf
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.ink.geometry)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Gson
    implementation(libs.gson)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Browser (Chrome Custom Tabs)
    implementation(libs.androidx.browser)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(platform("com.google.firebase:firebase-bom:34.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore) {
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
    }
    implementation(libs.play.services.auth)
    
    // Credential Manager for Google Sign In
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
}

// Ensure KSP (Hilt) runs after Protobuf code generation and sees generated sources
afterEvaluate {
    tasks.matching { it.name.startsWith("ksp") && it.name.endsWith("Kotlin") }.configureEach {
        val variantName = name.removePrefix("ksp").removeSuffix("Kotlin")
        val protoTask = tasks.findByName("generate${variantName}Proto")
        if (protoTask != null) {
            dependsOn(protoTask)
        }
    }
}

// Add proto-generated Java sources to Kotlin source sets so KSP can resolve them
android {
    applicationVariants.all {
        val variantName = name
        kotlin.sourceSets.getByName(variantName) {
            kotlin.srcDir(layout.buildDirectory.dir("generated/source/proto/$variantName/java"))
        }
    }
}