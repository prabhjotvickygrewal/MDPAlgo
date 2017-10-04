
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
                    Comm.sendToRobot("1,1\n");
                    while(!Comm.checkActionCompleted());
                }
                Calibration.addMoveCount();
                break;
            case Backward:
                pos.add(ori.getDown().toVector());
                if(!Algorithm.isSimulating){
                    Comm.sendToRobot("2,180,1\n");
                    while(!Comm.checkActionCompleted());
                    Comm.sendToRobot("1,1\n");
                    while(!Comm.checkActionCompleted());
                }
                Calibration.addMoveCount();
                break;
            case Right:
                ori=ori.getRight();
                if(!Algorithm.isSimulating){
                    Comm.sendToRobot("2,90,1\n");
                    while(!Comm.checkActionCompleted());
                }
                break;
            case Left:
                ori=ori.getLeft();
                if(!Algorithm.isSimulating){
                    Comm.sendToRobot("2,90,0\n");
                    while(!Comm.checkActionCompleted());
                }
                break;
        }
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
    
//    public void moveForwardMultiple(int count) {
//    	if(count == 1) {
//    		execute(RobotAction.Forward);
//    	}
//    	else {
//    		Communication comm = Communication.getCommMgr();
//    		if(count == 10) {
//    			comm.sendMsg("0", Communication.INSTRUCTIONS);
//    		}
//    		else if(count < 10) {
//    			comm.sendMsg(Integer.toString(count), Communication.INSTRUCTIONS);
//    		}
//    		
//    		switch(ori) {
//    		case North:
//    			pos.x += count;
//    			break;
//    		case East:
//    			pos.y += count;
//    		case South:
//    			pos.x += count;
//    		case West:
//    			pos.y += count;
//    			break;
//    		}
//    		comm.sendMsg(this.getPos().x + ", " + this.getPos().y + ", " + this.getOri(), Communication.BOT_POS);
//    	}
//    }
}
