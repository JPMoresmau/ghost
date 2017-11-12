package com.github.jpmoresmau.ghost

import com.badlogic.gdx.Game

class GhostGame : Game() {
    private var entry : GhostHandle? = null

    override fun create() {
        val e = GhostHandle(this)
        entry = e
        this.setScreen(LoadingScreen(e))
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        entry?.dispose()
    }
}
