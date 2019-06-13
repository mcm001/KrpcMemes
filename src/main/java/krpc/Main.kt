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
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.javatuples.Triplet



object Main {


    @Throws(IOException::class, RPCException::class, InterruptedException::class, StreamException::class)
    @JvmStatic
    fun main(args: Array<String>) {

        val craft = HoverCraft

//        val connection = Connection.newInstance("Launch into orbit")
//        val spaceCenter = SpaceCenter.newInstance(connection)
//        val vessel = spaceCenter.activeVessel
//
//        println("Current max thrust: " + spaceCenter.activeVessel.maxThrust)
//
//        // float turnStartAltitude = 250;
//        // float turnEndAltitude = 45000;
//        // float targetAltitude = 150000;
//
//        // Set up streams for telemetry
//        spaceCenter.ut
//        val ut = connection.addStream<Double>(SpaceCenter::class.java, "getUT")
//        val refFrame = vessel.surfaceReferenceFrame
//        val flight = vessel.flight(refFrame)
//        val altitude = connection.addStream<Double>(flight, "getMeanAltitude")
//        val apoapsis = connection.addStream<Double>(vessel.orbit, "getApoapsisAltitude")
//        val stage2Resources = vessel.resourcesInDecoupleStage(2, false)
//        val srbFuel = connection.addStream<Float>(stage2Resources, "amount", "SolidFuel")
//
//        val stage6Resources = vessel.resourcesInDecoupleStage(6, false)
//
//
//        val liquidFuel = connection.addStream<Float>(stage6Resources, "amount", "LiquidFuel")
//
//        // Pre-launch setup
//        vessel.control.sas = false
//        vessel.control.rcs = false
//        vessel.control.throttle = 1f
//
//        // configure the auto pilot
//        val mAutopilot = vessel.autoPilot
//        // mAutopilot.setAutoTune(value);
//
//        mAutopilot.targetPitchAndHeading(0f, 90f)
//        mAutopilot.targetRoll = 0f
//        mAutopilot.engage()
//
//        val controler = vessel.control
//        val surfaceRefFrame = vessel.flight(vessel.orbit.body.referenceFrame)
//        // controler.setInputMode(ControlInputMode.OVERRIDE);
//
//        val targetAltitude = 200//flight.meanAltitude + 50
//
//        // vessel.getAutoPilot().setSASMode(SASMode.);
//
        while (true) {
//
//            val twr = vessel.maxThrust /* newtons */ / (vessel.mass * 9.81)
//            val throttleToGetTWROf1 = 1 / twr
//            val verticalAeroForceNewtons = flight.aerodynamicForce.value0
//            val throttleToCounteractAeroForces = verticalAeroForceNewtons!! / vessel.maxThrust
//            val altitudeErr = targetAltitude - flight.meanAltitude
//            val velocitySetpoint = altitudeErr * 1
//            val velocityErr = surfaceRefFrame.verticalSpeed
//            val velocityInput = velocityErr * 0.1
//            val throttleToApply = throttleToGetTWROf1 + throttleToCounteractAeroForces + velocityInput
//            vessel.control.throttle = throttleToApply.toFloat()
//
//
//
//            val velocity = vessel.flight(ReferenceFrame.createHybrid(
//                    connection,
//                    vessel.orbit.body.referenceFrame,
//                    vessel.surfaceReferenceFrame,
//                    vessel.orbit.body.referenceFrame,
//                    vessel.orbit.body.referenceFrame)).velocity
//
//
//            val forwardSpeed = velocity.value2
//            val lateralSpeed = velocity.value1 * -1 // right is positive
//
//
//            val loc = Location(flight.latitude, flight.longitude)
//
//
//
//            fun dotProduct(u : Triplet<Double,Double,Double>,
//                    v: Triplet<Double,Double,Double>) : Double {
//                return u.value0 * v.value0 +
//                        u.value1 * v.value1 +
//                        u.value2 * v.value2
//            }
//
//            fun magnitude(v: Triplet<Double, Double, Double>): Double {
//                return Math.sqrt(dotProduct(v, v));
//            }
//
//            // Compute the angle between vector x and y
//            fun angleBetweenVectors(u: Triplet<Double,Double,Double>,
//                   v: Triplet<Double,Double,Double>) : Double {
//                val dp = dotProduct(u, v)
//                if (dp == 0.0) {
//                    return 0.0
//                }
//                val um = magnitude(u)
//                val vm = magnitude(v)
//                return Math.acos(dp / (um * vm)) * (180f / Math.PI)
//            }
//
//
//            val vesselDirection = vessel.direction(vessel.surfaceReferenceFrame)
//
//            // Get the direction of the vessel in the horizon plane
//            val horizonDirection = Triplet(
//                    0.0, vesselDirection.value1, vesselDirection.value2)
//
//            // Compute the pitch - the angle between the crafts direction
//            // and the direction in the horizon plane
//            var pitch = angleBetweenVectors(vesselDirection, horizonDirection)
//            if (vesselDirection.value0 < 0) {
//                pitch = -pitch
//            }
//
//            // Compute the heading - the angle between north
//            // and the direction in the horizon plane
//            val north = Triplet(0.0, 1.0, 0.0)
//            var heading = angleBetweenVectors(north, horizonDirection)
//            if (horizonDirection.value2 < 0) {
//                heading = 360 - heading
//            }
//
//            println("heading $heading")
//
////            val vesselPose = Pose2d(
////                    Translation2d(
////                            loc.longitude, loc.latitude
////                    ), heading
////            )
//
//            // assuming that we are pointing east. That means that latitude is in the roll axis and longitude is in the pitch axis
////            println("location ${loc.latitude}, ${loc.longitude}")
//
//            // to the east of ksc, -0.1057, -73.9078
//            // runway: -0.046, -74.7
//
//            val targetForwardSpeed = (-(loc.longitude - targetLocation.longitude) * 1000.0).boundTo(
//                    -maxForwardSpeed, maxForwardSpeed
//            )
//            val targetLateralSpeed = ((loc.latitude - targetLocation.latitude) * 1000.0).boundTo(
//                    -maxLateralSpeed, maxLateralSpeed
//            )
//
//            println("target forward $targetForwardSpeed lateral $targetLateralSpeed current forward $forwardSpeed current lateral $lateralSpeed")
//
//            var forwardInput = ((forwardSpeed!! - targetForwardSpeed) * kForwardKp)
//                    .boundTo(-maxForwardAngle, maxForwardAngle)
////            forwardInput = Math.max(Math.min(forwardInput, maxForwardAngle.toDouble()), (-maxForwardAngle).toDouble())
//            // System.out.println("Forward speed: " + forwardSpeed + " Forward input: " + forwardInput);
//            mAutopilot.targetPitch = forwardInput.toFloat()
//
//            var rollInput = (targetLateralSpeed - lateralSpeed) * kRollKp
//            rollInput = Math.max(Math.min(rollInput, maxForwardAngle.toDouble()), (-maxForwardAngle).toDouble())
////            println("Roll angle: $lateralSpeed Roll input: $rollInput")
//            mAutopilot.targetRoll = rollInput.toFloat()
//
//            mAutopilot.targetHeading = 90.0F

            craft.update()

            Thread.sleep((1.0 / 40.0).toLong())
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