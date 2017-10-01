
package algorithm;
import map.*;
import robot.*;
import simulation.*;
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
    public Algorithm(Simulator simulator){
        robot=new Robot();
        map=robot.getMap();
        this.simulator=simulator;
        mapLayer=new MapLayer(robot.getMap());
    }
    public void explore(int timeLimit, int covLimit) {      //timeLimit in second
        boolean firstRoundFinished=false;
        do{
            scan();
            followRightObstacle();
        }while(!checkTimeLimitReached(timeLimit) && !checkCovLimitReached(covLimit) && !reachStartZone());
        firstRoundFinished=true;
        //Vector goal;
        //while(!checkTimeLimitReached(timeLimit) && ! checkCovLimitReached(covLimit) &&!exploreComplete()){
        //    goal=getRemainedPoint().getFirst();
            
            //call shortest path
            //get to the goal
            
        //    do{
        //        scan();
        //        followRightObstacle();
        //    }while(robot.getPos()!=goal);    //explore until get to the original position again
        //}
    }
    public void followRightObstacle(){
        if(isRightFree()){
            robot.bufferAction(RobotAction.TurnRight);
            robot.bufferAction(RobotAction.MoveForward);
            robot.executeBuffered();
        }
        else{
            while(!isUpFree()){
                robot.bufferAction(RobotAction.TurnLeft);
                robot.executeBuffered();
            }
            robot.bufferAction(RobotAction.MoveForward);
            robot.executeBuffered();
        }
    }
    public void scan(){
        SensorData s=simulator.getSensorData(robot);
        mapLayer.processSensorData(s, robot);
    }
    public LinkedList<Vector> getRemainedPoint(){
        LinkedList<Vector> remainedPoint=mapLayer.getRemainedPoint(robot.getPos());
        LinkedList<Vector> legalRemainedPoint=new LinkedList<>();
        for(Vector v:remainedPoint)
            if(mapLayer.checkEnclosureLargeEnough(v))
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
        boolean right_l=mapLayer.checkIsFree(robot.getPos().nAdd(rightVector.nMultiply(2)).nAdd(upVector));
        boolean right_m=mapLayer.checkIsFree(robot.getPos().nAdd(rightVector.nMultiply(2)));
        boolean right_r=mapLayer.checkIsFree(robot.getPos().nAdd(rightVector.nMultiply(2)).nAdd(downVector));
        if(right_l && right_m && right_r)           //check whether can move to the right
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
}
