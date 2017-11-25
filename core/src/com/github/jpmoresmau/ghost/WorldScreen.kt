package com.github.jpmoresmau.ghost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector2
import cz.tchalupnik.libgdx.Toast


/**
 * Created by jpmoresmau on 05/11/2017.
 */
class WorldScreen (private val state: GhostState) : Screen {

    private val camera = OrthographicCamera()

    private val renderer = OrthogonalTiledMapRenderer(state.assets.castle1, 1/32f)

    private val toastFactory = Toast.ToastFactory.Builder()
            .font(state.assets.font)
            .backgroundColor(Color.BLACK)
            .margin(5)
            .build()

    private val moveMessages = mutableListOf<Toast>()

    init {
        camera.setToOrtho(false, 25f, 15f)
        renderer.setView(camera)


        //playerSprite.setScale(1/32f)
        state.assets.worldMusic.isLooping = true
        state.assets.worldMusic.volume = 0.05f

        Gdx.input.inputProcessor=TouchAdapter()
    }

    inner class TouchAdapter : InputAdapter(){
        override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
            val touchPos = Vector3()
            touchPos.set(screenX.toFloat(), screenY.toFloat(), 0f)
            camera.unproject(touchPos)
            touchPos.x = Math.floor(touchPos.x.toDouble()).toFloat()
            touchPos.y = Math.floor(touchPos.y.toDouble()).toFloat()
            if (!move(touchPos)){
                showPlayerScreen()
            }
            return true
        }

        override fun keyDown(keycode: Int): Boolean {
            when (keycode) {
               Input.Keys.RIGHT -> {
                   move(Vector3(state.player.position.x+1,state.player.position.y,0f))
                   return true
               }
                Input.Keys.LEFT -> {
                    move(Vector3(state.player.position.x-1,state.player.position.y,0f))
                    return true
                }
                Input.Keys.UP -> {
                    move(Vector3(state.player.position.x,state.player.position.y+1,0f))
                    return true
                }
                Input.Keys.DOWN -> {
                    move(Vector3(state.player.position.x,state.player.position.y-1,0f))
                    return true
                }
                Input.Keys.SPACE -> {
                    showPlayerScreen()
                    return true
                }
            }

            return false
        }
    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        val effectiveViewportWidth = camera.viewportWidth * camera.zoom
        val effectiveViewportHeight = camera.viewportHeight * camera.zoom


        camera.position.set(state.player.position.x,state.player.position.y,0f)
        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, 25 - effectiveViewportWidth / 2f)
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, 26 - effectiveViewportHeight / 2f)

        camera.update()
        renderer.setView(camera)
        renderer.render()

        val batch = renderer.batch
        batch.begin()
        drawAvatar(batch,state.player.avatar,state.player.position)
        for((pos,npc) in state.player.npcs){
            drawAvatar(batch,npc.avatar,pos)
        }
        batch.end()

        Toast.renderToasts(delta,moveMessages)

    }

    fun drawAvatar(batch: Batch, avatar:GhostState. Avatar,pos:Vector2){
        avatar.sprite.setSize(1f,1f)
        avatar.sprite.setPosition(pos.x,pos.y)
        avatar.sprite.draw(batch)
    }

    fun move(touchPos : Vector3) : Boolean{

        var newPos = Vector2(state.player.position)
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
        if (newPos != state.player.position) {
            //Gdx.app.log("WorldScreen","$touchPos : ${state.playerPosition} -> $newPos")
            val results = state.move(newPos,renderer.map)
            results.forEach(this::handleSideResult)
            return true
        }
        return false

    }

    private fun handleSideResult(ar:ActionResult){
        when (ar){
            is PossessOK -> state.assets.whisperSound.play(0.7f)
            LevelUp -> state.assets.sighSound.play(1f)
            is InsufficientPower -> {
                moveMessages.clear()
                moveMessages.add(toastFactory.create("Insufficient power! (required: ${ar.power})", Toast.Length.SHORT))
            }
        }
    }

    fun showPlayerScreen() {
        state.assets.game.screen=PlayerScreen(state)
        dispose()
    }


    override fun show() {
        // start the playback of the background music
        // when the screen is shown
        state.assets.worldMusic.play()

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun pause() {
        state.assets.worldMusic.pause()
    }

    override fun resume() {
        state.assets.worldMusic.play()
    }

    override fun hide() {
        state.assets.worldMusic.stop()
        Gdx.app.log("WorldScreen","hide")
    }

    override fun dispose() {
        renderer.dispose()
    }
}