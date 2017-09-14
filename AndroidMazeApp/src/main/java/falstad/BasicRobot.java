package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

public class BasicRobot implements Robot{

    public MazeController maze; //Main maze for reference
    public ManualDriver driver; //Parent driver for this class
    public float batteryLevel;
    public boolean stopped;
    public float quarterTurnVal = 3;
    public float moveVal = 5;
    public float sensingVal = 1;
    private boolean[] hasDistanceSensor = new boolean[4];
    private boolean hasRoomSensor;
    private boolean hasJunctionSensor;
    private int pathLength = 0;


    public BasicRobot(){
        this.hasJunctionSensor = true;
        this.hasRoomSensor = true;
        this.hasDistanceSensor = new boolean[]{true,true,true,true};
    }

    /**
     * Alternative constructor that allows for disabling of certain sensors.
     *
     * @param room : set to true in order to use junction sensor, false to disable
     * @param distance : is an array of four boolean values that allows for distance sensing in the {forward, left, backward, right} directions.
     */
    public BasicRobot(boolean room, boolean[] distance){
        this.hasRoomSensor = room;
        if (distance.length == 4)
            this.hasDistanceSensor = distance;
        else
            throw new IllegalArgumentException("The third argument must be a boolean array of size 4.");
    }



    /**
     * This method rotates the robot if it is called in the correct direction
     */
    @Override
    public void rotate(Turn turn) throws Exception {
        switch(turn){
            case LEFT:
                this.batteryLevel -= 3;

                hasStopped();

                this.maze.rotate(1);

                break;
            case RIGHT:
                this.batteryLevel -= 3;

                hasStopped();

                this.maze.rotate(-1);

                break;

            case AROUND:

                this.batteryLevel -= 6;

                hasStopped();

                this.maze.rotate(2);

                break;
        }


    }

    /**
     * This methid will move the driver forward and subtract the correct amount of energy
     */
    @Override
    public void move(int distance) throws Exception {


        for (int i = 0; i < distance; i++){

            this.batteryLevel -= 5;
            this.pathLength += 1;
            this.maze.walk(1);

            hasStopped();
        }


        System.out.println("\n\nMove Number:  " + String.valueOf(getPathLength()) );
        System.out.println("	Current Battery Level:  " + String.valueOf(this.batteryLevel));


    }




    /**
     * Returns true if the robot is at the exit position. Uses maze.isEndPosition()
     */
    @Override
    public boolean isAtExit() {
        return this.maze.isEndPosition(this.maze.px, this.maze.py);
    }


    /**
     * Returns true if the exit is right in sight of the robot
     */
    @Override
    public boolean canSeeExit(Direction direction) throws UnsupportedOperationException {

        //Make sure we can use the distance sensor for the method, reset when done.
        int distance = distanceToObstacle(direction);


        if (distance == Integer.MAX_VALUE)
            return true;
        else
            return false;
    }

    /**
     * Returns the true if the robot has stopped, false if it has not.
     */
    @Override
    public boolean hasStopped() {
        if (this.batteryLevel <= 0){
            System.out.println("The definitive value of the battery level is: " + String.valueOf(this.batteryLevel));
            this.stopped = true;

            this.maze.setState(Constants.STATE_FINISH);
            this.maze.notifyViewerRedraw();
        }

        if (isAtExit()){
            this.stopped = true;
        }

        return this.stopped;
    }



    /**
     * Simple setter that provides a maze object for this class.
     */
    @Override
    public void setMaze(MazeController maze) {
        this.maze = maze;
        this.maze.robot = this; //Give the maze the ability to reference this robot for notification
        this.batteryLevel = 2500;
        this.pathLength = 0;
        this.stopped = false;
    }

    /**
     * Returns the current position as an array {x,y}
     */
    @Override
    public int[] getCurrentPosition() throws Exception {
        int[] result = {this.maze.px, this.maze.py};

        if (result[0] > this.maze.mazew -1 || result[0] < 0 || result[1] > this.maze.mazeh -1 || result[1] < 0)
            throw new Exception("Current Position is out of range");

        return result;
    }

    /**
     * Returns the current direction.
     */
    @Override
    public int[] getCurrentDirection() {
        return new int[]{this.maze.dx, this.maze.dy};
    }

    /**
     *  get for the battery level.
     */
    @Override
    public float getBatteryLevel() {
        return this.batteryLevel;
    }

