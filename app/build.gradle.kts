plugins {
    id("com.android.application")
}

android {
    namespace = "com.peaceandcotton.sweatshirts"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.peaceandcotton.sweatshirts"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
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
    buildFeatures {
        viewBinding = true
    }

    // ADD THIS PACKAGING OPTIONS BLOCK
    packagingOptions {
        resources {
            excludes += "META-INF/INDEX.LIST"
            // You might also commonly need to exclude these if you encounter similar errors:
            // excludes += "META-INF/LICENSE.md"
            // excludes += "META-INF/LICENSE-notice.md"
            // excludes += "META-INF/*.txt" // Be careful with wildcards, might exclude needed files
             excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Standard AndroidX libraries
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    // ... other dependencies

    // Google Sign-In for authentication
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Google API Client Library for Android
    implementation("com.google.api-client:google-api-client-android:2.8.0")

    // Google Drive API Services for Java (v3)
    implementation("com.google.apis:google-api-services-drive:v3-rev20250511-2.0.0")

    // If you need specific JSON parsing for the API client,
    // though usually handled transitively by google-api-client-android
    // implementation("com.google.http-client:google-http-client-gson:1.42.2")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}