package krpc;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.KRPC;
import krpc.client.services.SpaceCenter;
import krpc.client.services.SpaceCenter.AutoPilot;
import krpc.client.services.SpaceCenter.Control;
import krpc.client.services.SpaceCenter.Vessel;

import java.io.IOException;

public class Main {
  public static void main(String[] args) throws IOException, RPCException {
    Connection connection = Connection.newInstance();
      KRPC krpc = KRPC.newInstance(connection);
      System.out.println("Connected to kRPC version " + krpc.getStatus().getVersion());
      SpaceCenter spaceCenter = SpaceCenter.newInstance(connection);
      Vessel vessel = spaceCenter.getActiveVessel();
      
      AutoPilot autoPilot = vessel.getAutoPilot();
      Control vesselControl = vessel.getControl();

      autoPilot.targetPitchAndHeading(90, 90);
      autoPilot.engage();

      vesselControl.setThrottle(1);

      
      // Thread.sleep(1000);
      System.out.println("Launch!");
      vessel.getControl().activateNextStage();
    
      

  }
}