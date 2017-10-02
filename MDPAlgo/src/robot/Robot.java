
package robot;
import map.*;
import java.util.LinkedList;

import communications.Communication;
/**
 *
 * @author user
 */
public class Robot {
    private Map map;
    private Direction ori;
    private Vector pos;
    private LinkedList<RobotAction> buffer;
    private boolean simulation; 
    
    public Robot(boolean s){
        map=new Map();
        ori=Direction.East;
        pos=new Vector(1,1);
        buffer=new LinkedList<RobotAction>();
        simulation = s;
    }
    public void bufferAction(RobotAction action){
        buffer.add(action);
    }
    public void execute(RobotAction action){
        switch(action){
            case Forward:
                pos.add(ori.toVector());
                break;
            case Backward:
                pos.add(ori.getDown().toVector());
                break;
            case Right:
                ori=ori.getRight();
                break;
            case Left:
                ori=ori.getLeft();
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
    
    public boolean getSimulation() {
    	return simulation;
    }
    
    public void moveForwardMultiple(int count) {
    	if(count == 1) {
    		execute(RobotAction.Forward);
    	}
    	else {
    		Communication comm = Communication.getCommMgr();
    		if(count == 10) {
    			comm.sendMsg("0", Communication.INSTRUCTIONS);
    		}
    		else if(count < 10) {
    			comm.sendMsg(Integer.toString(count), Communication.INSTRUCTIONS);
    		}
    		
    		switch(ori) {
    		case North:
    			pos.x += count;
    			break;
    		case East:
    			pos.y += count;
    		case South:
    			pos.x += count;
    		case West:
    			pos.y += count;
    			break;
    		}
    		comm.sendMsg(this.getPos().x + ", " + this.getPos().y + ", " + this.getOri(), Communication.BOT_POS);
    	}
    }
}
