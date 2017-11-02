
package algorithm;
import map.*;
import robot.*;
import simulation.*;
import communication.*;
import java.util.ArrayList;

/**
 *
 * @author WGUO002
 */
public class Algorithm {
    private static Robot robot;
    private static Map map;
    private static Simulator simulator;
    private static MapLayer mapLayer;
    private static long startTime=System.currentTimeMillis();
    private static long currentTime;
    private static boolean exploreUnknown=false;
    private static Comm comm;
    public static int timeLimit;
    public static boolean isSimulating;
    public static boolean androidEnabled=true;
    public static boolean simplifyActionEnabled=true;
    public static Vector startPoint=new Vector(1,1);
    public static Vector endPoint=new Vector(13,18);
    public static Vector wayPoint=null;//new Vector(5,8);
    public Algorithm(Simulator simulator, boolean isSimulating){
        robot=new Robot();
        map=robot.getMap();
        Algorithm.simulator=simulator;
        mapLayer=new MapLayer(robot.getMap());
        Algorithm.isSimulating=isSimulating;
        if(comm==null && !isSimulating)
            comm =new Comm();
    }
    public Algorithm(Simulator s, Robot r, boolean isSimulating){
        robot=r;
        map=robot.getMap();
        simulator=s;
        mapLayer=new MapLayer(map);
        Algorithm.isSimulating=isSimulating;
        if(comm==null && !isSimulating)
            comm=new Comm();
    }
    public void explore(int timeL, int covLimit, GUI gui) {      //timeLimit in second
    	timeLimit=timeL;
    	if(androidEnabled){
    		while(!Comm.checkAndroidMessage("exploration")){
    			robot.setPos(new Vector(Algorithm.startPoint.x,Algorithm.startPoint.y));
    			robot.setDefaultOri();
    			gui.getGridPanel().getGridContainer().drawGrid(map, robot);
    		}
    	}
    	startTime=System.currentTimeMillis();
    	covLimit++;
        ShortestPath sp = new ShortestPath(map, robot, true);
        do{
            while(!scan(gui));
            if(exploreComplete())
            	break;
            Calibration.calibrate(robot, mapLayer);
            followRightObstacle(gui);
        }while(!checkTimeLimitReached() && !checkCovLimitReached(covLimit) && !(reachStartZone() && reachGoalZone()) && !exploreUnknown);
        gui.getGridPanel().getGridContainer().drawGrid(map, robot);
        
        if(checkTimeLimitReached() || checkCovLimitReached(covLimit))
        	return;
        if(reachStartZone() && !exploreComplete()) {
        	robot.explorationFinished();
        	Calibration.afterExploration(robot);
            gui.getGridPanel().getGridContainer().drawGrid(map, robot);
        }
        
        simplifyActionEnabled=false;
        ArrayList<Vector> unknown;
        ArrayList<Vector> unreachable=new ArrayList<Vector>();
        ArrayList<RobotAction> movement;
        do{
	        	
        	unknown = getRemainedPoint();
        	unknown.removeAll(unreachable);
        	if(unknown.size()==0)
        		break;
        	Vector goal=findNearestExploredPoint(unknown);
            sp = new ShortestPath(map, robot, true);
            movement=sp.findShortestPath(goal.x, goal.y, gui);
	        if(movement==null)
	        	unreachable.add(goal);
	        else {
	        	sp.executeMovement(movement, gui);
	        	alignToUnknown(gui);
	        	while(!scan(gui));
//	        	alignToObstacle(gui);
//	        	Calibration.forceCalibration();
//	            do{
//	                while(!scan(gui));
//	                if(exploreComplete())
//	                	break;
//	                Calibration.calibrate(robot, mapLayer);
//	                System.out.println(robot.getPos() + "  " + robot.getOri());
//	                followRightObstacle(gui);
//	            }while(!robot.getPos().equals(goal));    //explore until get to the original position again
	        }
        }while(!checkTimeLimitReached() && ! checkCovLimitReached(covLimit) && !exploreComplete());
        if(checkTimeLimitReached() || checkCovLimitReached(covLimit))
        	return;
        sp = new ShortestPath(map, robot, true);
        sp.executeMovement(sp.findShortestPath(startPoint.x, startPoint.x, gui), gui);
        robot.explorationFinished();
        System.out.println("exploration finished");
//        for(Vector v:robot.getHistory())
//        	System.out.print(v);
//        Calibration.afterExploration(robot);
        gui.getGridPanel().getGridContainer().drawGrid(map, robot);
        
    }
    public void followRightObstacle(GUI gui){
    	if(robot.getFrequency(robot.getPos())>2)
			simplifyActionEnabled=false;
    	if(simplifyActionEnabled){
    		if(!checkNeedToGoInside()){
        		gui.getGridPanel().getGridContainer().drawGrid(map, robot);
        		return;
        	}
    		if(moveWithoutScan(gui))
    			return;
    	}
//    	if(simplifyActionEnabled && isOnEdge() && isRightFree()){
//	    	if(simplifyAction(gui))
//	    		return;
//    	}
        if(mapLayer.isRightFree(robot)){
            robot.bufferAction(RobotAction.Right);
            robot.bufferAction(RobotAction.Forward);
            robot.executeBuffered();
//            System.out.println(robot.getPos() + "  " + robot.getOri());
        }
        else{
            while(!mapLayer.isUpFree(robot)){
                robot.bufferAction(RobotAction.Left);
                robot.executeBuffered();
                while(!scan(gui));
//                map.printMap();
//                System.out.println(robot.getPos() + "  " + robot.getOri());

            }
        //    System.out.println(robot.getPos() + "  " + robot.getOri());
            robot.bufferAction(RobotAction.Forward);
            robot.executeBuffered();
//            System.out.println("\n");
           

        }
    }
    public static boolean scan(GUI gui){
    	String data;
    	SensorData s;
    	if(isSimulating){
	        s=simulator.getSensorData(robot);
    	}
    	else{
        		Comm.sendToRobot("4\n");
    			data=Comm.receiveSensorData();
    			if(data.equals("0"))
    				return false;
        		s=new SensorData(data);
    	}
    	
        mapLayer.processSensorData(s, robot);
        mapLayer.markVisitedPointFree(robot.getHistory());

//        map.printMap();
        if(!isSimulating){
        	Comm.sendToAndroid("map::"+mapLayer.getFirstString()+";;"+mapLayer.getSecondString());
        }
//        System.out.println("Send out string");
        gui.getGridPanel().getGridContainer().drawGrid(map, robot);
//        System.out.println(robot.getPos() + "  " + robot.getOri());
        return true;
        
    }
    public void alignToObstacle(GUI gui){
    	int count=0;
    	while(mapLayer.isRightFree(robot) && count<4){
    		robot.bufferAction(RobotAction.Left);
    		robot.executeBuffered();
            while(!scan(gui));
    		count++;
    	}
    }
    public void alignToUnknown(GUI gui){
    	int count=0;
    	while(!mapLayer.isUpUnknown(robot) && count<4){
            while(!scan(gui));
    		robot.bufferAction(RobotAction.Left);
    		robot.executeBuffered();
    		count++;
    	}
    }
    public ArrayList<Vector> getRemainedPoint(){
    	ArrayList<Vector> remainedPoint=mapLayer.getRemainedPoint();
    	ArrayList<Vector> legalRemainedPoint=new ArrayList<>();
        for(Vector v:remainedPoint)
            if(mapLayer.checkExplorable(v))
                legalRemainedPoint.add(v);
        return legalRemainedPoint;
    }

