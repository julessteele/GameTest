package objects.components

import objects.entity.Entity

/**
 * The type and parent must be the same in order for this to work.
 * The type specifies what *kind* of entity the parent may be, the parent
 * is the link to the actual entity.
 * Oftentimes, the compiler will be able to infer T in <T : Entity> from the parent's type in the parameters.
 **/
abstract class EntityComponent<T : Entity>(val parent: T)
