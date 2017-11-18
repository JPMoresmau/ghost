package com.github.jpmoresmau.ghost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Vector3


/**
 * Created by jpmoresmau on 04/11/2017.
 */
class MainMenuScreen (private val assets: GhostAssets) : Screen {



    private val camera = OrthographicCamera()

    private val titleLayout = GlyphLayout(assets.font64,"Ghost Quest")
    private val newLayout = GlyphLayout(assets.font,"New Game")
    private val continueLayout = GlyphLayout(assets.font,"Continue Game")
    private val quitLayout = GlyphLayout(assets.font,"Quit")

    init {
        camera.setToOrtho(false, 800f, 480f)

        assets.mainMenuMusic.isLooping = true
        assets.mainMenuMusic.volume = 0.2f
    }


    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        camera.update()
        assets.batch.projectionMatrix = camera.combined

        assets.batch.begin()
        assets.batch.draw(assets.paper, 0f, 0f, camera.viewportWidth, camera.viewportHeight);

        assets.font64.draw(assets.batch, titleLayout, assets.center(camera,titleLayout.width.toInt()), 440f)

        val newRect = assets.button(camera,true,260f,newLayout)
        val continueRect = assets.button(camera,false,160f,continueLayout)

        val quitRect= assets.button(camera,true,60f,quitLayout)


        assets.batch.end()

        if (Gdx.input.isTouched) {
            val touchPos = Vector3()
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touchPos)
            //assets.game.screen = GameScreen(assets)
            if (quitRect.contains(touchPos.x.toFloat(), touchPos.y.toFloat())){
               Gdx.app.log("MainMenuScreen","Exiting")
               Gdx.app.exit()
            } else if (newRect.contains(touchPos.x.toFloat(), touchPos.y.toFloat())){
                assets.game.screen = WorldScreen(GhostState(assets))
                dispose()
            }

            //
        }
    }


    override fun show() {
        // start the playback of the background music
        // when the screen is shown
        assets.mainMenuMusic.play()
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {
        assets.mainMenuMusic.pause()
    }

    override fun resume() {
        assets.mainMenuMusic.play()
    }

    override fun hide() {
        assets.mainMenuMusic.stop()
    }

    override fun dispose() {
    }
}