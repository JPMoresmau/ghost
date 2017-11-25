package com.github.jpmoresmau.ghost

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2


/**
 * Created by jpmoresmau on 05/11/2017.
 */
class GhostState (val assets: GhostAssets){
    init {
        assets.game.state=this
    }

    data class Avatar(val code:String,val label:String,val sprite:Sprite)

    data class NPC(val avatar: Avatar,val power:Int, val experience:Int)

    private val WRAITH=Avatar("wraith","Wraith",Sprite(assets.wraith))

    private val defaultNPCs = mapOf( Vector2(6f,19f) to NPC(Avatar("rat","A huge rat",Sprite(assets.rat)),1,5))

    data class Player (
            val position : Vector2,

            val avatar : Avatar,

            val npcs :Map<Vector2,NPC>,

            val passed :Set<Vector2> = setOf<Vector2>(),

            val power : Int = 1,

            val experience : Int = 0,

            val playerNPC : NPC? = null,

            val actions : List<ActionResult> = listOf<ActionResult>())

    var player = Player(Vector2(4f,21f),WRAITH,HashMap(defaultNPCs))

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
        val player2=results.fold(player, this::processAction)
        if (player2.actions.size>player.actions.size) {
            val s = toSave(player2)
            assets.game.saveContent(s)
        }
        player=player2
    }

    private fun processAction(player:Player, result:ActionResult ) : Player{
        var pl2=player
        when (result) {
            is MoveOK -> pl2 = player.copy(position= result.newPos)
            is Pass -> pl2 = player.copy(passed=player.passed.plus(result.newPos))
            is PossessOK -> {
                val npc=player.npcs.get(result.newPos)!!
                pl2 = player.copy(avatar=npc.avatar,playerNPC = npc.copy(experience = 0),npcs = player.npcs.minus(result.newPos))
            }
            is Experience -> pl2 = player.copy(experience=player.experience+result.inc)
            LevelUp -> pl2=player.copy(power=player.power+1)
        }
        return saveAction(pl2,result)
    }

    private fun saveAction(player:Player, result:ActionResult) : Player {
        var pl2=player
        when (result){
            is Pass,is PossessOK,is Experience,LevelUp -> pl2=player.copy(actions=player.actions.plus(result))
        }
        return pl2
    }

    private fun toSave(p : Player) : String {
        return toJSON(p.actions.plus(MoveOK(p.position)))
    }

    fun toSave() : String {
        return toSave(player)
    }

    fun fromSave(saves : String){
        processActions(fromJSON(saves))
    }
}
