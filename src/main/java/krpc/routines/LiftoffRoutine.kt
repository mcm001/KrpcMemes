package krpc.routines

import krpc.crafts.HoverCraft
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import java.lang.Thread.sleep

class LiftoffRoutine: () -> Unit {

    override fun invoke() {
        HoverCraft.autoPilotEngaged = true
        HoverCraft.direction = Translation3d(90.0, 0.0, 0.0)
        HoverCraft.throttle = 1.0
        while (true) {

        }
    }

}