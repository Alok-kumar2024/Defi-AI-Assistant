plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //Chart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //for refreshing
    implementation (libs.androidx.swiperefreshlayout)

    //Lottie Animation Dependency
    implementation(libs.lottie)


    //For Wallet Connect
    implementation(platform("com.walletconnect:android-bom:1.18.0"))
    implementation("com.walletconnect:android-core")
    implementation("com.walletconnect:web3wallet")
    implementation("com.walletconnect:sign")

    // WalletConnectKit for modal UI
//    implementation("dev.pinkroom.walletconnectkit:core:2.0.0-alpha03")
//    implementation("dev.pinkroom.walletconnectkit:sign-dapp:2.0.0-alpha03")


    implementation("androidx.browser:browser:1.5.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation (libs.gson)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation("com.google.code.gson:gson:2.10.1")

    //gemini dependency
    implementation("com.google.ai.client.generativeai:generativeai:0.4.0")

    implementation("io.noties.markwon:core:4.6.2")


}