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
	private static final int MAX_FRONTCOUNT=2;
	private static final int MAX_RIGHTCOUNT=3;
//	private static final int MAX_RIGHTCOUNT=4;

	
	public static void calibrate(Robot r, MapLayer m){
		robot=r;
		layer=m;
		boolean succ=false;
		if(rightCount>MAX_RIGHTCOUNT){
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
					System.out.println("calibrating");
			        try {
			            Thread.sleep(500);                 //1000 milliseconds is one second.
			        } catch(InterruptedException ex) {
			            Thread.currentThread().interrupt();
			        }
			        succ=true;
				}
				if(succ) 
					rightCount=0;
				
			}
		}
		if(frontCount>MAX_FRONTCOUNT) {
			if(checkFrontAlignmentPossible()){
				if(!Algorithm.isSimulating){
					Comm.sendToRobot("7");
					int count=0;
					do{
						succ=Comm.checkCalibrationCompleted();
						count++;
						if(count>3)
							break;
					}while(succ!=true);			
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
				if(succ)
					frontCount=0;
			}
		}
//			if(succ){
//					if(rightCount>MAX_COUNT)
//						rightCount=0;
//					else
//						frontCount=0;
//			}
		
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
		frontCount=5;
		rightCount=5;
		calibrate();
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
    	robot.execute(RobotAction.Backward);
    	Comm.sendToRobot("7\n");
		count=0;
		do{
			succ=Comm.checkCalibrationCompleted();
			count++;
			if(count>3)
				break;
		}while(succ!=true);
		}
		frontCount=0;
		rightCount=0;
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
