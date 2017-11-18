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

    private val stateLayout = GlyphLayout(state.assets.font,"${state.playerAvatar.label}")
    private val powerLayout = GlyphLayout(state.assets.font,"Power: ${state.playerPower}")
    private val experienceLayout = GlyphLayout(state.assets.font,"Experience: ${state.playerExperience}")

    init {
        camera.setToOrtho(false, 800f, 480f)

        state.assets.playerMusic.isLooping = true
        state.assets.playerMusic.volume = 0.2f

        Gdx.input.inputProcessor= object : InputAdapter(){
            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                state.assets.game.screen = WorldScreen(state)
                dispose()
                return true
            }
        }

    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        camera.update()
        state.assets.batch.projectionMatrix = camera.combined

        state.assets.batch.begin()
        state.assets.batch.draw(state.assets.paper, 0f, 0f, camera.viewportWidth, camera.viewportHeight);

        state.assets.button(camera,true,300f,stateLayout)
        state.assets.button(camera,true,200f,powerLayout)
        state.assets.button(camera,true,100f,experienceLayout)

        state.assets.batch.end()

    }


    override fun show() {
        // start the playback of the background music
        // when the screen is shown
        state.assets.playerMusic.play()

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {
        state.assets.playerMusic.pause()
    }

    override fun resume() {
        state.assets.playerMusic.play()
    }

    override fun hide() {
        state.assets.playerMusic.stop()
    }

    override fun dispose() {

    }
}