package krpc;

import java.io.IOException;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Resources;

public class Main {
  public static void main(String[] args)
    throws IOException, RPCException, InterruptedException, StreamException {
      Connection connection = Connection.newInstance("Launch into orbit");
      SpaceCenter spaceCenter = SpaceCenter.newInstance(connection);
      SpaceCenter.Vessel vessel = spaceCenter.getActiveVessel();

      System.out.println("Current max thrust: " + spaceCenter.getActiveVessel().getMaxThrust());

      float turnStartAltitude = 250;
      float turnEndAltitude = 45000;
      float targetAltitude = 150000;

      // Set up streams for telemetry
      spaceCenter.getUT();
      Stream<Double> ut = connection.addStream(SpaceCenter.class, "getUT");
      ReferenceFrame refFrame = vessel.getSurfaceReferenceFrame();
      Flight flight = vessel.flight(refFrame);
      Stream<Double> altitude = connection.addStream(flight, "getMeanAltitude");
      Stream<Double> apoapsis =
          connection.addStream(vessel.getOrbit(), "getApoapsisAltitude");
      Resources stage2Resources = vessel.resourcesInDecoupleStage(2, false);
      Stream<Float> srbFuel =
          connection.addStream(stage2Resources, "amount", "SolidFuel");

          Resources resources = vessel.getResources();


          Stream<Float> liquidFuel =
          connection.addStream(resources, "amount", "LiquidFuel");

      // Pre-launch setup
      vessel.getControl().setSAS(false);
      vessel.getControl().setRCS(false);
      vessel.getControl().setThrottle(1);

      // Countdown...
      System.out.println("3...");
      Thread.sleep(1000);
      System.out.println("2...");
      Thread.sleep(1000);
      System.out.println("1...");
      Thread.sleep(1000);

      while(true) {
        System.out.println("Liquid fuel: " + liquidFuel.get());
      }

      // // Activate the first stage
      // vessel.getControl().activateNextStage();
      // vessel.getAutoPilot().engage();
      // vessel.getAutoPilot().targetPitchAndHeading(90, 90);

      // // Main ascent loop
      // boolean srbsSeparated = false;
      // double turnAngle = 0;
      // while (true) {

      //     // Gravity turn
      //     if (altitude.get() > turnStartAltitude &&
      //         altitude.get() < turnEndAltitude) {
      //         double frac = (altitude.get() - turnStartAltitude)
      //                       / (turnEndAltitude - turnStartAltitude);
      //         double newTurnAngle = frac * 90.0;
      //         if (Math.abs(newTurnAngle - turnAngle) > 0.5) {
      //             turnAngle = newTurnAngle;
      //             vessel.getAutoPilot().targetPitchAndHeading(
      //                 (float)(90 - turnAngle), 90);
      //         }
      //     }

      //     // Separate SRBs when finished
      //     if (!srbsSeparated) {
      //       if (srbFuel.get() < 0.1) {
      //             vessel.getControl().activateNextStage();
      //             srbsSeparated = true;
      //             System.out.println("SRBs separated");
      //         }
      //     }

      //     // Decrease throttle when approaching target apoapsis
      //     if (apoapsis.get() > targetAltitude * 0.9) {
      //         System.out.println("Approaching target apoapsis");
      //         break;
      //     }
      // }
  }
}