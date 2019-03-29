package krpc;

import java.io.IOException;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.Stream;
import krpc.client.StreamException;
import krpc.client.services.SpaceCenter;
import krpc.client.services.SpaceCenter.ControlInputMode;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Resources;
import krpc.client.services.SpaceCenter.SASMode;

public class Main {
  public static void main(String[] args)
    throws IOException, RPCException, InterruptedException, StreamException {
      Connection connection = Connection.newInstance("Launch into orbit");
      SpaceCenter spaceCenter = SpaceCenter.newInstance(connection);
      SpaceCenter.Vessel vessel = spaceCenter.getActiveVessel();

      System.out.println("Current max thrust: " + spaceCenter.getActiveVessel().getMaxThrust());

      // float turnStartAltitude = 250;
      // float turnEndAltitude = 45000;
      // float targetAltitude = 150000;

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

          Resources stage6Resources = vessel.resourcesInDecoupleStage(6, false);
          

          Stream<Float> liquidFuel =
          connection.addStream(stage6Resources, "amount", "LiquidFuel");

      // Pre-launch setup
      vessel.getControl().setSAS(false);
      vessel.getControl().setRCS(false);
      vessel.getControl().setThrottle(1);

      // Countdown...
      // System.out.println("3...");
      // Thread.sleep(1000);
      // System.out.println("2...");
      // Thread.sleep(1000);
      // System.out.println("1...");
      // Thread.sleep(1000);

      // // Activate the first stage
      // vessel.getControl().activateNextStage();

      // configure the auto pilot
      var mAutopilot = vessel.getAutoPilot();
      // mAutopilot.setAutoTune(value);

      mAutopilot.targetPitchAndHeading(0, 90);
      mAutopilot.setTargetRoll(0);
      mAutopilot.engage();

      var controler = vessel.getControl();
      var surfaceRefFrame = vessel.flight(vessel.getOrbit().getBody().getReferenceFrame());
      // controler.setInputMode(ControlInputMode.OVERRIDE);

      final double targetAltitude = flight.getMeanAltitude() + 50;

      // vessel.getAutoPilot().setSASMode(SASMode.);

      while(true) {

      //   System.out.println("Current PID GAINZ: Pitch: " + 
      //   String.format("P(%s) I(%s) D(%s) yaw P(%s) I(%s) D(%s) roll P(%s) I(%s) D(%s)", 
      //   mAutopilot.getPitchPIDGains().getValue0(),
      //   mAutopilot.getPitchPIDGains().getValue1(),
      //   mAutopilot.getPitchPIDGains().getValue2(),
      //   mAutopilot.getYawPIDGains().getValue0(),
      //   mAutopilot.getYawPIDGains().getValue1(),
      //   mAutopilot.getYawPIDGains().getValue2(),
      //   mAutopilot.getRollPIDGains().getValue0(),
      //   mAutopilot.getRollPIDGains().getValue1(),
      //   mAutopilot.getRollPIDGains().getValue2()
        
      //   )
      // );

        var twr = vessel.getMaxThrust() /* newtons */ / (vessel.getMass() * 9.81);
        var throttleToGetTWROf1 = 1/twr;

        var verticalAeroForceNewtons = flight.getAerodynamicForce().getValue0();
        // var b = flight.getAerodynamicForce().getValue1();
        // var c = flight.getAerodynamicForce().getValue2();

        var throttleToCounteractAeroForces = verticalAeroForceNewtons / vessel.getMaxThrust();

        // final double targetAltitude = 50;z
        double altitudeErr = targetAltitude - flight.getMeanAltitude();

        // System.out.println("altitude setpoint: " + targetAltitude + " alt error: " + altitudeErr);

        double velocitySetpoint = altitudeErr * 1;

        var velocityErr = velocitySetpoint - surfaceRefFrame.getVerticalSpeed();

        var velocityInput = velocityErr * 0.1;
        // System.out.println("velocity err: " + velocityErr + "velocity input: " + velocityInput);



        var throttleToApply = throttleToGetTWROf1 + throttleToCounteractAeroForces + velocityInput;

        // var yes = surfaceRefFrame.getangul
        

        vessel.getControl().setThrottle((float)throttleToApply);


        // now correct for lateral speed
        var velocity = vessel.flight(ReferenceFrame.createHybrid(
          connection,
          vessel.getOrbit().getBody().getReferenceFrame(),
          vessel.getSurfaceReferenceFrame(),
          vessel.getOrbit().getBody().getReferenceFrame(),
          vessel.getOrbit().getBody().getReferenceFrame())).getVelocity();
        
          // System.out.printf("Surface velocity = (%.1f, %.1f, %.1f)\n",
          // velocity.getValue0(),
          // velocity.getValue1(),
          // velocity.getValue2());


        var forwardSpeed = velocity.getValue2();
        var lateralSpeed = velocity.getValue1() * -1; // right is positive

        var targetForwardSpeed = 0;
        var targetLateralSpeed = 0;

        var forwardInput = (targetForwardSpeed - forwardSpeed) * 1;
        System.out.println("Forward speed: " + forwardSpeed + " Forward input: " + forwardInput);
        mAutopilot.setTargetPitch((float) forwardInput);


        Thread.sleep((long) (1d / 60d));
      }

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