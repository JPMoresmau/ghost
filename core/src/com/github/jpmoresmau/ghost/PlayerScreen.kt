package com.github.jpmoresmau.ghost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.GlyphLayout

/**
 * Created by jpmoresmau on 18/11/2017.
 */
class PlayerScreen (private val state: GhostState) : Screen {

    private val camera = OrthographicCamera()

    private val stateLayout = GlyphLayout(state.handle.font,"${state.playerState}")
    private val powerLayout = GlyphLayout(state.handle.font,"Power: ${state.playerPower}")
    private val experienceLayout = GlyphLayout(state.handle.font,"Experience: ${state.playerExperience}")

    init {
        camera.setToOrtho(false, 800f, 480f)

        state.handle.playerMusic.isLooping = true
        state.handle.playerMusic.volume = 0.2f

        Gdx.input.inputProcessor= object : InputAdapter(){
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                state.handle.game.screen = WorldScreen(state)
                dispose()
                return true
            }
        }

    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        state.handle.batch.projectionMatrix = camera.combined

        state.handle.batch.begin()
        state.handle.batch.draw(state.handle.paper, 0f, 0f, camera.viewportWidth, camera.viewportHeight);

        state.handle.button(camera,true,300f,stateLayout)
        state.handle.button(camera,true,200f,powerLayout)
        state.handle.button(camera,true,100f,experienceLayout)

        state.handle.batch.end()

    }


    override fun show() {
        // start the playback of the background music
        // when the screen is shown
        state.handle.playerMusic.play()

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {
        state.handle.playerMusic.pause()
    }

    override fun resume() {
        state.handle.playerMusic.play()
    }

    override fun hide() {
        state.handle.playerMusic.stop()
    }

    override fun dispose() {

    }
}