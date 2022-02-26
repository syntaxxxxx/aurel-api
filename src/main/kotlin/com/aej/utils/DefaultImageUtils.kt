package com.aej.utils

import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object DefaultImageUtils {
    private const val IMAGE_WIDTH = 200
    private const val IMAGE_HEIGHT = 200

    private val bgColors = arrayOf(
        Color.decode("#BB927B"),
        Color.decode("#DAB073"),
        Color.decode("#D56328"),
        Color.decode("#30383D"),
        Color.decode("#87A1AB"),
        Color.decode("#2A2829"),
        Color.decode("#4A4C48"),
        Color.decode("#7398B7"),
        Color.decode("#6CB8D2")
    )

    private fun getRandomColor(): Color {
        return bgColors.random()
    }

    fun createImage(text: String): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val bufferedImage = BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB)
        val graphic = bufferedImage.createGraphics()
        val font = Font("Arial", Font.BOLD, 130)

        graphic.apply {
            setFont(font)
            val metrics = getFontMetrics(font)
            val positionX = (IMAGE_WIDTH - metrics.stringWidth(text)) / 2
            val positionY = (IMAGE_HEIGHT - metrics.height) / 2 + metrics.ascent

            color = getRandomColor()
            fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT)

            color = Color.WHITE
            drawString(text, positionX, positionY)
            graphic.dispose()
        }

        ImageIO.write(bufferedImage, "png", outputStream)
        return outputStream.toByteArray()
    }
}