package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation.Distance;

public class AutomaticDriver extends Thread implements RobotDriver {

    private int pathLength;
    private int[] dimensions = new int[2];
    private Distance distance;
    private Robot robot;
    private BasicRobot basicRobot;
    private String navigatorAlgorithm;
    private RobotDriver childDriver;

    public AutomaticDriver(){
        super();
    }

    public AutomaticDriver(String navigatorAlgorithm){
        this.navigatorAlgorithm = navigatorAlgorithm;
        System.out.println("Automatic driver recieved direction to run with the " + navigatorAlgorithm + " algorithm.");
    }


    @Override
    public void run() {
        try {
            drive2Exit();
        } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public boolean drive2Exit() throws Exception {
        //Have the maze and solution shown on screen.
        this.basicRobot.maze.mapMode = true;
        this.basicRobot.maze.showSolution = true;
        this.basicRobot.maze.showMaze = true;
        this.basicRobot.maze.notifyViewerRedraw();


        if (navigatorAlgorithm.equals("Wizard")){

            System.out.println("Wizard!");
            Wizard wizard = new Wizard();
            wizard.setDistance(this.distance);
            wizard.setBasicRobot(this.basicRobot);
            this.childDriver = wizard;
            if (wizard.drive2Exit())
                return true;
            else
                return false;
        }
        if (navigatorAlgorithm.equals("Wall-Follower")){
            System.out.println("Wall Follower!");
            WallFollower wallFollower = new WallFollower();
            wallFollower.setBasicRobot(this.basicRobot);
            this.childDriver = wallFollower;
            if (wallFollower.drive2Exit())
                return true;
            else
                return false;
        }

        if (navigatorAlgorithm.equals("Pledge")){
            System.out.println("Pledge");
            Pledge pledge = new Pledge();
            pledge.setBasicRobot(this.basicRobot);
            this.childDriver = pledge;
            if (pledge.drive2Exit())
                return true;
            else
                return false;
        }
        return true;
    }

    @Override
    public void setRobot(Robot r) throws Exception {
        this.robot = (BasicRobot)r;
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
        return this.robot.getBatteryLevel();
    }

    @Override
    public int getPathLength() {
        return this.childDriver.getPathLength();
    }


    public void setPathLength(int i) {
        this.pathLength = i;
    }
    public void setBasicRobot(BasicRobot r){
        this.basicRobot = r;
    }








}
