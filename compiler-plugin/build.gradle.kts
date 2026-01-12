plugins {
  id("org.jetbrains.kotlin.jvm")
}

dependencies {
  compileOnly(libs.kotlin.compiler)
}

kotlin {
  compilerOptions {
    optIn.addAll(
      "org.jetbrains.kotlin.fir.symbols.SymbolInternals",
      "org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi",
      "org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI",
      "org.jetbrains.kotlin.backend.common.extensions.ExperimentalAPIForScriptingPlugin",
    )
  }
}