    public static boolean checkNeedToGoInside(){
    	Robot virtualR=new Robot(robot.getMap(),robot.getOri(),robot.getPos());
    	virtualR.setVirtual(true);
    	Vector startP=new Vector(virtualR.getPos().x, virtualR.getPos().y);
    	Direction startOri=virtualR.getOri();
    	int count=0;
    	do{
    		if(count>15)
    			return true;
    		if(mapLayer.isRightFree(virtualR)){
    			virtualR.execute(RobotAction.Right);
    			virtualR.execute(RobotAction.Forward);
            }
            else{
                while(!mapLayer.isUpFree(virtualR)){
                	virtualR.execute(RobotAction.Left);
                	if(mapLayer.checkScanRequired(virtualR.getPos(), virtualR.getOri()))
            			return true;
                }
                //when the robot reaches dead end, turn back
                if(count==0 && virtualR.getPos().equals(startP) && virtualR.getOri().getDown()==startOri){
                	robot.execute(RobotAction.Backward);
                	return false;
                }
                virtualR.execute(RobotAction.Forward);
            }
    		if(mapLayer.checkScanRequired(virtualR.getPos(), virtualR.getOri()))
    			return true;
    		count++;
        }while(!virtualR.getPos().equals(startP));
    	if(virtualR.getOri()==startOri)
    		return true;
    	else{
    		robot.execute(robot.getTargetMove(virtualR.getOri()));
        	return false;
    	}
    }
    public static boolean moveWithoutScan(GUI gui){
    	Robot virtualR=new Robot(robot.getMap(),robot.getOri(),robot.getPos());
    	virtualR.setVirtual(true);
    	int count=0;
    	boolean scanRequired=false;
    	do{
    		if(mapLayer.isRightFree(virtualR)){
    			virtualR.execute(RobotAction.Right);
    			virtualR.execute(RobotAction.Forward);
    			
            }
            else{
                while(!mapLayer.isUpFree(virtualR)){
                	virtualR.execute(RobotAction.Left);
                	if(mapLayer.checkScanRequired(virtualR.getPos(), virtualR.getOri())){
                		scanRequired=true;
        				if(count>1){
    	    				ShortestPath sp=new ShortestPath(robot.getMap(),robot,true);
    	    				ArrayList<RobotAction> mv=sp.findShortestPath(virtualR.getPos().x, virtualR.getPos().y, gui);
    	    				if(mv!=null)
    	    					sp.executeMovement(mv, gui);
    	    				else
    	    					break;
    	    				robot.execute(robot.getTargetMove(virtualR.getOri()));
    	    				while(!scan(gui));
    	    				Calibration.calibrate();
    	    				robot.execute(RobotAction.Forward);
    	    				return true;
        				}
        			}
                	else if(virtualR.getPos().equals(startPoint) && reachGoalZone()){
                		exploreUnknown=true;
                		return true;
                	}
                }
                virtualR.execute(RobotAction.Forward);
            }
    		if(mapLayer.checkScanRequired(virtualR.getPos(), virtualR.getOri())){
    			scanRequired=true;
				if(count>1){
    				ShortestPath sp=new ShortestPath(robot.getMap(),robot,true);
    				ArrayList<RobotAction> mv=sp.findShortestPath(virtualR.getPos().x, virtualR.getPos().y, gui);
    				if(mv!=null)
    					sp.executeMovement(mv, gui);
    				else
    					break;
    				robot.execute(robot.getTargetMove(virtualR.getOri()));
//    				Calibration.forceCalibration();
    				return true;
				}
    		}
    		else if(virtualR.getPos().equals(startPoint) && reachGoalZone()){
    			exploreUnknown=true;
    			return true;
    		}
    		
    		count++;
        }while(count<20 && !scanRequired);
    	return false;
    }
    public static boolean checkTimeLimitReached(){
        currentTime=System.currentTimeMillis();
        long diff=currentTime-startTime;
        return diff/1000>timeLimit;
    }
    public boolean checkCovLimitReached(int covLimit){
        return mapLayer.checkCovLimitReached(covLimit);
    }
    public boolean reachStartZone(){
        return robot.getPos().equals(startPoint);
    }
    public boolean exploreComplete(){
        return mapLayer.checkCovLimitReached(100);
    }
    public static boolean reachGoalZone() {
    	return !mapLayer.checkIsUnknown(endPoint);
    }
    public Map getMap(){
        return map;
    }
    private Vector findNearestExploredPoint(ArrayList<Vector> unknown) {
    	int nearest=999;
    	Vector goal=null;
    	Vector origin=robot.getPos();
    	for(Vector v:unknown){
    		Vector cur=v.nAdd(new Vector(-2,0));
    		if(mapLayer.checkIsBlockFree(cur))
    			if(distance(cur,origin)<nearest){
    				goal=cur;
    				nearest=distance(cur,origin);
    			}
    		cur=v.nAdd(new Vector(0,-2));
    		if(mapLayer.checkIsBlockFree(cur))
    			if(distance(cur,origin)<nearest){
    				goal=cur;
    				nearest=distance(cur,origin);
    			}
    		cur=v.nAdd(new Vector(0,2));
    		if(mapLayer.checkIsBlockFree(cur))
    			if(distance(cur,origin)<nearest){
    				goal=cur;
    				nearest=distance(cur,origin);
    			}
    		cur=v.nAdd(new Vector(2,0));
    		if(mapLayer.checkIsBlockFree(cur))
    			if(distance(cur,origin)<nearest){
    				goal=cur;
    				nearest=distance(cur,origin);
    			}
    	}
    	return goal;
    }
    
    private int distance(Vector v1, Vector v2){
    	return Math.abs(v1.x-v2.x)+Math.abs(v1.y-v2.y);
    }
}
