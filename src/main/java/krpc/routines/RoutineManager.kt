package krpc.routines

object RoutineManager {

    val liftoffRoutine = LiftoffRoutine()

    fun start() {
        Thread(this::routine).start()
    }

    fun routine() {
        println("Starting routine...")
        liftoffRoutine()
    }

}