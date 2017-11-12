@file:JvmName("MapBuilder")
package com.github.jpmoresmau.ghost.desktop

import org.xml.sax.Attributes
import org.xml.sax.ContentHandler
import org.xml.sax.InputSource
import org.xml.sax.Locator
import org.xml.sax.helpers.XMLReaderFactory
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files

/**
 * Created by jpmoresmau on 12/11/2017.
 */
fun main(args: Array<String>) {
    val tileIDs = HashSet<String>()

    tileIDs.addAll(loadMap("../../../tiled/castle1.tmx","maps/castle1.tmx"))

    loadTileSet("../../../tiled/crawl.tsx","maps/crawl.tsx",tileIDs)
}

fun loadMap(map : String,dest : String) : Set<String>{
    val inputMap = File(map)
    if (!inputMap.exists()) {
        throw IllegalArgumentException("Map ${inputMap.absolutePath} does not exist")
    } else {
        val destFile = File(dest)
        destFile.parentFile.mkdirs()
        destFile.delete()
        inputMap.copyTo(destFile)

        val xmlR=XMLReaderFactory.createXMLReader()
        val mh= MapHandler()
        xmlR.contentHandler = mh
        xmlR.parse(InputSource(destFile.canonicalPath))
        return mh.tileIDs
    }

}

fun loadTileSet(tileset : String,dest : String, tileIDs : Set<String>) {
    val inputMap = File(tileset)
    if (!inputMap.exists()) {
        throw IllegalArgumentException("TileSet ${inputMap.absolutePath} does not exist")
    } else {
        val destFile = File(dest)
        destFile.parentFile.mkdirs()
        destFile.delete()

        val regex= Regex("\\s*<tile id=\"(\\d+)\">")
        var skip=0
        val w=BufferedWriter(OutputStreamWriter(FileOutputStream(destFile),StandardCharsets.UTF_8))
        try {
            for (line in Files.readAllLines(inputMap.toPath(), StandardCharsets.UTF_8)) {
                //println (line)
                if (skip > 0) {
                    skip--
                } else if (regex.matches(line)) {
                    val v = regex.matchEntire(line)!!.groups.get(1)!!.value
                    if (tileIDs.contains(v)) {
                        w.write(line)
                        w.newLine()
                    } else {
                        skip = 2
                    }
                } else if (line.trim().startsWith("<image")) {
                    w.write(line.replace("../assets/crawl-tiles Oct-5-2010/crawl-tiles Oct-5-2010/", "./"))
                    w.newLine()
                } else {
                    w.write(line)
                    w.newLine()
                }
            }
        } finally {
            w.close()
        }
    }
}

class MapHandler : ContentHandler {
    var inCSVData : Boolean = false
    var sb = StringBuffer()

    val tileIDs = HashSet<String>()

    override fun endDocument() {

    }

    override fun endElement(p0: String?, p1: String?, p2: String?) {
        if (inCSVData){
           val s=sb.toString()
           tileIDs.addAll(s.split(',','\r','\n',' ').filter { i -> i.length >0 })

        }
        inCSVData=false
        sb.setLength(0)
     }

    override fun processingInstruction(p0: String?, p1: String?) {
    }

    override fun startPrefixMapping(p0: String?, p1: String?) {
    }

    override fun ignorableWhitespace(p0: CharArray?, p1: Int, p2: Int) {
    }

    override fun characters(p0: CharArray?, p1: Int, p2: Int) {
        sb.append(p0,p1,p2)
    }

    override fun startElement(p0: String?, p1: String?, p2: String?, p3: Attributes?) {
      inCSVData = p1=="data" && "csv".equals(p3?.getValue("encoding"))
    }

    override fun skippedEntity(p0: String?) {
    }

    override fun setDocumentLocator(p0: Locator?) {
    }

    override fun endPrefixMapping(p0: String?) {
    }

    override fun startDocument() {
    }
}