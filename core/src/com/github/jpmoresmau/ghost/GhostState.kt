package com.github.jpmoresmau.ghost

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2

/**
 * Created by jpmoresmau on 05/11/2017.
 */
class GhostState (val assets: GhostAssets){


    data class Avatar(val code:String,val label:String,val sprite:Sprite)

    data class NPC(val avatar: Avatar,val power:Int)

    val WRAITH=Avatar("wraith","Wraith",Sprite(assets.wraith))

    var playerPosition = Vector2(4f,21f)

    var playerPower = 1

    var playerExperience = 0

    var playerAvatar = WRAITH

    var playerNPC : NPC? = null

    val npcs = hashMapOf( Vector2(6f,19f) to NPC(Avatar("rat","A huge rat",Sprite(assets.rat)),1))


    fun canPass(pos : Vector2 ,map :TiledMap) : ActionResult {
        var r: ActionResult = MoveOK
        for (l in map.layers.reversed()){
            val layer = l as TiledMapTileLayer
            val cell = layer.getCell(pos.x.toInt(),pos.y.toInt())
            if (cell!=null && cell.tile!=null) {
                val all =  cell.tile.properties.get("allowed", String::class.java)
                if (all!=null && all.equals(playerAvatar.code)){
                    return MoveOK
                }
                val pow = cell.tile.properties.get("power", 0, Int::class.java)
                if (pow > playerPower) {
                    r = InsufficientPower(pow)
                }

            }
        }
        return r
    }

    fun move(newPos : Vector2 ,map :TiledMap) : ActionResult {
        val npc = npcs.get(newPos)
        if (npc!=null){
            if (npc.power>playerPower){
                return InsufficientPower(npc.power)
            } else {
                npcs.remove(newPos)
                playerPosition = newPos
                possess(npc)
                return PossessOK
            }
        }
        val mr = canPass(newPos,map)
        when (mr) {
            MoveOK -> playerPosition = newPos
        }
        return mr
    }

    private fun possess(npc:NPC){
        playerAvatar = npc.avatar
        playerNPC = npc
        playerExperience+=1

    }


}
