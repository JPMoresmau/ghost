package com.github.jpmoresmau.ghost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Rectangle


/**
 * Created by jpmoresmau on 04/11/2017.
 */
class GhostHandle(val game : GhostGame) {
    val manager = AssetManager()

    val batch = SpriteBatch()

    init {

        manager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))

        manager.load("fonts/breathefire.fnt", BitmapFont::class.java )
        manager.load("fonts/breathefire-64.fnt", BitmapFont::class.java )

        manager.load("paper.png", Texture::class.java)
        manager.load("RPG_GUI_v1.png",Texture::class.java)

        manager.load("music/Something_Wicked.mp3",Music::class.java)
        manager.load("music/Heart_of_Nowhere.mp3",Music::class.java)
        manager.load("music/Lost_Time.mp3",Music::class.java)

        manager.load("sprites/wraith.png",Texture::class.java)


        manager.load("maps/castle1.tmx", TiledMap::class.java)

    }

    fun dispose() {
        batch.dispose()
        manager.dispose()
    }

    val font : BitmapFont
        get() = manager.get("fonts/breathefire.fnt", BitmapFont::class.java )

    val font64 : BitmapFont
        get() = manager.get("fonts/breathefire-64.fnt", BitmapFont::class.java )

    val paper : Texture
        get() = manager.get("paper.png", Texture::class.java)

    val rpgElements : Texture
        get() = manager.get("RPG_GUI_v1.png", Texture::class.java)

    val mainMenuMusic : Music
        get() = manager.get("music/Something_Wicked.mp3", Music::class.java)

    val worldMusic : Music
        get() = manager.get("music/Heart_of_Nowhere.mp3", Music::class.java)

    val playerMusic : Music
        get() = manager.get("music/Lost_Time.mp3", Music::class.java)


    val wraith : Texture
        get() =  manager.get("sprites/wraith.png",Texture::class.java)

    val castle1 : TiledMap
     get() = manager.get("maps/castle1.tmx", TiledMap::class.java)


    private val enabledButton : TextureRegion
        get() = TextureRegion(rpgElements,10,126,292,60)
    private val disabledButton : TextureRegion
        get() = TextureRegion(rpgElements,10,360,292,60)

    fun button(camera: OrthographicCamera, enabled : Boolean, y : Float, layout: GlyphLayout) : Rectangle {
        val region= if (enabled) enabledButton else disabledButton
        val x = center(camera,region.regionWidth)
        batch.draw(region, x, y)
        font.draw(batch, layout, x+(region.regionWidth-layout.width)/2, y+45)
        return Rectangle(x,y,region.regionWidth.toFloat(),region.regionHeight.toFloat())
    }

    fun center(camera: OrthographicCamera, width: Int) : Float {
        return (camera.viewportWidth - width)/2
    }
}