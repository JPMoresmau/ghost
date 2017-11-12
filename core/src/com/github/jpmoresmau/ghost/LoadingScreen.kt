package com.github.jpmoresmau.ghost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont



/**
 * Created by jpmoresmau on 12/11/2017.
 */
class LoadingScreen (private val handle: GhostHandle) : Screen {

    var font: BitmapFont = BitmapFont()

    private val camera = OrthographicCamera()

    init {
        camera.setToOrtho(false, 800f, 480f)
    }

    override fun render(delta: Float) {
        if (handle.manager.update()){
            handle.game.screen=MainMenuScreen(handle)
            dispose()
        } else {
            val p=handle.manager.progress
            val percent = (p*100).toInt()
            Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            camera.update()
            handle.batch.projectionMatrix = camera.combined

            handle.batch.begin()
            font.draw(handle.batch, "Loading $percent%", 100f, 150f)
            handle.batch.end()
        }

    }

    override fun hide() {

    }

    override fun resume() {

    }

    override fun show() {

    }

    override fun pause() {

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun dispose() {
        font.dispose()
    }
}