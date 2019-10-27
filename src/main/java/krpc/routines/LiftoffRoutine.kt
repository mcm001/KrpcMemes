package krpc.routines

import krpc.client.services.SpaceCenter
import krpc.crafts.FalconCraft
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.second
import org.javatuples.Triplet
import java.lang.Thread.sleep
import kotlin.math.pow
import kotlin.math.sqrt

class LiftoffRoutine: () -> Unit {

    override fun invoke() {
        println("Engaging liftoff")
        FalconCraft.autoPilotEngaged = true
        FalconCraft.setGear(true)
        FalconCraft.direction = Translation3d(90.0, 0.0, 0.0)
        FalconCraft.throttle = 0.6
        sleep(500)

        hover(10.second)
        FalconCraft.throttle = 0.0

    }

    fun hover(time: SIUnit<Second>) {
        val startTime = FalconCraft.wrappedVessel.met
        val endTime = startTime + time.second
        while(FalconCraft.wrappedVessel.met < endTime) {
            val twr = FalconCraft.twr
            println("TWR $twr")
            val thrustNeeded = 1.0 / twr
            FalconCraft.throttle = thrustNeeded
        }
        println("Done with hover")
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
