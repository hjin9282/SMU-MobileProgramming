import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "smu.ai.teampj_schedule"
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "smu.ai.teampj_schedule"
        minSdk = 35
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProps = gradleLocalProperties(
            rootDir,
            providers = providers
        )

        val timeKey = localProps.getProperty("TIMETABLE_API_KEY") ?: ""
        val scheduleKey = localProps.getProperty("SCHEDULE_API_KEY") ?: ""

        buildConfigField("String", "TIMETABLE_API_KEY", "\"$timeKey\"")
        buildConfigField("String", "SCHEDULE_API_KEY", "\"$scheduleKey\"")
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit 라이브러리 (서버 통신용)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson 변환기 (JSON 데이터를 자바 변수로 바꿔주는 애)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}