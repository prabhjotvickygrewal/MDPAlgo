
package robot;
import communication.Comm;
import algorithm.*;
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
	public static final int MAX_STEP=20;
	public static int delay=300;
    private boolean fastestRun=false;
	private boolean virtual=false;
    private Map map;
    private Direction ori;
    private Vector pos;
    private LinkedList<RobotAction> buffer;
    private LinkedList<Vector> history;
    private LinkedList<Vector> shortestPath=null;
    public Robot(){
        map=new Map();
        ori=Direction.East;
        pos=new Vector(Algorithm.startPoint.x,Algorithm.startPoint.y);
        buffer=new LinkedList<RobotAction>();
        history=new LinkedList<Vector>();
    }
    public Robot(Map map, Direction ori,Vector pos){
    	this.map=map;
    	this.ori=ori;
    	this.pos=new Vector(pos.x,pos.y);
    	buffer=new LinkedList<RobotAction>();
        history=new LinkedList<Vector>();
    }
    public void bufferAction(RobotAction action){
        buffer.add(action);
    }
    public void execute(RobotAction action){
        switch(action){
            case Forward:
            	if(!virtual){
	            	if(fastestRun)
	                	shortestPath.add(new Vector(pos.x,pos.y));
	                history.add(new Vector(pos.x,pos.y));
	                if(!(Algorithm.isSimulating || virtual)){
	                    Comm.sendToRobot("1,1");
	                    if(!fastestRun)
	                    	while(!Comm.checkActionCompleted());
	                }
	                Calibration.addCount();
            	}
            	if(!fastestRun)
            		pos.add(ori.toVector());
                break;
            case Backward:
            	if(!fastestRun)
            		ori=ori.getDown();
            	if(!virtual){
	                if(!(Algorithm.isSimulating || virtual)){
	                    Comm.sendToRobot("2,180,1");
	                    if(!fastestRun)
	                    	while(!Comm.checkActionCompleted());
	                }
	                Calibration.addCount();
            	}
                break;
            case Right:
            	if(!fastestRun)
            		ori=ori.getRight();
                if(!virtual){
	                if(!(Algorithm.isSimulating || virtual)){
	                    Comm.sendToRobot("2,90,1");
	                    if(!fastestRun)
	                    	while(!Comm.checkActionCompleted());
	                }
	                Calibration.addCount();
                }
                break;
            case Left:
            	if(!fastestRun)
            		ori=ori.getLeft();
                if(!virtual){
	                if(!(Algorithm.isSimulating || virtual)){
	                    Comm.sendToRobot("2,90,0");
	                    if(!fastestRun)
	                    	while(!Comm.checkActionCompleted());
	                }
	                Calibration.addCount();
                }
                break;
        }
        if(!(Algorithm.isSimulating || virtual)) {
	        Comm.sendToAndroid("position::"+pos.x+";;"+pos.y);
	        Comm.sendToAndroid("orientation::"+ori.ordinal());
        }
        else{
        	if(!virtual && !fastestRun){
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
    public void setOriByTurn(RobotAction ac){
    	switch(ac){
    	case Backward:
    		ori=ori.getDown();
    		break;
    	case Right:
    		ori=ori.getRight();
    		break;
    	case Left:
    		ori=ori.getLeft();
    		break;
    	}
    }
    public void setFastestRun(boolean sp){
    	fastestRun=sp;
    	if(sp==true && shortestPath==null)
    		shortestPath=new LinkedList<Vector>();
    }
    public boolean isFastestRun(){
    	return fastestRun;
    }
    public boolean onShortestPath(Vector v){
    	for(Vector vt:shortestPath)
    		if(vt.equals(v))
    			return true;
    	return false;
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
    public int getFrequency(Vector v){
    	int count=0;
    	for(Vector vt:history)
    		if(vt.equals(v))
    			count++;
    	return count;
    }
    public LinkedList<Vector> getHistory(){
    	return history;
    }
    public void restart(){
    		pos=new Vector(Algorithm.startPoint.x,Algorithm.startPoint.y);
    		ori=getDefaultOri();
    }
    public void explorationFinished() {
    	Direction target=getDefaultOri();
    	execute(getTargetMove(target));
    }
//    public void calibrate() {
//    	boolean succ;
//    	execute(RobotAction.Backward);
//    	Comm.sendToRobot("7");
//		int count=0;
//		do{
//			succ=Comm.checkCalibrationCompleted();
//			count++;
//			if(count>3)
//				break;
//		}while(succ!=true);
//    	execute(RobotAction.Backward);
//    	Comm.sendToRobot("7");
//		count=0;
//		do{
//			succ=Comm.checkCalibrationCompleted();
//			count++;
//			if(count>3)
//				break;
//		}while(succ!=true);
//    }
    public void moveForwardMultiple(int n, GUI gui){
//    	if(n==1){
//    		execute(RobotAction.Forward);
//    	    if(!fastestRun)
//    	    	gui.getGridPanel().getGridContainer().drawGrid(map, this);
//    	    return;
//    	}

//    	while(n>MAX_STEP){
//    		if(!Algorithm.isSimulating && !runShortestPath){
//    			Comm.sendToRobot("1,"+MAX_STEP);
//    			updateGUI(MAX_STEP, gui);
//    			while(!Comm.checkActionCompleted());
//    		}
//    		else{
//    			for(int i=0;i<MAX_STEP;i++){
//    	    		execute(RobotAction.Forward);
//    	    		gui.getGridPanel().getGridContainer().drawGrid(map, this);
//    			}
//    		}
//    		n-=MAX_STEP;
//    	}
//    	if(n!=0){
//    		if(n==1){
//    			execute(RobotAction.Forward);
//        		gui.getGridPanel().getGridContainer().drawGrid(map, this);
//    		}
////	    	pos.add(ori.toVector().nMultiply(n));
//    		else{
//		    	if(!Algorithm.isSimulating){
//		    		Comm.sendToRobot("1,"+n);
//		    		updateGUI(n, gui);
//		    		while(!Comm.checkActionCompleted());
//		    	}
//		    	else{
//	    			for(int i=0;i<n;i++){
//	    	    		execute(RobotAction.Forward);
//	    	    		gui.getGridPanel().getGridContainer().drawGrid(map, this);	    		
//	    			}
//		    	}
//    		}
//    	}
    	if(!Algorithm.isSimulating && !fastestRun){
			Comm.sendToRobot("1,"+n);
			updateGUI(n, gui);
			while(!Comm.checkActionCompleted());
		}
    	else if(!Algorithm.isSimulating && fastestRun){
			Comm.sendToRobot("1,"+n);
    	}
		else if(!fastestRun){
			for(int i=0;i<n;i++){
	    		execute(RobotAction.Forward);
	    		gui.getGridPanel().getGridContainer().drawGrid(map, this);
			}
		}
//        Comm.sendToAndroid("position::"+pos.x+";;"+pos.y);
//        Comm.sendToAndroid("orientation::"+ori.ordinal());
    }
    
    public void updateGUI(int steps, GUI gui){
    	for(int i=0;i<steps;i++){
    		if(fastestRun)
    			shortestPath.add(pos);
            pos.add(ori.toVector());
            if(!Algorithm.isSimulating){
            	while(!Comm.checkArduinoMessage(String.valueOf(i+1)));
	            Comm.sendToAndroid("position::"+pos.x+";;"+pos.y);
	            Comm.sendToAndroid("orientation::"+ori.ordinal());
            }
            else if(fastestRun){
            	try {
		            Thread.sleep(delay);                 //1000 milliseconds is one second.
		        } catch(InterruptedException ex) {
		            Thread.currentThread().interrupt();
		        }
            }
	    	gui.getGridPanel().getGridContainer().drawGrid(map, this);
    	}
    }
    public RobotAction getTargetMove(Direction b) {
		switch(ori) {
		case North:
			switch(b) {
			case North:
				return RobotAction.Error;
			case South:
				return RobotAction.Backward;
			case West:
				return RobotAction.Left;
			case East:
				return RobotAction.Right;
			}
			break;
		case South:
			switch(b) {
			case North:
				return RobotAction.Backward;
			case South:
				return RobotAction.Error;
			case West:
				return RobotAction.Right;
			case East:
				return RobotAction.Left;
			}
			break;
		case West:
			switch(b) {
			case North:
				return RobotAction.Right;
			case South:
				return RobotAction.Left;
			case West:
				return RobotAction.Error;
			case East:
				return RobotAction.Backward;
			}
			break;
		case East:
			switch(b) {
			case North:
				return RobotAction.Left;
			case South:
				return RobotAction.Right;
			case West:
				return RobotAction.Backward;
			case East:
				return RobotAction.Error;
			}
		}
		return RobotAction.Error;
	}
}
