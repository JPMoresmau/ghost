package com.github.jpmoresmau.ghost

/**
 * Created by jpmoresmau on 18/11/2017.
 */

sealed class MoveResult
object MoveOK : MoveResult()
data class MoveInsufficientPower(val power: Int) : MoveResult()