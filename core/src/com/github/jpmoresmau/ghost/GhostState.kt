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

    data class NPC(val avatar: Avatar,val power:Int, val experience:Int)

    val WRAITH=Avatar("wraith","Wraith",Sprite(assets.wraith))

    var playerPosition = Vector2(4f,21f)

    var playerPower = 1

    var playerExperience = 0

    var playerAvatar = WRAITH

    var playerNPC : NPC? = null

    val npcs = hashMapOf( Vector2(6f,19f) to NPC(Avatar("rat","A huge rat",Sprite(assets.rat)),1,5))

    val passed = hashSetOf<Vector2>()

    fun canPass(pos : Vector2 ,map :TiledMap, sideResults:MutableList<ActionResult>) : ActionResult {
        var r: ActionResult = MoveOK
        for (l in map.layers.reversed()){
            val layer = l as TiledMapTileLayer
            val cell = layer.getCell(pos.x.toInt(),pos.y.toInt())
            if (cell!=null && cell.tile!=null) {
                val all =  cell.tile.properties.get("allowed", String::class.java)
                if (all!=null && all.equals(playerAvatar.code)){
                    if (passed.add(pos)) {
                        val exp = cell.tile.properties.get("exp", 0, Int::class.java)
                        incExperience(exp, sideResults)
                    }
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

    fun move(newPos : Vector2 ,map :TiledMap) : ActionResults {
        val npc = npcs.get(newPos)
        val sideResults= mutableListOf<ActionResult>()
        if (npc!=null){
            if (npc.power>playerPower){
                return ActionResults(InsufficientPower(npc.power),sideResults)
            } else {
                npcs.remove(newPos)
                playerPosition = newPos
                possess(npc,sideResults)
                return ActionResults(MoveOK,sideResults)
            }
        }
        val mr = canPass(newPos,map,sideResults)
        when (mr) {
            MoveOK -> playerPosition = newPos
        }
        return ActionResults(mr,sideResults)
    }

    private fun possess(npc:NPC, sideResults:MutableList<ActionResult>){
        playerAvatar = npc.avatar

        sideResults.add(PossessOK)
        incExperience(npc.experience,sideResults)
        playerNPC = NPC(npc.avatar,npc.power,0)

    }

    private fun incExperience(exp: Int, sideResults:MutableList<ActionResult>){
        playerExperience+=exp
        var up=false
        while (playerExperience>=Math.pow(playerPower.toDouble(),2.0)*10){
            playerPower++
            up=true
        }
        if (up){
            sideResults.add(LevelUp)
        }
    }


}
