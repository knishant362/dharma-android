import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id ("kotlin-parcelize")
    id ("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    id("com.google.firebase.crashlytics")
}

fun loadProperties(fileName: String): Properties {
    val props = Properties()
    val file = rootProject.file(fileName)
    FileInputStream(file).use { props.load(it) }
    return props
}

// Load properties from config.properties
val configProps = loadProperties("config.properties")

fun loadProperty(propertyName: String): String {
    return configProps[propertyName] as String
}

android {
    namespace = "com.aurora.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sanatan.daily.mandir"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", loadProperty("app.BASE_URL"))
        buildConfigField("String", "AUTH_TOKEN", loadProperty("app.AUTH_TOKEN"))
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = "AuroaStudio@123"
            keyAlias = "key0"
            keyPassword = "AuroaStudio@123"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {}
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material.icons.extended)


    // Firebase
    implementation (platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.config.ktx)

    // one signal
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")

    implementation (libs.app.update.ktx)
    implementation (libs.review.ktx)

    // Room
    implementation ("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    // hilt
    implementation ("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // networking
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.google.code.gson:gson:2.11.0")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.3")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")

    // Coroutine and Lifecycle
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

    implementation("io.github.raamcosta.compose-destinations:core:2.2.0")
    implementation("io.github.raamcosta.compose-destinations:animations-core:1.11.9")
    ksp("io.github.raamcosta.compose-destinations:ksp:2.2.0")

    // datastore
    implementation ("androidx.datastore:datastore-preferences:1.1.1")

    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

    implementation("com.jakewharton.timber:timber:5.0.1")

    debugImplementation("com.github.chuckerteam.chucker:library:4.1.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.1.0")

    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.6")
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("com.valentinilk.shimmer:compose-shimmer:1.3.1")
    implementation("com.github.commandiron:WheelPickerCompose:1.1.11")


}