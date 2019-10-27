package krpc.routines

import krpc.client.services.SpaceCenter
import krpc.crafts.FalconCraft
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.utils.safeRangeTo
import org.javatuples.Triplet
import wpi.TrapezoidProfile
import java.lang.Thread.sleep
import kotlin.math.*

class LiftoffRoutine: () -> Unit {

    override fun invoke() {
        println("Engaging liftoff")
        FalconCraft.autoPilotEngaged = true
        FalconCraft.setGear(true)
        FalconCraft.direction = Translation3d(90.0, 0.0, 0.0)
//        FalconCraft.throttle = 0.6
//        sleep(500)

//        hover(10.second)
        hoverAt(300.0)
        FalconCraft.throttle = 0.0
        waitForLand(100.0)
        while(FalconCraft.verticalVelcoity < -5.0) FalconCraft.throttle = 1.0
        hoverAt(100.0, false)
//        while(true) hoverAt(100.0)

    }

    fun getTWRof1(): Double {
        val thrust /* newtons */ = FalconCraft.maxThrust
        val gravity = /* mg */ FalconCraft.mass * FalconCraft.wrappedVessel.orbit.body.surfaceGravity
        val drag = FalconCraft.srfFlight.drag.norm().withSign(
                FalconCraft.verticalVelcoity
        ) // newtons
        val thrustNeeded = (gravity + drag)/thrust
//        println("need $thrustNeeded")
        return thrustNeeded
    }

    fun hover(time: SIUnit<Second>) {
        val thrust = FalconCraft.maxThrust
        val startTime = FalconCraft.wrappedVessel.met
        val endTime = startTime + time.second
        while(FalconCraft.wrappedVessel.met < endTime) {
            val thrustNeeded = getTWRof1()
            if(thrust != 0.0) FalconCraft.throttle = thrustNeeded
        }
        println("Done with hover")
    }

    fun waitForLand(height: Double) {
        while (true) {
            val cAlt = FalconCraft.surfaceAltitude
            val cVel = FalconCraft.verticalVelcoity
            val acceleation = (FalconCraft.wrappedVessel.maxThrust / FalconCraft.mass) - 9.8

            /*
            if a is constant
            v(t) = a * t + vi
            s(t) = a* t^2 / 2 + vi * t + si
             */

            var zeroVelT = 0.0
            fun pos(t: Double) = acceleation * t.pow(2.0) / 2.0 + cVel * t + cAlt
            fun vel(t: Double) = acceleation * t + cVel
            while(vel(zeroVelT) < 0.0) {
                zeroVelT += 0.020
            }
            val zeroVelPos = pos(zeroVelT)

            println("current alt $cAlt, position is $zeroVelPos")

            if(zeroVelPos < height) {
                println("DONE!") ; return
            }

            sleep(20)
        }
    }

    fun hoverAt(height: Double, end: Boolean = true) {
        FalconCraft.throttle = 0.01
        val goal = TrapezoidProfile.State(height, 0.0)
        val constraints = TrapezoidProfile.Constraints(15.0, 25.0)
        val velocityRange = (-10.0..10.0)
        var lastTime = FalconCraft.wrappedVessel.met
        while(true) {
            val cState = TrapezoidProfile.State(FalconCraft.surfaceAltitude, FalconCraft.verticalVelcoity)
            val time = FalconCraft.wrappedVessel.met
            if (time != lastTime) {
                lastTime = time
                if((cState.position - goal.position).absoluteValue < 0.2 && end) return

                val profile = TrapezoidProfile(constraints, goal, cState)
                val setpoint = profile.calculate(0.02)
    //            println("calculated new state: position [${setpoint.position}], velo [${setpoint.velocity}]")

                // p loop on position
                val wantedPosition = setpoint.position
                val currentPosition = FalconCraft.surfaceAltitude
                val _error = wantedPosition - currentPosition
                val veloSetpoint = (_error * 0.5).coerceIn(velocityRange)
//                println("error ${_error.roundToInt()} out ${veloSetpoint.roundToInt()}")

                // p loop on velocity
                val wantedVelocity = /*veloSetpoint +*/ setpoint.velocity //setpoint.velocity
                val currentVelocity = FalconCraft.verticalVelcoity
                val error = wantedVelocity - currentVelocity
                val velocityOutput = error * 0.6

                val feedForward = getTWRof1()

                println("altitude ${cState.position}")

                FalconCraft.throttle = feedForward + velocityOutput
                sleep(20)
            }
        }
    }

    fun targetVelocity(velocity: Double, time: Double) {
        val startTime = FalconCraft.wrappedVessel.met
        val endTime = startTime + time
        val flight = FalconCraft.wrappedVessel.flight(FalconCraft.wrappedVessel.surfaceReferenceFrame)
        while(FalconCraft.wrappedVessel.met < endTime) {
            val currentVelocity = flight.velocity.norm()
            println(currentVelocity)
        }
    }

}

fun Triplet<Double, Double, Double>.norm() = sqrt(value0.pow(2) + value1.pow(2) + value2.pow(2))
