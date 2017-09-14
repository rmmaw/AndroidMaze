package williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad;

/**
 * Created by jeskay on 11/27/16.
 */

import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.Robot.Direction;
import williameskay.cs301.cs.wm.edu.amazebywilliameskayandreillymaw.falstad.Robot.Turn;

public class Pledge extends AutomaticDriver {

    BasicRobot robot;

    public Pledge() {
        super();
    }

    public Pledge(String navigatorAlgorithm) {
        super(navigatorAlgorithm);
    }

    @Override
    public void setBasicRobot(BasicRobot robot) {
        this.robot = robot;
    }


    @Override
    public boolean drive2Exit() throws Exception {
        if (robot.distanceToObstacle(Direction.LEFT) != 0){
            robot.rotate(Turn.LEFT);
            robot.move(robot.distanceToObstacle(Direction.FORWARD));
            robot.rotate(Turn.RIGHT);
        }


        while (this.robot.isAtExit() == false){

            if (robot.maze.killDriver == true){
                return false;
            }

            if (robot.maze.pauseDriver == true){
                Thread.sleep(500);
                continue;
            }

            if (robot.batteryLevel <= 0)
                return false;

            robot.move(1);
            if (this.robot.isAtExit())
                return true;

            if (robot.distanceToObstacle(Direction.LEFT) == 0){

                if (robot.distanceToObstacle(Direction.FORWARD) != 0){
                    continue;
                }
                else{
                    robot.rotate(Turn.RIGHT);
                }
            }
            else{
                robot.rotate(Turn.LEFT);
            }

        }

        return true;
    }
    }