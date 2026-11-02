package world

import kotlinx.serialization.Serializable

@Serializable
data class TiledMap(
    val width: Int,
    val height: Int,
    val tileWidth: Int,
    val tileHeight: Int,
    val layers: List<TiledLayer>,
    val tilesets: List<TiledTilesetRef> = emptyList()
)

@Serializable
data class TiledLayer(
    val name: String,
    val type: String,
    val width: Int = 0,
    val height: Int = 0,
    val data: List<Long> = emptyList()   // Long, not Int: flip flags use the high bits
)

@Serializable
data class TiledTilesetRef(val firstGid: Int, val source: String? = null)

@Serializable
data class TiledProperty(val name: String, val type: String, val value: kotlinx.serialization.json.JsonElement)

@Serializable
data class TiledTile(val id: Int, val properties: List<TiledProperty> = emptyList())

@Serializable
data class TiledTileset(
    val firstGid: Int,
    val image: String? = null,
    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
    val tileWidth: Int = 0,
    val tileHeight: Int = 0,
    val columns: Int = 0,
    val tiles: List<TiledTile> = emptyList()
)