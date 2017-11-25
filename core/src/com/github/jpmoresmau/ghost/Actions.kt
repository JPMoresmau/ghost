package com.github.jpmoresmau.ghost

import com.badlogic.gdx.math.Vector2


/**
 * Created by jpmoresmau on 18/11/2017.
 */

sealed class ActionResult
data class MoveOK(val newPos : Vector2) : ActionResult()
data class PossessOK(val newPos : Vector2): ActionResult()
data class InsufficientPower(val power: Int) : ActionResult()
data class Experience(val inc: Int) : ActionResult()
data class Pass(val newPos: Vector2) : ActionResult()
object LevelUp: ActionResult()


