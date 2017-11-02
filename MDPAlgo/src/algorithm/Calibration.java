package algorithm;
import map.Vector;
import robot.*;
import map.*;
import communication.*;

public class Calibration {
	private static int frontCount=0;
	private static int rightCount=0;
//	private static int turnFrontCount=0;
//	private static int turnRightCount=0;
	private static Robot robot;
	private static MapLayer layer;
	private static final int MAX_FRONTCOUNT=200;
	private static final int MAX_RIGHTCOUNT=200;
//	private static final int MAX_RIGHTCOUNT=4;

	
	public static void calibrate(Robot r, MapLayer m){
		robot=r;
		layer=m;
		boolean succ=false;
		if(rightCount>MAX_RIGHTCOUNT){
			System.out.println("try right alignment");
			rightAlignment();
		}
		if(frontCount>MAX_FRONTCOUNT) {
			System.out.println("try front alignment");
			frontAlignment();
		}
	}
	public static void rightAlignment(){
		boolean succ=false;
		if(checkRightAlignmentPossible()){
			if(!Algorithm.isSimulating){
				Comm.sendToRobot("5");
				int count=0;
				do{
					succ=Comm.checkCalibrationCompleted();
					count++;
					if(count>3)
						break;
				}while(succ!=true);
			}
			else{
				System.out.println("rightAlignment");
		        try {
		            Thread.sleep(500);                 //1000 milliseconds is one second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
		        succ=true;
			}
			if(succ){
				rightCount=0;
				System.out.println("rightAlignment");
			}
		}
	}
	public static void frontAlignment(){
		boolean succ=false;
		boolean[] possible=checkFrontAlignmentPossible();
		if(possible[0]||possible[1]||possible[2]){
			if(!Algorithm.isSimulating){
				if(possible[0]&&possible[1]&&possible[2])
					Comm.sendToRobot("7");
				else if(possible[2])
					Comm.sendToRobot("u,2");
				else if(possible[1])
					Comm.sendToRobot("u,1");
				else
					Comm.sendToRobot("u,0");
				int count=0;
				do{
					succ=Comm.checkCalibrationCompleted();
					count++;
					if(count>3)
						break;
				}while(succ!=true);			
			}
			else{
		        try {
		            Thread.sleep(500);                 //1000 milliseconds is one second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
		        succ=true;
			}
			if(succ){
				frontCount=0;
				System.out.println("frontAlignment");
			}
		}
	}
	public static void calibrate(){
		if(robot!=null && layer!=null)
			calibrate(robot, layer);
		else
			System.out.println("Calibration failed");
	}
	public static void addfrontCount(){
		frontCount++;
	}
	public static void addrightCount(){
		rightCount++;
	}
	public static void addCount() {
		frontCount++;
		rightCount++;
	}
	public static void forceCalibration() {
		frontCount=MAX_FRONTCOUNT+1;
		rightCount=MAX_RIGHTCOUNT+1;
		calibrate();
	}
	public static void forceNextRightAlignment(){
		rightCount=MAX_RIGHTCOUNT+1;
	}
	public static void forceNextFrontAlignment(){
		frontCount=MAX_FRONTCOUNT+1;
	}
	public static void afterExploration(Robot robot){
		if(!Algorithm.isSimulating){
    	boolean succ;
    	robot.execute(RobotAction.Backward);
    	Comm.sendToRobot("7\n");
		int count=0;
		do{
			succ=Comm.checkCalibrationCompleted();
			count++;
			if(count>3)
				break;
		}while(succ!=true);
    	robot.execute(RobotAction.Left);
    	Comm.sendToRobot("7\n");
		count=0;
		do{
			succ=Comm.checkCalibrationCompleted();
			count++;
			if(count>3)
				break;
		}while(succ!=true); 
    	robot.execute(RobotAction.Left);
		}
		frontCount=0;
		rightCount=0;
	}
    public static boolean checkRightAlignmentPossible(){
    	Vector pos=robot.getPos();
    	Direction ori=robot.getOri();
    	Vector rightVector=ori.getRight().toVector();
    	boolean right_t=layer.checkIsObstacle(pos.nAdd(rightVector.nMultiply(2)).nAdd(ori.toVector()));
    	boolean right_m=layer.checkIsObstacle(pos.nAdd(rightVector.nMultiply(2)));
    	boolean right_b=layer.checkIsObstacle(pos.nAdd(rightVector.nMultiply(2)).nAdd(ori.getDown().toVector()));
    	return (right_t && right_m && right_b);
    }
    public static boolean[] checkFrontAlignmentPossible(){
    	boolean[] result=new boolean[3];
        Vector rightVector=robot.getOri().getRight().toVector();
        Vector upVector=robot.getOri().toVector();
        Vector leftVector=robot.getOri().getLeft().toVector();
        result[0]=layer.checkIsObstacle(robot.getPos().nAdd(upVector.nMultiply(2)).nAdd(leftVector));
        result[1]=layer.checkIsObstacle(robot.getPos().nAdd(upVector.nMultiply(2)));
        result[2]=layer.checkIsObstacle(robot.getPos().nAdd(upVector.nMultiply(2)).nAdd(rightVector));
        return result;
    }
}
