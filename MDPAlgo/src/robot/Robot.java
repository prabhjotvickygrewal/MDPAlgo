
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
    private Map map;
    private Direction ori;
    private Vector pos;
    private LinkedList<RobotAction> buffer;
    
    public Robot(){
        map=new Map();
        ori=Direction.East;
        pos=new Vector(1,1);
        buffer=new LinkedList<RobotAction>();
    }
    public void bufferAction(RobotAction action){
        buffer.add(action);
    }
    public void execute(RobotAction action){
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
        Comm.sendToAndroid("position:"+pos.x+":"+pos.y+"\n");
        Comm.sendToAndroid("orientation:"+ori.ordinal()+"\n");
    }
    public void executeBuffered(){
        for(RobotAction action: buffer)
            execute(action);
        buffer.clear();
    }
    public Map getMap(){
        return map;
    }
    public Vector getPos(){
        return pos;
    }
    public Direction getOri(){
        return ori;
    }
    
//    public boolean getSimulation() {
//    	return isSimulating;
//    }
    public void restart(){
    	if(Algorithm.isSimulating){
    		ori=Direction.East;
    		pos=new Vector(1,1);
    		map=new Map();
    	}
    	else
    		;//shortest path added here?
    		
    }
    
    public void moveForwardMultiple(int n){
    	if(n==1)
    		execute(RobotAction.Forward);
    	while(n>6){
    		pos.add(ori.toVector().nMultiply(6));
    		if(!Algorithm.isSimulating){
    			Comm.sendToRobot("1,6");
    			while(!Comm.checkActionCompleted());
    		}
    		n-=6;
    	}
    	
    	pos.add(ori.toVector().nMultiply(n));
    	if(!Algorithm.isSimulating){
    		Comm.sendToRobot("1,"+n);
    		while(!Comm.checkActionCompleted());
    	}
    	
        Comm.sendToAndroid("position::"+pos.x+";;"+pos.y);
        Comm.sendToAndroid("orientation::"+ori.ordinal());
    }
}
