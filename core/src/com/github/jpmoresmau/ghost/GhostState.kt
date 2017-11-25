package com.github.jpmoresmau.ghost

import com.badlogic.gdx.Gdx
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

    private val WRAITH=Avatar("wraith","Wraith",Sprite(assets.wraith))

    private val defaultNPCs = mapOf( Vector2(6f,19f) to NPC(Avatar("rat","A huge rat",Sprite(assets.rat)),1,5))

    data class Player (
        var position : Vector2,

        var avatar : Avatar,

        val npcs :HashMap<Vector2,NPC>,

        val passed :HashSet<Vector2> = hashSetOf<Vector2>(),

        var power : Int = 1,

        var experience : Int = 0,

        var playerNPC : NPC? = null)

    val player = Player(Vector2(4f,21f),WRAITH,HashMap(defaultNPCs))

    private fun canPass(pos : Vector2 ,map :TiledMap, results:MutableList<ActionResult>) {
        var requiredPower=0
        for (l in map.layers.reversed()){
            val layer = l as TiledMapTileLayer
            val cell = layer.getCell(pos.x.toInt(),pos.y.toInt())
            if (cell!=null && cell.tile!=null) {
                val all =  cell.tile.properties.get("allowed", String::class.java)
                if (all!=null && all==player.avatar.code){
                    if (!player.passed.contains(pos)) {
                        results.add(Pass(pos))
                        val exp = cell.tile.properties.get("exp", 0, Int::class.java)
                        incExperience(exp, results)
                    }
                    results.add(MoveOK(pos))
                    return
                }
                val pow = cell.tile.properties.get("power", 0, Int::class.java)
                if (pow > player.power) {
                    requiredPower=Math.max(requiredPower,pow)
                }
            }
        }
        if (requiredPower>0){
            results.add(InsufficientPower(requiredPower))
        } else {
            results.add(MoveOK(pos))
        }
    }

    fun move(newPos : Vector2 ,map :TiledMap) : List<ActionResult> {
        val npc = player.npcs.get(newPos)
        val results= mutableListOf<ActionResult>()
        if (npc!=null){
            if (npc.power>player.power){
                results.add(InsufficientPower(npc.power))
                return results
            } else {
                results.add(MoveOK(newPos))
                results.add(PossessOK(newPos))
                incExperience(npc.experience,results)
                processActions(results)
                return results
            }
        }
        canPass(newPos,map,results)
        processActions(results)
        return results
    }


    private fun incExperience(exp: Int, results:MutableList<ActionResult>){
        results.add(Experience(exp))
        var pow=player.power
        while (player.experience+exp>=Math.pow(pow.toDouble(),2.0)*10){
            pow++
            results.add(LevelUp)
        }
    }

    private fun processActions(results:List<ActionResult>){
        results.fold(player, this::processAction)
    }

    private fun processAction(player:Player, result:ActionResult ) : Player{
        when (result) {
            is MoveOK -> player.position = result.newPos
            is Pass -> player.passed.add(result.newPos)
            is PossessOK -> {
                val npc=player.npcs.remove(result.newPos)!!
                player.avatar = npc.avatar
                player.playerNPC = NPC(npc.avatar,npc.power,0)
            }
            is Experience -> player.experience+=result.inc
            LevelUp -> player.power++
        }
        Gdx.app.log("State","result:${result},player:${player}")
        return player
    }
}
