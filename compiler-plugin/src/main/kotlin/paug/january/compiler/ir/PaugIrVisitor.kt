package paug.january.compiler.ir


import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.isFakeOverride
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import paug.january.compiler.fir.KeyImplementation

/**
 * The IR visitor that implements the function bodies
 */
class PaugIrVisitor(val pluginContext: IrPluginContext) : IrVisitorVoid() {
  override fun visitClass(declaration: IrClass) {
    val origin = declaration.origin
    if (origin is IrDeclarationOrigin.GeneratedByPlugin) {
      if (origin.pluginKey is KeyImplementation) {
        declaration.declarations.forEach { declaration ->
          if (!declaration.isFakeOverride) return@forEach
          if (declaration is IrFunction && declaration.name.asString() in setOf("equals", "hashCode", "toString")) {
            return@forEach
          }

          if (declaration is IrProperty) {
            declaration.getter!!.isFakeOverride = false
            declaration.getter!!.modality = Modality.FINAL

            val symbol = declaration.getter!!.symbol

            declaration.getter!!.body =
              DeclarationIrBuilder(pluginContext, symbol, symbol.owner.startOffset, symbol.owner.endOffset)
                .irBlockBody {
                  val irClass = declaration.getter!!.returnType.classOrNull!!.owner
                  +irReturn(
                    constructorExpression(irClass)
                  )
                }
          }
        }
      }
    }

    declaration.acceptChildrenVoid(this)
  }

  override fun visitElement(element: IrElement) {
    when (element) {
      is IrDeclaration,
      is IrFile,
      is IrModuleFragment -> element.acceptChildrenVoid(this)

      else -> {}
    }
  }
}

/**
 * Generates an expression that creates an instance of the given class using its primary constructor
 */
internal fun IrBuilderWithScope.constructorExpression(irClass: IrClass): IrConstructorCall {
  val constructor =
    irClass.constructors.singleOrNull() ?: error("Expected a single constructor for class ${irClass.name}")

  return irCallConstructor(constructor.symbol, emptyList()).apply {
    arguments.clear()
    arguments += constructor.parameters.map {
      constructorExpression(it.type.classOrNull!!.owner)
    }
  }
}