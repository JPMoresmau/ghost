/*
 * Copyright (c) 2017-present, Tomas Chalupnik (tchalupnik.cz)
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.tchalupnik.libgdx

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Align

/**
 * Android-like toast implementation for LibGDX projects
 *
 * @author Tomas Chalupnik (tchalupnik.cz)
 */
class Toast internal constructor(
        private val msg: String,
        length: Length,
        private val font: BitmapFont,
        backgroundColor: Color,
        private val fadingDuration: Float,
        maxRelativeWidth: Float,
        private val fontColor: Color,
        private val positionY: Float, // left bottom corner
        customMargin: Int?
) {
    private val spriteBatch = SpriteBatch()
    private val renderer = ShapeRenderer()

    private var opacity = 1f
    private val toastWidth: Int
    private val toastHeight: Int
    private var timeToLive: Float = 0.toFloat()
    private val positionX: Float
    private val fontX: Float
    private val fontY: Float // left top corner
    private var fontWidth: Int = 0

    enum class Length (val duration: Float // in seconds
    ) {
        SHORT(1.5f),
        LONG(3.5f)
    }

    init {

        this.timeToLive = length.duration
        renderer.color = backgroundColor

        // measure text box
        val layoutSimple = GlyphLayout()
        layoutSimple.setText(this.font, msg)

        val lineHeight = layoutSimple.height.toInt()
        fontWidth = layoutSimple.width.toInt()
        var fontHeight = layoutSimple.height.toInt()

        val margin = customMargin ?: lineHeight * 2

        val screenWidth = Gdx.graphics.width.toFloat()
        val maxTextWidth = screenWidth * maxRelativeWidth
        if (fontWidth > maxTextWidth) {
            val cache = BitmapFontCache(this.font, true)
            val layout = cache.addText(msg, 0f, 0f, maxTextWidth, Align.center, true)
            fontWidth = layout.width.toInt()
            fontHeight = layout.height.toInt()
        }

        toastHeight = fontHeight + 2 * margin
        toastWidth = fontWidth + 2 * margin

        positionX = screenWidth / 2 - toastWidth / 2

        fontX = positionX + margin
        fontY = positionY + margin.toFloat() + fontHeight.toFloat()
    }

    /**
     * Displays toast<br></br>
     * Must be called at the end of [Game.render]<br></br>
     * @param delta [Graphics.getDeltaTime]
     * @return activeness of the toast (true while being displayed, false otherwise)
     */
    fun render(delta: Float): Boolean {
        timeToLive -= delta

        if (timeToLive < 0) {
            return false
        }

        renderer.begin(ShapeRenderer.ShapeType.Filled)
        //renderer.circle(positionX, positionY + toastHeight / 2, (toastHeight / 2).toFloat())
        renderer.rect(positionX, positionY, toastWidth.toFloat(), toastHeight.toFloat())
        //renderer.circle(positionX + toastWidth, positionY + toastHeight / 2, (toastHeight / 2).toFloat())
        renderer.end()

        spriteBatch.begin()

        if (timeToLive > 0 && opacity > 0.15) {
            if (timeToLive < fadingDuration) {
                opacity = timeToLive / fadingDuration
            }

            font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * opacity)
            font.draw(spriteBatch, msg, fontX, fontY, fontWidth.toFloat(), Align.center, true)
        }

        spriteBatch.end()

        return true
    }

    /**
     * Factory for creating toasts
     */
    class ToastFactory private constructor() {

        private var font: BitmapFont = BitmapFont()
        private var backgroundColor = Color(55f / 256, 55f / 256, 55f / 256, 1f)
        private var fontColor = Color(1f, 1f, 1f, 1f)
        private var positionY: Float = 0.toFloat()
        private var fadingDuration = 0.5f
        private var maxRelativeWidth = 0.65f
        private var customMargin: Int? = null

        init {
            val screenHeight = Gdx.graphics.height.toFloat()
            val bottomGap = 100f
            positionY = bottomGap + (screenHeight - bottomGap) / 10
        }

        /**
         * Creates new toast
         * @param text message
         * @param length toast duration
         * @return newly created toast
         */
        fun create(text: String, length: Length): Toast {
            return Toast(
                    text,
                    length,
                    font,
                    backgroundColor,
                    fadingDuration,
                    maxRelativeWidth,
                    fontColor,
                    positionY,
                    customMargin)
        }

        /**
         * Builder for creating factory
         */
        class Builder {

            private var built = false
            private val factory = ToastFactory()

            /**
             * Specify font for toasts
             * @param font font
             * @return this
             */
            fun font(font: BitmapFont): Builder {
                check()
                factory.font = font
                return this
            }

            /**
             * Specify background color for toasts.<br></br>
             * Note: Alpha channel is not supported (yet).<br></br>
             * Default: rgb(55,55,55)
             * @param color background color
             * @return this
             */
            fun backgroundColor(color: Color): Builder {
                check()
                factory.backgroundColor = color
                return this
            }

            /**
             * Specify font color for toasts.<br></br>
             * Default: white
             * @param color font color
             * @return this
             */
            fun fontColor(color: Color): Builder {
                check()
                factory.fontColor = color
                return this
            }

            /**
             * Specify vertical position for toasts<br></br>
             * Default: bottom part
             * @param positionY vertical position of bottom left corner
             * @return this
             */
            fun positionY(positionY: Float): Builder {
                check()
                factory.positionY = positionY
                return this
            }

            /**
             * Specify fading duration for toasts<br></br>
             * Default: 0.5s
             * @param fadingDuration duration in seconds which it takes to disappear
             * @return this
             */
            fun fadingDuration(fadingDuration: Float): Builder {
                check()
                if (fadingDuration < 0) {
                    throw IllegalArgumentException("Duration must be non-negative number")
                }
                factory.fadingDuration = fadingDuration
                return this
            }

            /**
             * Specify max text width for toasts<br></br>
             * Default: 0.65
             * @param maxTextRelativeWidth max text width relative to screen (Eg. 0.5 = max text width is equal to 50% of screen width)
             * @return this
             */
            fun maxTextRelativeWidth(maxTextRelativeWidth: Float): Builder {
                check()
                factory.maxRelativeWidth = maxTextRelativeWidth
                return this
            }

            /**
             * Specify text margin for toasts<br></br>
             * Default: line height
             * @param margin margin in px
             * @return this
             */
            fun margin(margin: Int): Builder {
                check()
                factory.customMargin = margin
                return this
            }

            /**
             * Builds factory
             * @return new factory
             */
            fun build(): ToastFactory {
                check()
                built = true
                return factory
            }

            private fun check() {
                if (built) {
                    throw IllegalStateException("Builder can be used only once")
                }
            }
        }
    }

}