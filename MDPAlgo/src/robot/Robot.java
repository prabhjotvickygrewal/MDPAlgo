
package robot;
import communication.Comm;
import map.*;
import algorithm.Algorithm;
import algorithm.Calibration;
import simulation.GUI;

import java.util.LinkedList;

//import communications.Communication;
/**
 *
 * @author user
 */
public class Robot {
	public static final int MAX_STEP=6;
	public static int delay=300;
	private boolean virtual=false;
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
    public void execute(RobotAction action){
        switch(action){
            case Forward:
                pos.add(ori.toVector());
                if(!(Algorithm.isSimulating || virtual)){
                    Comm.sendToRobot("1,1");
                    while(!Comm.checkActionCompleted());
                }
                Calibration.addMoveCount();
                break;
            case Backward:
                pos.add(ori.getDown().toVector());
                if(!(Algorithm.isSimulating || virtual)){
                    Comm.sendToRobot("2,180,1");
                    while(!Comm.checkActionCompleted());
                    Comm.sendToRobot("1,1");
                    while(!Comm.checkActionCompleted());
                }
                Calibration.addMoveCount();
                break;
            case Right:
                ori=ori.getRight();
                if(!(Algorithm.isSimulating || virtual)){
                    Comm.sendToRobot("2,90,1");
                    while(!Comm.checkActionCompleted());
                }
                break;
            case Left:
                ori=ori.getLeft();
                if(!(Algorithm.isSimulating || virtual)){
                    Comm.sendToRobot("2,90,0");
                    while(!Comm.checkActionCompleted());
                }
                break;
        }
        if(!(Algorithm.isSimulating || virtual)) {
	        Comm.sendToAndroid("position::"+pos.x+";;"+pos.y);
	        Comm.sendToAndroid("orientation::"+ori.ordinal());
        }
        else{
        	if(!virtual){
	        	try {
		            Thread.sleep(delay);                 //1000 milliseconds is one second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
        	}
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
    public void setPos(Vector v){
    	this.pos=v;
    }
    public Direction getOri(){
        return ori;
    }
    public void setDefaultOri(){
    	this.ori=getDefaultOri();
    }
    public void setVirtual(boolean v){
    	virtual=true;
    }
    public Direction getDefaultOri(){
    	if(pos.y==1 && (pos.x<Map.MAX_X-2))
			return Direction.East;
		else if(pos.x==Map.MAX_X-2 && (pos.y<Map.MAX_Y-2))
			return Direction.North;
		else if(pos.y==Map.MAX_Y-2 && pos.x>1)
			return Direction.West;
		else if(pos.x==1 && pos.y>1)
			return Direction.South;
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
    
    public void moveForwardMultiple(int n, GUI gui){
    	if(n==1){
    		execute(RobotAction.Forward);
    	    gui.getGridPanel().getGridContainer().drawGrid(map, this);
    	}

    	while(n>MAX_STEP){
    		if(!Algorithm.isSimulating){
//    	    	pos.add(ori.toVector().nMultiply(n));
    			Comm.sendToRobot("1,"+MAX_STEP);
    			updateGUI(MAX_STEP, gui);
    			while(!Comm.checkActionCompleted());
    		}
    		else{
    			for(int i=0;i<MAX_STEP;i++){
    	    		execute(RobotAction.Forward);
    	    		gui.getGridPanel().getGridContainer().drawGrid(map, this);
    			}
    		}
    		n-=MAX_STEP;
    	}
    	if(n!=0){
    		if(n==1){
    			execute(RobotAction.Forward);
        		gui.getGridPanel().getGridContainer().drawGrid(map, this);
    		}
//	    	pos.add(ori.toVector().nMultiply(n));
    		else{
		    	if(!Algorithm.isSimulating){
		    		Comm.sendToRobot("1,"+n);
		    		updateGUI(n, gui);
		    		while(!Comm.checkActionCompleted());
		    	}
		    	else{
	    			for(int i=0;i<n;i++){
	    	    		execute(RobotAction.Forward);
	    	    		gui.getGridPanel().getGridContainer().drawGrid(map, this);	    		
	    			}
		    	}
    		}
    	}
//        Comm.sendToAndroid("position::"+pos.x+";;"+pos.y);
//        Comm.sendToAndroid("orientation::"+ori.ordinal());
    }
    
    public void updateGUI(int steps, GUI gui){
    	for(int i=0;i<steps;i++){
            pos.add(ori.toVector());
    		while(!Comm.checkArduinoMessage(String.valueOf(i)));
            Comm.sendToAndroid("position::"+pos.x+";;"+pos.y);
            Comm.sendToAndroid("orientation::"+ori.ordinal());
    		gui.getGridPanel().getGridContainer().drawGrid(map, this);
    	}
    }
}
