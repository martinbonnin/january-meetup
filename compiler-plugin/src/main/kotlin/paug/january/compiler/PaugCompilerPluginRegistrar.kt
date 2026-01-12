package paug.january.compiler


import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import paug.january.compiler.fir.PaugFirExtensionRegistrar
import paug.january.compiler.ir.PaugIrGenerationExtension

/**
 * The main plugin entry point
 */
class PaugCompilerPluginRegistrar : CompilerPluginRegistrar() {
  override val pluginId = "paug.january"
  override val supportsK2 = true

  override fun ExtensionStorage.registerExtensions(
    configuration: CompilerConfiguration
  ) {
    FirExtensionRegistrarAdapter.registerExtension(PaugFirExtensionRegistrar())
    IrGenerationExtension.registerExtension(PaugIrGenerationExtension())
  }
}
