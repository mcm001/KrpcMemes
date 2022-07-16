package krpc.scripts

import krpc.client.Connection
import krpc.client.RPCException
import krpc.client.services.Drawing
import krpc.client.services.SpaceCenter
import org.javatuples.Quartet
import org.javatuples.Triplet

import java.io.IOException
import java.lang.Math

object LandingGuidance {
    @Throws(IOException::class, RPCException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val connection = Connection.newInstance("Landing Site")
        val spaceCenter = SpaceCenter.newInstance(connection)
        val drawing = Drawing.newInstance(connection)
        val vessel = spaceCenter.activeVessel
        val body = vessel.orbit.body

        // Define the landing site as the top of the VAB
        val landingLatitude = -(0.0 + 5.0 / 60.0 + 48.38 / 60.0 / 60.0)
        val landingLongitude = -(74.0 + 37.0 / 60.0 + 12.2 / 60.0 / 60.0)
        val landingAltitude = 111.0

        // Determine landing site reference frame
        // (orientation: x=zenith, y=north, z=east)
        val landingPosition = body.surfacePosition(
                landingLatitude, landingLongitude, body.referenceFrame)
        val qLong = Quartet(
                0.0,
                Math.sin(-landingLongitude * 0.5 * Math.PI / 180.0),
                0.0,
                Math.cos(-landingLongitude * 0.5 * Math.PI / 180.0))
        val qLat = Quartet(
                0.0,
                0.0,
                Math.sin(landingLatitude * 0.5 * Math.PI / 180.0),
                Math.cos(landingLatitude * 0.5 * Math.PI / 180.0))
        val qIdentity = Quartet(0.0, 0.0, 0.0, 1.0)
        val zero = Triplet(0.0, 0.0, 0.0)
        val landingReferenceFrame = SpaceCenter.ReferenceFrame.createRelative(
                connection,
                SpaceCenter.ReferenceFrame.createRelative(
                        connection,
                        SpaceCenter.ReferenceFrame.createRelative(
                                connection,
                                body.referenceFrame,
                                landingPosition, qLong, zero, zero),
                        zero, qLat, zero, zero),
                Triplet(landingAltitude, 0.0, 0.0),
                qIdentity, zero, zero)

        // Draw axes
        drawing.addLine(
                zero, Triplet(1.0, 0.0, 0.0),
                landingReferenceFrame, true)
        drawing.addLine(
                zero, Triplet(0.0, 1.0, 0.0),
                landingReferenceFrame, true)
        drawing.addLine(
                zero, Triplet(0.0, 0.0, 1.0),
                landingReferenceFrame, true)

        while (true){
            println("hi")
            Thread.sleep(1000)
        }
    }
}
