package entity.comp

import entity.Entity

/**
 * The type and parent must be the same in order for this to work.
 * The type specifies what *kind* of entity the parent may be, the parent
 * is the link to the actual entity.
 **/
open class EntityComp<T : Entity>(val parent: T)
