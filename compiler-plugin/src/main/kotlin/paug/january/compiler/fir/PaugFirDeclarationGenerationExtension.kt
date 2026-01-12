package paug.january.compiler.fir

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.toAnnotationClassIdSafe
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.plugin.createNestedClass
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames

internal object KeyImplementation : GeneratedDeclarationKey()
internal object KeyImplementationConstructor : GeneratedDeclarationKey()

internal val dependencyGraphClassId = ClassId(
  packageFqName = FqName(fqName = "paug.january.runtime"),
  topLevelName = Name.identifier("DependencyGraph")
)

class PaugFirDeclarationGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
  /**
   * Tells the Kotlin compiler to analyze those classes.
   * This is a coarse filter. Other classes may still be passed down to other callbacks
   */
  override fun FirDeclarationPredicateRegistrar.registerPredicates() {
    register(LookupPredicate.create {
      annotated(dependencyGraphClassId.asSingleFqName())
    })
  }

  private val implementations = mutableSetOf<ClassId>()

  /**
   * Tells the Kotlin compiler to generate a nested `Impl`
   * class for each `@DependencyGraph` class.
   */
  override fun getNestedClassifiersNames(
    classSymbol: FirClassSymbol<*>,
    context: NestedClassGenerationContext
  ): Set<Name> {
    val isDependencyGraph = classSymbol.resolvedCompilerAnnotationsWithClassIds.any {
      it.toAnnotationClassIdSafe(session) == dependencyGraphClassId
    }
    if (!isDependencyGraph) {
      return setOf()
    }

    val name = Name.identifier("Impl")
    implementations.add(classSymbol.classId.createNestedClassId(name))
    return setOf(name)
  }

  /**
   * Generates the actual `Impl` FIR declaration.
   */
  override fun generateNestedClassLikeDeclaration(
    owner: FirClassSymbol<*>,
    name: Name,
    context: NestedClassGenerationContext
  ): FirClassLikeSymbol<*>? {

    val classId = owner.classId.createNestedClassId(name)
    if (!implementations.contains(classId)) {
      return null
    }
    return createNestedClass(owner, name, KeyImplementation) {
      superType(owner.constructType())
    }.symbol
  }

  /**
   * Tells the Kotlin compiler to generate a constructor for each `Impl` class.
   */
  override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
    val origin = classSymbol.origin
    if (origin !is FirDeclarationOrigin.Plugin || origin.key !is KeyImplementation) {
      return emptySet()
    }
    return setOf(SpecialNames.INIT)
  }

  /**
   * Generates the constructor for each `Impl` class.
   */
  override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
    val origin = context.owner.origin
    if (origin !is FirDeclarationOrigin.Plugin || origin.key !is KeyImplementation) {
      return emptyList()
    }

    return listOf(
      createConstructor(
        owner = context.owner,
        key = KeyImplementationConstructor,
        isPrimary = true,
        generateDelegatedNoArgConstructorCall = true
      ).symbol
    )
  }
}