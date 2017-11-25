package com.github.jpmoresmau.ghost

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx

class GhostGame : Game() {
    private var entry : GhostAssets? = null

    var state : GhostState? = null

    override fun create() {
        val e = GhostAssets(this)
        entry = e
        this.setScreen(LoadingScreen(e))
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        entry?.dispose()
    }

    override fun pause() {
        val st=state
        if (st!=null){
            saveContent(st.toSave())
        }
        super.pause()
    }


    fun getSaveContent() : String? {
        if (Gdx.files.isLocalStorageAvailable()){
            val handle = Gdx.files.internal("save.json")
            if (handle.exists() && !handle.isDirectory){
                return handle.readString()
            }
        }
        return null;
    }

    fun saveContent(save : String) {
        if (Gdx.files.isLocalStorageAvailable) {
            Gdx.app.log("Game","Path is ${Gdx.files.localStoragePath}")
            val handle = Gdx.files.local("save.json")
            handle.writeString(save,false)

        }
    }
}
