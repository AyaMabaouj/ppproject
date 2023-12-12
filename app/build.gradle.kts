plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.gms.strict-version-matcher-plugin") version "1.2.1"


}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        mlModelBinding = true
    }

    testOptions.unitTests.isIncludeAndroidResources = true

}

dependencies {

    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")

    implementation("androidx.test.ext:junit-ktx:1.1.5")
    implementation("com.google.ar:core:1.41.0")
    implementation("androidx.core:core-ktx:+")
    implementation("androidx.core:core-ktx:+")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("org.mockito:mockito-android:3.11.2")
    testImplementation ("io.mockk:mockk:1.12.0")

    testImplementation ("org.mockito:mockito-core:3.11.2")
    testImplementation ("org.robolectric:robolectric:4.11.1")
    testImplementation ("androidx.test:core:1.5.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")


    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-location:18.0.0")


    implementation("androidx.biometric:biometric:1.1.0")

    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.google.android.gms:play-services-mlkit-language-id:17.0.0")
    implementation("com.google.mlkit:language-id:17.0.4")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    implementation ("androidx.media:media:1.4.1")

    implementation("androidx.camera:camera-camera2:1.4.0-alpha02")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha02")
    implementation("androidx.camera:camera-view:1.4.0-alpha02")
}