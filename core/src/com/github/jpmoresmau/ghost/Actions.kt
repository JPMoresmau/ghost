package com.github.jpmoresmau.ghost

/**
 * Created by jpmoresmau on 18/11/2017.
 */

sealed class ActionResult
object MoveOK : ActionResult()
object PossessOK: ActionResult()
data class InsufficientPower(val power: Int) : ActionResult()

