package com.github.jpmoresmau.ghost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3


/**
 * Created by jpmoresmau on 04/11/2017.
 */
class MainMenuScreen (private val handle: GhostHandle) : Screen {



    private val camera = OrthographicCamera()

    private val titleLayout = GlyphLayout(handle.font64,"Ghost Quest")
    private val newLayout = GlyphLayout(handle.font,"New Game")
    private val continueLayout = GlyphLayout(handle.font,"Continue Game")
    private val quitLayout = GlyphLayout(handle.font,"Quit")

    init {
        camera.setToOrtho(false, 800f, 480f)

        handle.mainMenuMusic.isLooping = true
        handle.mainMenuMusic.volume = 0.2f
    }


    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        handle.batch.projectionMatrix = camera.combined

        handle.batch.begin()
        handle.batch.draw(handle.paper, 0f, 0f, camera.viewportWidth, camera.viewportHeight);

        handle.font64.draw(handle.batch, titleLayout, handle.center(camera,titleLayout.width.toInt()), 440f)

        val newRect = handle.button(camera,true,260f,newLayout)
        val continueRect = handle.button(camera,false,160f,continueLayout)

        val quitRect=handle.button(camera,true,60f,quitLayout)


        handle.batch.end()

        if (Gdx.input.isTouched) {
            val touchPos = Vector3()
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touchPos)
            //handle.game.screen = GameScreen(handle)
            if (quitRect.contains(touchPos.x.toFloat(), touchPos.y.toFloat())){
               Gdx.app.log("MainMenuScreen","Exiting")
               Gdx.app.exit()
            } else if (newRect.contains(touchPos.x.toFloat(), touchPos.y.toFloat())){
                handle.game.screen = WorldScreen(GhostState(handle))
                dispose()
            }

            //
        }
    }


    override fun show() {
        // start the playback of the background music
        // when the screen is shown
        handle.mainMenuMusic.play()
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {
        handle.mainMenuMusic.pause()
    }

    override fun resume() {
        handle.mainMenuMusic.play()
    }

    override fun hide() {
        handle.mainMenuMusic.stop()
    }

    override fun dispose() {

    }
}