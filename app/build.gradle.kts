plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "smu.ai.transittime"
    compileSdk = 36

    defaultConfig {
        applicationId = "smu.ai.transittime"
        minSdk = 35
        targetSdk = 36
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // RecyclerView (목록 효율적으로 보여주기 위해)
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    // API 통신을 위한 라이브러리
//    implementation("com.squarup.retrofit2:retrofit:2.9.0")
//    implementation("com.squarup.retrofit2:converter-gson:2.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}