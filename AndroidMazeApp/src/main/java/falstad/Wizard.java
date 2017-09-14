package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.Robot.Turn;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation.Cells;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.generation.Distance;

public class Wizard extends AutomaticDriver {

    BasicRobot robot;
    Distance distances;
    Cells cells;
    boolean moveIsEnd = false;
    enum CardinalDirection {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    public Wizard(){
        super();
    }

    public Wizard(String navigatorAlgorithm) {
        super(navigatorAlgorithm);
    }

    @Override
    public void setBasicRobot(BasicRobot robot){
        this.robot = robot;
        this.cells = this.robot.maze.getCells();
    }

    @Override
    public void setDistance(Distance distances){
        this.distances = distances;
    }


    @Override
    public boolean drive2Exit() throws Exception{
        CardinalDirection directionToMove;

        while (this.moveIsEnd == false){

            if (robot.maze.killDriver == true){
                return false;
            }

            if (robot.maze.pauseDriver == true){
                Thread.sleep(500);
                continue;
            }



            if (robot.batteryLevel <= 0)
                return false;

            int x = robot.getCurrentPosition()[0];
            int y = robot.getCurrentPosition()[1];

            directionToMove = chooseDirection(x,y);
            moveCardDirection(directionToMove);
        }

        return true;
    }

    /*
     * Given a cardinal direction, this method has the robot rotate toward that cardinal direction.
     */
    private void moveCardDirection(CardinalDirection directionToMove) throws Exception {
        int dx = robot.getCurrentDirection()[0];
        int dy = robot.getCurrentDirection()[1];

        int xNeeded = getDirectionCoords(directionToMove)[0];
        int yNeeded = getDirectionCoords(directionToMove)[1];


        //Transform temps of dx and dy to resemble the left direction.
        int Rightdx = dy;
        int Rightdy = -dx;

        //Transform temps of dx and dy to resemble the right direction.
        int Leftdx = -dy;
        int Leftdy = dx;

        //Transform temps of dx and dy to resemble the around direction.
        int Arounddx = -dx;
        int Arounddy = -dy;


        if (Arounddx == xNeeded && Arounddy == yNeeded){
            robot.rotate(Turn.AROUND);
        }
        if (Leftdx == xNeeded && Leftdy == yNeeded){
            robot.rotate(Turn.LEFT);
        }
        if (Rightdx == xNeeded && Rightdy == yNeeded){
            robot.rotate(Turn.RIGHT);
        }

        robot.move(1);
    }


    /*
     * This method chooses the cardinal direction that the robot should move based on the
     * cell's surrounding distances information.
     */
    public CardinalDirection chooseDirection(int x, int y){
        int lowestDistance = -1;
        CardinalDirection cardDirection = CardinalDirection.NORTH; // Arbitrary

        if (cells.hasNoWallOnLeft(x,y)){

            if (this.robot.maze.isEndPosition(x-1, y)){
                this.moveIsEnd = true;
                return CardinalDirection.WEST;
            }

            if (distances.getDistance(x-1, y) < lowestDistance || lowestDistance == -1){
                cardDirection = CardinalDirection.WEST;
                lowestDistance = distances.getDistance(x-1, y);
            }
        }

        if (cells.hasNoWallOnRight(x,y)){

            if (this.robot.maze.isEndPosition(x+1, y)){
                this.moveIsEnd = true;
                return CardinalDirection.EAST;
            }

            if (distances.getDistance(x+1, y) < lowestDistance || lowestDistance == -1){
                cardDirection = CardinalDirection.EAST;
                lowestDistance = distances.getDistance(x+1, y);
            }
        }

        if (cells.hasNoWallOnBottom(x,y)){

            if (this.robot.maze.isEndPosition(x, y+1)){
                this.moveIsEnd = true;
                return CardinalDirection.SOUTH;
            }

            if (distances.getDistance(x, y+1) < lowestDistance || lowestDistance == -1){
                cardDirection = CardinalDirection.SOUTH;
                lowestDistance = distances.getDistance(x, y+1);
            }
        }

        if (cells.hasNoWallOnTop(x,y)){

            if (this.robot.maze.isEndPosition(x, y-1)){
                this.moveIsEnd = true;
                return CardinalDirection.NORTH;
            }

            if (distances.getDistance(x, y-1) < lowestDistance || lowestDistance == -1){
                cardDirection = CardinalDirection.NORTH;
                lowestDistance = distances.getDistance(x, y-1);
            }
        }

        return cardDirection;
    }

    /*
     * Given a cardinal direction, this class returns the appropriate dx and dy values.
     */
    public int[] getDirectionCoords(CardinalDirection direction){
        if (direction == CardinalDirection.NORTH)
            return new int[]{0,-1};
        if (direction == CardinalDirection.SOUTH)
            return new int[]{0,1};
        if (direction == CardinalDirection.EAST)
            return new int[]{1,0};
        return new int[]{-1,0}; //Returns west

    }


}