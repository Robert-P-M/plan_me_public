plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.kotlin.parcelize.plugin)
    alias(libs.plugins.robolectric.extension.gradle.plugin)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "at.robthered.plan_me"
    compileSdk = 36

    defaultConfig {
        applicationId = "at.robthered.plan_me"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "at.robthered.plan_me.CustomTestRunner"

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

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
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
    }
    packaging {
        resources {
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
    }

}


dependencies {
    // --- Core & Lifecycle ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Jetpack Compose ---
    implementation(platform(libs.androidx.compose.bom)) // BOM import
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3) // Material 3
    implementation(libs.androidx.compose.material.material.icons.core) // Icons
    implementation(libs.androidx.compose.material.material.icons.extended) // Icons
    implementation(libs.androidx.compose.ui.ui.text.google.fonts) // Google Fonts
    implementation(libs.google.accompanist.accompanist.permissions) // Accompanist Permissions

    // --- Navigation ---
    implementation(libs.androidx.navigation.navigation.compose)

    // --- Data Layer ---
    // Room
    implementation(libs.androidx.room.room.runtime)
    implementation(libs.androidx.room.room.ktx)
    implementation(libs.androidx.room.room.paging)
    ksp(libs.androidx.room.room.compiler) // Room KSP processor
    // Paging
    implementation(libs.androidx.paging.paging.runtime)
    implementation(libs.androidx.paging.paging.compose)
    // Kotlinx Serialization & DateTime
    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation(libs.jetbrains.kotlinx.kotlinx.datetime)

    // --- Dependency Injection (Koin) ---
    implementation(libs.bundles.koin) // Use the Koin bundle

    // --- Debugging ---
    debugImplementation(libs.androidx.ui.tooling) // Compose Tooling for Layout Inspector, etc.
    debugImplementation(libs.androidx.ui.test.manifest) // Needed for Compose UI tests

    // --- Android Instrumentation Tests (/androidTest source set) ---
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Align Compose versions
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose UI Test Rule (uses JUnit 4)


    // tests
    // Unit Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.junit5.jupiter)
    testCompileOnly(libs.junit5.jupiter.api)
    testRuntimeOnly(libs.junit5.jupiter.engine)
    testImplementation(libs.junit5.vintage.engine)
    testRuntimeOnly(libs.junit5.platform.launcher)
    testImplementation(libs.junit5.platform.commons)
    testImplementation(libs.bundles.unitTestLibs)

    // Instrumentation Tests
    androidTestImplementation(libs.bundles.androidTestLibs)
}