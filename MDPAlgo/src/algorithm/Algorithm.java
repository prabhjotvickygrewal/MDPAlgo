
package algorithm;
import map.*;
import robot.*;
import simulation.*;
import communication.*;
import java.util.LinkedList;
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
    private static Comm comm;
    public Algorithm(Simulator simulator){
        robot=new Robot(true);
        map=robot.getMap();
        this.simulator=simulator;
        mapLayer=new MapLayer(robot.getMap());
//        if(comm==null)
//            comm =new Comm();
    }
    public Algorithm(Simulator s, Robot r){
        robot=r;
        map=robot.getMap();
        simulator=s;
        mapLayer=new MapLayer(map);
//        if(comm==null)
//            comm=new Comm();
    }
    public void explore(int timeLimit, int covLimit, GUI gui) {      //timeLimit in second
        ShortestPath sp = new ShortestPath(map, robot);
        do{
            scan(gui);        
            followRightObstacle(gui);
        }while(!checkTimeLimitReached(timeLimit) && !checkCovLimitReached(covLimit) && !reachStartZone());
        Vector goal;
       
        while(!checkTimeLimitReached(timeLimit) && ! checkCovLimitReached(covLimit) && !exploreComplete()){
	        	
        	goal = findNearestExploredPoint(getRemainedPoint().getFirst());
	        
        	if(goal == null) {
	        	break;
	        }	
	        sp.executeShortestPath(goal.x, goal.y, gui);
            do{
                scan(gui);
                try {
                    Thread.sleep(200);                 //1000 milliseconds is one second.
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                map.printMap();
                gui.getGridPanel().getGridContainer().drawGrid(map, robot);
                System.out.println(robot.getPos() + "  " + robot.getOri());
                followRightObstacle(gui);
            }while(robot.getPos()!=goal);    //explore until get to the original position again
            sp.executeShortestPath(1, 1, gui);
        }
    }
    public void followRightObstacle(GUI gui){
        if(isRightFree()){
            robot.bufferAction(RobotAction.Right);
            robot.bufferAction(RobotAction.Forward);
            robot.executeBuffered();
//            System.out.println(robot.getPos() + "  " + robot.getOri());
        }
        else{
            while(!isUpFree()){
                robot.bufferAction(RobotAction.Left);
                robot.executeBuffered();
                scan(gui);
                map.printMap();
                System.out.println(robot.getPos() + "  " + robot.getOri());

            }
        //    System.out.println(robot.getPos() + "  " + robot.getOri());
            robot.bufferAction(RobotAction.Forward);
            robot.executeBuffered();
//            System.out.println("\n");
           

        }
    }
    public void scan(GUI gui){
        SensorData s=simulator.getSensorData(robot);
        mapLayer.processSensorData(s, robot);
        try {
            Thread.sleep(200);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        map.printMap();
//        comm.test();
//        comm.sendToAndroid(String.format("%s%n%s", mapLayer.getFirstString(), mapLayer.getSecondString()));
        gui.getGridPanel().getGridContainer().drawGrid(map, robot);
        System.out.println(robot.getPos() + "  " + robot.getOri());
        
    }
    public LinkedList<Vector> getRemainedPoint(){
        LinkedList<Vector> remainedPoint=mapLayer.getRemainedPoint(robot.getPos());
        LinkedList<Vector> legalRemainedPoint=new LinkedList<>();
        for(Vector v:remainedPoint)
            if(mapLayer.checkExplorable(v))
                legalRemainedPoint.add(v);
        return legalRemainedPoint;
    }
    
    public boolean checkTimeLimitReached(int timeLimit){
        currentTime=System.currentTimeMillis();
        long diff=currentTime-startTime;
        return diff/1000>timeLimit;
    }
    public boolean checkCovLimitReached(int covLimit){
        return mapLayer.checkCovLimitReached(covLimit);
    }
    public boolean reachStartZone(){
        return robot.getPos().equals(new Vector(1,1));
    }
    public boolean exploreComplete(){
        return mapLayer.checkCovLimitReached(100);
    }
    public boolean isRightFree(){
        Vector rightVector=robot.getOri().getRight().toVector();
        Vector upVector=robot.getOri().toVector();
        Vector downVector=robot.getOri().getDown().toVector();
        boolean right_t=mapLayer.checkIsFree(robot.getPos().nAdd(rightVector.nMultiply(2)).nAdd(upVector));
        boolean right_m=mapLayer.checkIsFree(robot.getPos().nAdd(rightVector.nMultiply(2)));
        boolean right_b=mapLayer.checkIsFree(robot.getPos().nAdd(rightVector.nMultiply(2)).nAdd(downVector));
        if(right_t && right_b && right_m)           //check whether can move to the right
            return true;
        return false;
    }
    public boolean isUpFree(){
        Vector rightVector=robot.getOri().getRight().toVector();
        Vector upVector=robot.getOri().toVector();
        Vector leftVector=robot.getOri().getLeft().toVector();
        boolean up_l=mapLayer.checkIsFree(robot.getPos().nAdd(upVector.nMultiply(2)).nAdd(leftVector));
        boolean up_m=mapLayer.checkIsFree(robot.getPos().nAdd(upVector.nMultiply(2)));
        boolean up_r=mapLayer.checkIsFree(robot.getPos().nAdd(upVector.nMultiply(2)).nAdd(rightVector));
        if(up_l && up_m && up_r)                    //check whether can move forward
            return true;
        else
            return false;
    }
    public Map getMap(){
        return map;
    }
    private Vector findNearestExploredPoint(Vector p) {
    	double distance;
    	double min = 2;
    	for(int i = 0 ; i < Map.MAX_X; i++)
    		for (int j = 0 ; j < Map.MAX_Y; j++) {
    			distance = Math.sqrt((p.x - i)*(p.x - i)+(p.y - j)*(p.y - j));
    			if(distance < min && map.checkInsideBoundary(p) && map.checkIsFree(p)) {
    				Vector v = new Vector(i,j);
    				return v;
    			}
    		}
    	double min2 = 3;
    	for(int i = 0 ; i < Map.MAX_X; i++)
    		for (int j = 0 ; j < Map.MAX_Y; j++) {
    			distance = Math.sqrt((p.x - i)*(p.x - i)+(p.y - j)*(p.y - j));
    			if(distance < min2 && map.checkInsideBoundary(p) && map.checkIsFree(p)) {
    				Vector v = new Vector(i,j);
    				return v;
    			}
    		}
    	double min3 = 4;
    	for(int i = 0 ; i < Map.MAX_X; i++)
    		for (int j = 0 ; j < Map.MAX_Y; j++) {
    			distance = Math.sqrt((p.x - i)*(p.x - i)+(p.y - j)*(p.y - j));
    			if(distance < min3 && map.checkInsideBoundary(p) && map.checkIsFree(p)) {
    				Vector v = new Vector(i,j);
    				return v;
    			}
    		}
    	return null;
    }
}