    /**
     * Set for the battery level of this robot.
     */
    @Override
    public void setBatteryLevel(float level) {
        this.batteryLevel = level;
    }

    /**
     * Returns energy of a full rotation.
     */
    @Override
    public float getEnergyForFullRotation() {
        return this.quarterTurnVal * 4;
    }

    /**
     * Returns energy of a forward step.
     */
    @Override
    public float getEnergyForStepForward() {
        return this.moveVal;
    }

    /**
     * Get for the path length of the driver.
     * @return
     */
    public int getPathLength(){
        return this.pathLength ;
    }

    /**
     * Setter for the path length of the driver.  Used only to reset the path length
     * to zero when the game is restarted. Called in the keydown method at restart.
     * @param i
     */
    public void setPathLength(int i){
        this.driver.setPathLength(i);
    }




    /**
     * Returns true if the current position is inside a room, false otherwise. **/
    @Override
    public boolean isInsideRoom() throws UnsupportedOperationException {
        if (hasRoomSensor()){

            //Using a sensor decreases the battery level by one
            this.batteryLevel -= 1;
            hasStopped(); //Will terminate process if battery is now zero

            return this.maze.mazecells.isInRoom(this.maze.px, this.maze.py);
        }
        else
            throw new UnsupportedOperationException("Room sensor not available.");
    }



    /**
     * Returns the distance from the robot to a wall. 0 is returned if the there is wall on the tile that the robot is currently on.
     */
    @Override
    public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
        if (hasDistanceSensor(direction)){


            this.batteryLevel -= 1;
            hasStopped();

            int dx = getCurrentDirection()[0];
            int dy = getCurrentDirection()[1];


            int temp;
            if (direction == Direction.BACKWARD){
                dx = -dx;
                dy = -dy;
            }
            if (direction == Direction.LEFT){
                temp = dx;
                dx = -dy;
                dy = temp;
            }
            if (direction == Direction.RIGHT){
                temp = dx;
                dx = dy;
                dy = -temp;
            }

            int count = 0;
            int tempX = this.maze.px;
            int tempY = this.maze.py;

            while (true){

                if (dx == 0 && dy == -1){
                    if (this.maze.mazecells.hasWallOnTop(tempX, tempY))
                        return count;
                    tempY -= 1;
                }
                if (dx == 1 && dy == 0){
                    if (this.maze.mazecells.hasWallOnRight(tempX, tempY))
                        return count;
                    tempX += 1;
                }
                if (dx == 0 && dy == 1){
                    if (this.maze.mazecells.hasWallOnBottom(tempX, tempY))
                        return count;
                    tempY += 1;
                }
                if (dx == -1 && dy == 0){
                    if (this.maze.mazecells.hasWallOnLeft(tempX, tempY))
                        return count;
                    tempX -= 1;
                }

                count += 1;


                if (tempX < 0 || tempY < 0 || tempX >= this.maze.mazew  || tempY >= this.maze.mazeh)
                    return Integer.MAX_VALUE;
            }

        }
        else
            throw new UnsupportedOperationException("Distance Sensor is unavailable.");

    }





    /**
     * Returns true if this instance can use a room sensor.
     */
    @Override
    public boolean hasRoomSensor() {
        return this.hasRoomSensor;
    }


    /**
     * Returns true if this instance can use a distance sensor.
     */
    @Override
    public boolean hasDistanceSensor(Direction direction) {
        if (direction == Direction.FORWARD && this.hasDistanceSensor[0] == true)
            return true;
        else if (direction == Direction.LEFT && this.hasDistanceSensor[1] == true)
            return true;
        else if (direction == Direction.BACKWARD && this.hasDistanceSensor[2] == true)
            return true;
        else if (direction == Direction.RIGHT && this.hasDistanceSensor[3] == true)
            return true;
        else
            return false;
    }





    /**
     * This method is only used by a manual driver to set this robot's parent as that driver.
     */
    public void setParentDriver(ManualDriver driver){
        this.driver = driver;
    }

    /**
     * This method is used by the keyDown method in maze in order to pass a notification
     * to the driver for use.
     */
    public void notifyRobot(int notice) throws Exception{
        if (this.maze.manualMode == true)
            driver.notifyManualDriver(notice);
        else
            ;
    }




}
