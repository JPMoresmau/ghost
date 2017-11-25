package com.github.jpmoresmau.ghost

import com.badlogic.gdx.math.Vector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import java.io.StringWriter
import javax.swing.Action


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


fun toJSON(results:List<ActionResult>) : String {
    val sw= StringWriter()
    val w=Json(JsonWriter.OutputType.json)
    w.setWriter(sw)
    toJSON(results,w)
    return sw.toString()
}

fun toJSON( results:List<ActionResult>,w:Json) {
    w.writeArrayStart()
    results.forEach {r->toJSON(r,w)}
    w.writeArrayEnd()
}

fun toJSON(r:ActionResult,w:Json){
    w.writeObjectStart()
    when (r){
        is MoveOK -> w.writeValue("MoveOK",r.newPos)
        is PossessOK -> w.writeValue("PossessOK",r.newPos)
        is Experience -> w.writeValue("Experience",r.inc)
        is InsufficientPower -> w.writeValue("InsufficientPower",r.power)
        is Pass -> w.writeValue("Pass",r.newPos)
        LevelUp -> w.writeValue("LevelUp",true)
    }
    w.writeObjectEnd()
}

fun fromJSON(s : String) : List<ActionResult> {
    return fromJSON(JsonReader().parse(s))
}

fun fromJSON(v:JsonValue) : List<ActionResult>{
    val l= mutableListOf<ActionResult>()
    if (v.isArray){
        v.forEach{v2->
            val mm = v2.get("MoveOk")
            if (mm!=null) {
                l.add(MoveOK(Vector2(mm.getFloat("x"),mm.getFloat("y"))))
            }
            val mp = v2.get("PossessOK")
            if (mp!=null) {
                l.add(PossessOK(Vector2(mp.getFloat("x"),mp.getFloat("y"))))
            }
            val me = v2.get("Experience")
            if (me!=null) {
                l.add(Experience(me.asInt()))
            }
            val mi = v2.get("InsufficientPower")
            if (mi!=null) {
                l.add(InsufficientPower(mi.asInt()))
            }
            val ms = v2.get("Pass")
            if (ms!=null) {
                l.add(Pass(Vector2(ms.getFloat("x"),ms.getFloat("y"))))
            }
            val ml = v2.get("LevelUp")
            if (ml!=null) {
                l.add(LevelUp)
            }
        }
    }
    return l
}
