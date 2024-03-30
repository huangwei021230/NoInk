plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.bagel.noink"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bagel.noink"
        minSdk = 24
        targetSdk = 33
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
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation ("androidx.activity:activity-compose:1.7.2")
    implementation ("androidx.compose.ui:ui:1.4.6")
    implementation ("androidx.compose.ui:ui-tooling-preview:1.4.6")
    implementation ("androidx.compose.material3:material3:1.2.0-alpha08")
    implementation ("io.github.ShawnLin013:number-picker:2.4.13")
    implementation ("androidx.viewpager2:viewpager2:1.0.0-alpha02")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.1")
    implementation ("com.aliyun.dpa:oss-android-sdk:+")
    implementation ("com.squareup.okio:okio:1.9.0")
    implementation ("com.github.JakeWharton:ViewPagerIndicator:2.4.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("androidx.core:core-ktx:+")
    implementation ("me.relex:circleindicator:2.1.6")
    implementation("com.github.LinweiJ:ViewPagerIndicator:0.3.0")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("androidx.test:core:1.4.0")
    testImplementation("org.mockito:mockito-core:4.2.0")

    val lifecycle_version = "2.2.0"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

    implementation ("io.getstream:avatarview-coil:1.0.7")

}