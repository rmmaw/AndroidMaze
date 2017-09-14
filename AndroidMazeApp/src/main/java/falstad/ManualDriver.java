package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */


import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.Robot.Turn;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation.Distance;

public class ManualDriver implements RobotDriver {

    public int notice;
    private int pathLength;
    private int[] dimensions = new int[2];
    private Distance distance;
    private Robot robot;
    private BasicRobot robotNotifier;


    @Override
    public boolean drive2Exit() throws Exception {



        switch(this.notice){
            case 0: //Up
                this.robot.move(1);
                this.pathLength += 1;
                break;
            case 1: //Left
                this.robot.rotate(Turn.LEFT);
                break;
            case 2: //Right
                this.robot.rotate(Turn.RIGHT);
                break;
            case 3: //Down
                this.robot.rotate(Turn.AROUND);
                break;
        }

        if (this.robot.hasStopped())
            return true;
        else
            return false;
    }


    @Override
    public void setRobot(Robot r) throws Exception {
        this.robot = r;

    }

    @Override
    public void setDimensions(int width, int height) {
        this.dimensions[0] = width;
        this.dimensions[1] = height;
    }

    @Override
    public void setDistance(Distance distance) {
        this.distance = distance;

    }
    @Override
    public float getEnergyConsumption() {
        return this.robotNotifier.getBatteryLevel();
    }

    @Override
    public int getPathLength() {
        return this.pathLength;
    }

    /**
     * Used by the robot class in order to reset the path length to zero when the
     * maze application is restarted.
     */
    public void setPathLength(int i) {
        this.pathLength = i;
    }



    public void setRobotNotifier(BasicRobot robot){
        this.robotNotifier = robot;
        this.robotNotifier.setParentDriver(this);
    }

    public void notifyManualDriver(int notice) throws Exception{
        this.notice = notice;
        drive2Exit();
    }


}