@file:JvmName("DesktopLauncher")

package com.github.jpmoresmau.ghost.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.tools.hiero.Hiero
import com.github.jpmoresmau.ghost.GhostGame


fun main(args: Array<String>) {
    val config = LwjglApplicationConfiguration()
    config.title = "Ghost Quest"
    config.width = 800
    config.height = 480
    config.forceExit = true
    config.addIcon("sprites/wraith.png",Files.FileType.Internal)
    LwjglApplication(GhostGame(), config)
    //Hiero.main(arrayOf())
}

