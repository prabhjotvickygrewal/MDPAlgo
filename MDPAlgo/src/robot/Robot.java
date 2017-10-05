
package robot;
import communication.Comm;
import map.*;
import algorithm.Algorithm;
import algorithm.Calibration;

import java.util.LinkedList;

//import communications.Communication;
/**
 *
 * @author user
 */
public class Robot {
	public static final int MAX_STEP=6;
	public static int delay=300;
    private Map map;
    private Direction ori;
    private Vector pos;
    private LinkedList<RobotAction> buffer;
    
    public Robot(){
        map=new Map();
        ori=Direction.East;
        pos=new Vector(Algorithm.startPoint.x,Algorithm.startPoint.y);
        buffer=new LinkedList<RobotAction>();
    }
    public Robot(Map map, Direction ori,Vector pos){
    	this.map=map;
    	this.ori=ori;
    	this.pos=new Vector(pos.x,pos.y);
    	buffer=new LinkedList<RobotAction>();
    }
    public void bufferAction(RobotAction action){
        buffer.add(action);
    }
    public void execute(RobotAction action, boolean canDelay){
        switch(action){
            case Forward:
                pos.add(ori.toVector());
                if(!Algorithm.isSimulating){
                    Comm.sendToRobot("1,1");
                    while(!Comm.checkActionCompleted());
                }
                Calibration.addMoveCount();
                break;
            case Backward:
                pos.add(ori.getDown().toVector());
                if(!Algorithm.isSimulating){
                    Comm.sendToRobot("2,180,1");
                    while(!Comm.checkActionCompleted());
                    Comm.sendToRobot("1,1");
                    while(!Comm.checkActionCompleted());
                }
                Calibration.addMoveCount();
                break;
            case Right:
                ori=ori.getRight();
                if(!Algorithm.isSimulating){
                    Comm.sendToRobot("2,90,1");
                    while(!Comm.checkActionCompleted());
                }
                break;
            case Left:
                ori=ori.getLeft();
                if(!Algorithm.isSimulating){
                    Comm.sendToRobot("2,90,0");
                    while(!Comm.checkActionCompleted());
                }
                break;
        }
        if(!Algorithm.isSimulating) {
	        Comm.sendToAndroid("position:"+pos.x+":"+pos.y+"\n");
	        Comm.sendToAndroid("orientation:"+ori.ordinal()+"\n");
        }
        else{
        	if(canDelay){
	        	try {
		            Thread.sleep(delay);                 //1000 milliseconds is one second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
        	}
        }
    }
    public void executeBuffered(boolean canDelay){
        for(RobotAction action: buffer)
            execute(action, canDelay);
        buffer.clear();
    }
    public Map getMap(){
        return map;
    }
    public Vector getPos(){
        return pos;
    }
    public void setPos(Vector v){
    	this.pos=v;
    }
    public Direction getOri(){
        return ori;
    }
    
    
//    public boolean getSimulation() {
//    	return isSimulating;
//    }
    public void restart(){
    		ori=Direction.East;
    		pos=new Vector(Algorithm.startPoint.x,Algorithm.startPoint.y);
    		map=new Map();    		
    }
    
    public void moveForwardMultiple(int n){
    	if(n==1)
    		execute(RobotAction.Forward, true);
    	while(n>MAX_STEP){
    		pos.add(ori.toVector().nMultiply(MAX_STEP));
    		if(!Algorithm.isSimulating){
    			Comm.sendToRobot("1,"+MAX_STEP);
    			while(!Comm.checkActionCompleted());
    		}
    		n-=MAX_STEP;
    	}
    	if(n!=0){
	    	pos.add(ori.toVector().nMultiply(n));
	    	if(!Algorithm.isSimulating){
	    		Comm.sendToRobot("1,"+n);
	    		while(!Comm.checkActionCompleted());
	    	}
    	}
        Comm.sendToAndroid("position::"+pos.x+";;"+pos.y);
        Comm.sendToAndroid("orientation::"+ori.ordinal());
    }
}
