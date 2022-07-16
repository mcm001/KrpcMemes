package krpc.routines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object RoutineManager {

    val liftoffRoutine = LiftoffRoutine()

    fun start() {
        routine()
    }

    fun routine() {
        println("Starting routine...")
        liftoffRoutine()
    }

}

fun main() = RoutineManager.routine()
