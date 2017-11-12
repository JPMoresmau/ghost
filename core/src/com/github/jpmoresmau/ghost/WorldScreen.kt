package com.github.jpmoresmau.ghost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2


/**
 * Created by jpmoresmau on 05/11/2017.
 */
class WorldScreen (private val state: GhostState) : Screen {

    private val camera = OrthographicCamera()

    private val renderer = OrthogonalTiledMapRenderer(state.handle.castle1, 1/32f)

    private val playerSprite = Sprite(state.handle.wraith)

    init {
        camera.setToOrtho(false, 25f, 15f)
        renderer.setView(camera)

        //playerSprite.setScale(1/32f)
        playerSprite.setSize(1f,1f)
        state.handle.worldMusic.isLooping = true
        state.handle.worldMusic.volume = 0.05f

        Gdx.input.inputProcessor=TouchAdapter()
    }

    inner class TouchAdapter : InputAdapter(){
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val touchPos = Vector3()
            touchPos.set(screenX.toFloat(), screenY.toFloat(), 0f)
            camera.unproject(touchPos)
            touchPos.x = Math.floor(touchPos.x.toDouble()).toFloat()
            touchPos.y = Math.floor(touchPos.y.toDouble()).toFloat()
            move(touchPos)
            return true
        }
    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        val effectiveViewportWidth = camera.viewportWidth * camera.zoom
        val effectiveViewportHeight = camera.viewportHeight * camera.zoom


        camera.position.set(state.playerPosition.x,state.playerPosition.y,0f)
        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, 25 - effectiveViewportWidth / 2f)
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, 26 - effectiveViewportHeight / 2f)

        camera.update()
        renderer.setView(camera)
        renderer.render()

        val batch = renderer.batch
        batch.begin()
        playerSprite.setPosition(state.playerPosition.x,state.playerPosition.y)
        playerSprite.draw(batch)

        batch.end()


    }

    fun move(touchPos : Vector3){

        var newPos = Vector2(state.playerPosition)
        if (touchPos.x>newPos.x){
            newPos.x+=1f;
        } else if  (touchPos.x<newPos.x){
            newPos.x-=1f;
        }
        if (touchPos.y>newPos.y){
            newPos.y+=1f;
        } else if  (touchPos.y<newPos.y){
            newPos.y-=1f;
        }
        //Gdx.app.log("WorldScreen","$touchPos : ${state.playerPosition} -> $newPos")
        if (state.canPass(newPos,renderer.map)){
           state.playerPosition=newPos
        } else {

        }


    }

    override fun show() {
        // start the playback of the background music
        // when the screen is shown
        state.handle.worldMusic.play()
    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {
        state.handle.worldMusic.pause()
    }

    override fun resume() {
        state.handle.worldMusic.play()
    }

    override fun hide() {

    }

    override fun dispose() {

    }
}