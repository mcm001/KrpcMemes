package krpc

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.Rotation2d
import java.math.RoundingMode
import java.text.DecimalFormat

val decimalFormat = DecimalFormat()

fun Number.boundTo(min: Double, max: Double): Double = when {
    toDouble() < min -> min
    toDouble() > max -> max
    else -> toDouble()
}

fun Number.rounded() = decimalFormat.format(toDouble())

fun Translation2d.rotateBy(rotation: Rotation2d) = Translation2d(norm * rotation.cos,
        norm * rotation.sin)