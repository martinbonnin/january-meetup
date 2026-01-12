plugins {
  id("org.jetbrains.kotlin.jvm")
  id("com.github.gmazzo.buildconfig")
  id("java-gradle-plugin")
}

dependencies {
  compileOnly(libs.kgp)
  compileOnly(libs.gradle.api)
}

buildConfig {
  buildConfigField("kotlin.String", "version", "\"$version\"")
}

gradlePlugin {
  plugins {
    create("paug.january") {
      id = "paug.january"
      implementationClass = "paug.january.gradle.PaugGradlePlugin"
    }
  }
}