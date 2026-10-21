package world

import entity.Entity
import render.Camera
import render.Mesh
import render.Renderer

open class World(
    val name: String,
    val entities: MutableList<Entity>,
    var camera: Camera? = null,
    var renderer: Renderer? = null,
    var meshes: MutableList<Mesh> = mutableListOf()) {

    // Spawn in entity into the world
    fun <T : Entity> spawn(entity: T) = entities.add(entity)

    // Remove entity from the world, returns whether the element was removed (found)
    fun <T : Entity> die(entity: T) = entities.remove(entity)
}