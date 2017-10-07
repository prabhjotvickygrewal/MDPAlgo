package algorithm;
import map.Vector;
import robot.*;
import map.*;
import communication.*;

public class Calibration {
	private static int moveCount=0;
	private static Robot robot;
	private static MapLayer layer;
	private static final int MAX_COUNT=3;
	
	public static void calibrate(Robot r, MapLayer m){
		robot=r;
		layer=m;
		boolean succ=false;
		if(moveCount>MAX_COUNT){
			if(checkRightAlignmentPossible()){
				if(!Algorithm.isSimulating){
					Comm.sendToRobot("5\n");
					succ=Comm.checkCalibrationCompleted();
				}
				else{
					System.out.println("calibrating");
			        try {
			            Thread.sleep(500);                 //1000 milliseconds is one second.
			        } catch(InterruptedException ex) {
			            Thread.currentThread().interrupt();
			        }
			        succ=true;
				}
			}
			else if(checkFrontAlignmentPossible()){
				if(!Algorithm.isSimulating){
					Comm.sendToRobot("7\n");
					succ=Comm.checkCalibrationCompleted();
				}
				else{
					System.out.println("calibrating");
			        try {
			            Thread.sleep(2000);                 //1000 milliseconds is one second.
			        } catch(InterruptedException ex) {
			            Thread.currentThread().interrupt();
			        }
			        succ=true;
				}
			}
			if(succ)
				moveCount=0;
		}
	}
	public static void addMoveCount(){
		moveCount++;
	}
	
    public static boolean checkRightAlignmentPossible(){
    	Vector pos=robot.getPos();
    	Direction ori=robot.getOri();
    	Vector rightVector=ori.getRight().toVector();
    	boolean right_t=layer.checkIsObstacle(pos.nAdd(rightVector.nMultiply(2)).nAdd(ori.toVector()));
    	boolean right_m=layer.checkIsObstacle(pos.nAdd(rightVector).nMultiply(2));
    	boolean right_b=layer.checkIsObstacle(pos.nAdd(rightVector.nMultiply(2)).nAdd(ori.getDown().toVector()));
    	return (right_t && right_m && right_b);
    }
    public static boolean checkFrontAlignmentPossible(){
        Vector rightVector=robot.getOri().getRight().toVector();
        Vector upVector=robot.getOri().toVector();
        Vector leftVector=robot.getOri().getLeft().toVector();
        boolean up_l=layer.checkIsObstacle(robot.getPos().nAdd(upVector.nMultiply(2)).nAdd(leftVector));
        boolean up_m=layer.checkIsObstacle(robot.getPos().nAdd(upVector.nMultiply(2)));
        boolean up_r=layer.checkIsObstacle(robot.getPos().nAdd(upVector.nMultiply(2)).nAdd(rightVector));
        if(up_l && up_m && up_r)                    //check whether in front are all obstacles
            return true;
        else
            return false;
    }
}
