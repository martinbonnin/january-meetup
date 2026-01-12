package paug.january.compiler.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

/**
 * The entry point for FIR
 */
class PaugFirExtensionRegistrar : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::PaugFirDeclarationGenerationExtension
  }
}
