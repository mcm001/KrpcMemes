package krpc

import java.io.IOException

import krpc.client.Connection
import krpc.client.RPCException
import krpc.client.Stream
import krpc.client.StreamException
import krpc.client.services.SpaceCenter
import krpc.client.services.SpaceCenter.ControlInputMode
import krpc.client.services.SpaceCenter.Flight
import krpc.client.services.SpaceCenter.ReferenceFrame
import krpc.client.services.SpaceCenter.Resources
import krpc.client.services.SpaceCenter.SASMode
import krpc.crafts.HoverCraft
import krpc.routines.RoutineManager
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.javatuples.Triplet



object Main {


    @Throws(IOException::class, RPCException::class, InterruptedException::class, StreamException::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val craft = HoverCraft

        while (true) {
//            craft.update()
//            Thread.sleep((1.0 / 40.0).toLong())
            RoutineManager.start()
        }
    }


    data class Location(val latitude: Double, val longitude: Double)

    val targetLocation = Location(-0.046, -74.6)

    val maxForwardSpeed = 20.0
    val maxLateralSpeed = 20.0
    val maxForwardAngle = 20.0

    const val kForwardKp = 0.7
    const val kRollKp = 0.3

    private fun Number.boundTo(min: Double, max: Double): Double = when {
        toDouble() < min -> min
        toDouble() > max -> max
        else -> toDouble()
    }

}