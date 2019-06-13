package krpc.crafts

//import krpc.Location
import krpc.Main
import krpc.boundTo
import krpc.client.Connection
import krpc.client.services.SpaceCenter
import krpc.rotateBy
import krpc.rounded
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.degree

object HoverCraft : AbstractCraft() {

    private const val MAX_VERT_SPEED: Double = 10.0

    var targetAltitude = 200

    val whackyVelocityReferenceFrame = wrappedVessel.flight(SpaceCenter.ReferenceFrame.createHybrid(
            connecc,
            wrappedVessel.orbit.body.referenceFrame,
            wrappedVessel.surfaceReferenceFrame,
            wrappedVessel.orbit.body.referenceFrame,
            wrappedVessel.orbit.body.referenceFrame))

    override fun update() {

        autoPilotEngaged = true

        val newThrottle = calculateThrottle()
        throttle = newThrottle

        calculateLateralVelocities(midRunayLocation, globalPose)

        val newDirection = calculateDirection(0.0, 0.0)
        direction = newDirection

    }

    private fun calculateLateralVelocities(targetLocation: Translation2d, currentLocation: Pose2d) {

        val error = (targetLocation - currentLocation.translation)

//        print("error ${error.x}, ${error.y} ")

        // borrowed from the Translation2d constructor using a heading and magnitude
        val rotatedError = error.rotateBy(currentLocation.rotation + 90.degree) // because we want
        // the new reference frame to be X axis forward

        println("rotated error ${rotatedError.x}, ${rotatedError.y}")

    }

    private fun calculateDirection(targetForwardSpeed: Double, targetLateralSpeed: Double, targetHeading: Double = 90.0): Translation3d {

        val velocity = whackyVelocityReferenceFrame.velocity
        val forwardSpeed = velocity.value2
        val lateralSpeed = velocity.value1 * -1

        val forwardInput = ((forwardSpeed!! - targetForwardSpeed) * kForwardKp)
                    .boundTo(-maxForwardAngle, maxForwardAngle)

        val rollInput = ((targetLateralSpeed - lateralSpeed) * kRollKp)
                    .boundTo(-maxForwardAngle, maxForwardAngle)

//        println("direction = pitch ${forwardInput.rounded()} yaw ${targetHeading.rounded()} roll ${rollInput.rounded()} \n")

        return Translation3d(forwardInput, targetHeading, rollInput)
    }

    private fun calculateThrottle(): Double {
        val twr = wrappedVessel.maxThrust /* newtons */ / (wrappedVessel.mass * 9.81)
        val throttleToGetTWROf1 = 1 / twr
        val verticalAeroForceNewtons = srfFlight.aerodynamicForce.value0
        val throttleToCounteractAeroForces = verticalAeroForceNewtons!! / wrappedVessel.maxThrust
        val altitudeErr = targetAltitude - srfFlight.meanAltitude
        val velocitySetpoint = (altitudeErr * kVerticalPositionKp).boundTo(-MAX_VERT_SPEED, MAX_VERT_SPEED)
        val verticalSpeed = srfFlight.verticalSpeed
        val velocityErr = velocitySetpoint - verticalSpeed
        val velocityInput = (velocityErr * kVerticalVelocityKp)
        val throttleToApply = throttleToGetTWROf1 + throttleToCounteractAeroForces + velocityInput
        return throttleToApply.boundTo(0.0, 1.0)
    }

}

val midRunayLocation = Translation2d(-74.6, -0.046)


const val kVerticalVelocityKp = 0.2
const val kVerticalPositionKp = 1

val maxForwardSpeed = 20.0
val maxLateralSpeed = 20.0
val maxForwardAngle = 20.0

const val kForwardKp = 2.2
const val kRollKp = 0.3