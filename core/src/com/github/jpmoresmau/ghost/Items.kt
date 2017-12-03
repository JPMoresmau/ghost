package com.github.jpmoresmau.ghost

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2

/**
 * Created by jpmoresmau on 03/12/2017.
 */

data class Avatar(val code:String,val label:String,val sprite:Sprite)

data class NPC(val avatar: Avatar,val power:Int, val experience:Int)

data class PassAllowance(val npc : String, val experience : Int)

class GhostItems (val assets: GhostAssets) {

    val defaultNPCs = mapOf(
            Vector2(6f, 19f) to NPC(Avatar("rat", "A huge rat", Sprite(assets.rat)), 1, 5)
            ,Vector2(5f,12f) to NPC(Avatar("maid","Faithful maid",Sprite(assets.maid)), 2, 5)
        )

    val passAllowed = mapOf(Vector2(5f, 18f) to PassAllowance("rat", 5),
            Vector2(8f,14f) to PassAllowance("maid",5))

    val messages = mapOf(Vector2(5f, 18f) to "There is a small hole in that wall",
            Vector2(4f,14f) to "The coffin where your decaying body lays")
}