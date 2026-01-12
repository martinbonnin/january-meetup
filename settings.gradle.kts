@file:Suppress("UnstableApiUsage")

pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
  }
}

include(":runtime", ":gradle-plugin", ":compiler-plugin")

gradle.lifecycle.beforeProject {
  group = "paug.january"
  version = "0.0.0"
}