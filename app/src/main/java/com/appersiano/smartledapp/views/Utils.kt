package com.appersiano.smartledapp.views

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import kotlin.math.ln
import kotlin.math.pow

private const val TAG = "LEDTemperatureUtils"
object LEDTemperatureUtils {

    fun generateTemperatureArray(@androidx.annotation.IntRange(from = 120) size: Int): List<Color> {
        val listTemp = mutableListOf<Color>()
        val startKelvin = 1000
        val endKelvin = 15000
        val setStep = (endKelvin - startKelvin) / (size - 1)

        for (i in startKelvin..endKelvin step setStep) {
            val(red,green,blue) = (i.toFloat().toRgb())
            val fa = floatArrayOf(0f, 0f, 0f)
            ColorUtils.RGBToHSL(
                red.toInt(),
                green.toInt(),
                blue.toInt(),
                fa
            )
            listTemp.add(Color.hsl(fa[0], fa[1], fa[2]))
        }

        return listTemp
    }


    private const val MAX = 255.0
    private const val MIN = 0.0

    private fun Float.toRgb() = convertKelvinToRgb(this)

    private fun convertKelvinToRgb(kelvin: Float): Triple<Float, Float, Float> {

        val temp = (kelvin.div(100.0))

        var red: Double?
        var green: Double?
        var blue: Double?


        if (temp <= 66.0) {
            red = MAX
            green = temp
            green = (99.4708025861.times(ln(green))).minus(161.1195681661)


            if (temp <= 19.0) {
                blue = MIN
            } else {
                blue = temp.minus(10.0)
                blue = (138.5177312231.times(ln(blue))).minus(305.0447927307)
            }
        } else {
            red = temp.minus(60.0)
            red = 329.698727446.times(red.pow(-0.1332047592))
            green = temp.minus(60.0)
            green = 288.1221695283.times(green.pow(-0.0755148492))
            blue = MAX
        }


        return Triple(clamp(red, MIN, MAX), clamp(green, MIN, MAX), clamp(blue, MIN, MAX))

    }

    private fun clamp(value: Double, min: Double, max: Double): Float {
        if (value < min) return min.toFloat()

        if (value > max) return max.toFloat()

        return value.toFloat()
    }

}