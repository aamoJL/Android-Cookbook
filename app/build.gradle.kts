plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp")
  id("androidx.room")
}

room {
  schemaDirectory("$projectDir/schemas")
}

android {
  namespace = "com.aamo.cookbook"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.aamo.cookbook"
    minSdk = 28
    //noinspection OldTargetApi
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    debug {
      isMinifyEnabled = false
      applicationIdSuffix = ".debug"
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
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
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.7"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  sourceSets {
    getByName("androidTest").assets.srcDir("$projectDir/schemas")
  }
  androidResources {
    @Suppress("UnstableApiUsage")
    generateLocaleConfig = true
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
  implementation("androidx.activity:activity-compose:1.8.2")
  implementation(platform("androidx.compose:compose-bom:2023.10.01"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")
  implementation(platform("androidx.compose:compose-bom:2023.03.00"))
  implementation(platform("androidx.compose:compose-bom:2023.03.00"))
  implementation(platform("androidx.compose:compose-bom:2023.03.00"))
  implementation("androidx.navigation:navigation-compose:2.7.6")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
  implementation("androidx.room:room-runtime:2.6.1")
  implementation("androidx.test.ext:junit-ktx:1.1.5")
  annotationProcessor("androidx.room:room-compiler:2.6.1")
  ksp ("androidx.room:room-compiler:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")
  implementation("io.coil-kt:coil-compose:2.5.0")
  implementation("androidx.camera:camera-camera2:1.4.0-alpha04")


  testImplementation("junit:junit:4.13.2")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")

  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
  androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
  androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
  androidTestImplementation("androidx.navigation:navigation-testing:2.7.6")
  androidTestImplementation("androidx.room:room-testing:2.6.1")

  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}