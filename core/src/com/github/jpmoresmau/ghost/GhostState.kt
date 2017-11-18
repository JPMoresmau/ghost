package com.github.jpmoresmau.ghost

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

/**
 * Created by jpmoresmau on 05/11/2017.
 */
class GhostState (val handle:GhostHandle){
    var playerPosition : Vector2 = Vector2(4f,21f)

    var playerPower : Int = 1

    var playerExperience: Int = 0

    var playerState : String = "Wraith"

    fun canPass(pos : Vector2 ,map :TiledMap) : MoveResult {
        for (l in map.layers.reversed()){
            val layer = l as TiledMapTileLayer
            val cell = layer.getCell(pos.x.toInt(),pos.y.toInt())
            if (cell!=null && cell.tile!=null) {
                val pow = cell.tile.properties.get("power", 0, Int::class.java)
                if (pow > playerPower) {
                    return MoveInsufficientPower(pow)
                }
            }
        }
        return MoveOK
    }


}
