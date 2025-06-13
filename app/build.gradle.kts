plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.androidprojek.unifind"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.androidprojek.unifind"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.activity)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.google.android.gms:play-services-base:18.5.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    // Untuk CardView dan MaterialButton
    implementation("com.google.android.material:material:1.12.0")
    // Untuk CircleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // Untuk ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    // Untuk Dots Indicator
    implementation("com.tbuonomo:dotsindicator:5.0")
    //bentuk filter kategori di home
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
}