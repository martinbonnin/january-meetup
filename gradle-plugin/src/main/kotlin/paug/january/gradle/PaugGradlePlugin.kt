package paug.january.gradle

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import paug.january.gradle_plugin.BuildConfig

abstract class PaugGradlePlugin : KotlinCompilerPluginSupportPlugin {
  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
    return true
  }

  override fun getCompilerPluginId(): String {
    return "paug.january"
  }

  override fun getPluginArtifact(): SubpluginArtifact {
    return SubpluginArtifact("paug.january", "compiler-plugin", BuildConfig.version)
  }

  override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
    return kotlinCompilation.target.project.provider { emptyList() }
  }
}
