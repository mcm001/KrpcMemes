package krpc.crafts

import krpc.client.Connection
import krpc.client.services.SpaceCenter
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.degree
import org.javatuples.Triplet
import kotlin.properties.Delegates

abstract class AbstractCraft {

    val connecc = Connection.newInstance()
    val spaceCenterInstance = SpaceCenter.newInstance(connecc)
    val wrappedVessel: SpaceCenter.Vessel = spaceCenterInstance.activeVessel
    val directionOffset = Translation3d.kZero

    val wrappedControl = wrappedVessel.control
    private val autoPilot = wrappedVessel.autoPilot

    val srfFlight: SpaceCenter.Flight = wrappedVessel.flight(wrappedVessel.orbit.body.referenceFrame)

    // this is called periodically to update shit
    abstract fun update()

    /**
     * The home location in the surface reference frame (KSC runway), in the form (lat, lng)
     */
    var homeLocation = Translation2d(-74.7, -0.046)
        protected set

    /**
     * Get the current position of the vessel in the surface reference frame
     * Longitude (left/right on the globe) is on the x axis, and latitude is on the y axis
     */
    val globalLatLng: Translation2d
        get() = Translation2d(srfFlight.longitude, srfFlight.latitude)

    /**
     * @return the location relative to the homeLocation
     */
    val globalPose: Pose2d
        get() = Pose2d(globalLatLng, srfFlight.heading.degree)

    var throttle
        get() = wrappedControl.throttle.toDouble()
        set(value) {
            wrappedControl.throttle = value.toFloat()
        }

    val pitch = srfFlight.pitch.toDouble()
    val roll = srfFlight.roll.toDouble()
    val heading = srfFlight.heading.toDouble()

    var direction
        get() = Translation3d(pitch, heading, roll) - directionOffset
        set(value) {
//            println("targeting pitch ${value.x} heading ${value.y} roll ${value.z}")
            autoPilot.targetRoll = value.z.toFloat()
            autoPilot.targetPitchAndHeading(value.x.toFloat(), value.y.toFloat())
        }

    var autoPilotEngaged by Delegates.observable(false) {
        _,_, newValue -> run {
            if(newValue) wrappedControl.sas = false
            if(newValue) autoPilot.engage() else autoPilot.disengage()
        }
    }

    protected operator fun Translation3d.minus(other: Translation3d) = Translation3d(x - other.x, y - other.y, z - other.z)

}