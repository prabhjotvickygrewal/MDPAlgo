
package algorithm;
import map.*;
import robot.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author WGUO002
 */
public class MapLayer {
    private Map map;
    private PointState[][] states;
    public static final int Sensor_ShortRange=3;
    public static final int Sensor_LongRange=6;

    public MapLayer(Map map){
        this.map=map;
        states=new PointState[Map.MAX_X][Map.MAX_Y];
        for(int i=0;i<Map.MAX_X;i++)
            for(int j=0;j<Map.MAX_Y;j++)
                states[i][j]=PointState.Unknown;
    }
    public MapLayer(Map map,PointState[][] states){
        this.map=map;
        this.states=states;
    }
    public void processSensorData(SensorData s, Robot r){
        Vector pos=r.getPos();
        Direction ori=r.getOri();
        Vector upVector=ori.toVector();
        Vector leftVector=ori.getLeft().toVector();
        Vector rightVector=ori.getRight().toVector();
        Vector downVector=ori.getDown().toVector();
        for(int i=-1;i<=1;i++)
            for(int j=-1;j<=1;j++)
                setStateAt(pos.nAdd(new Vector(i,j)),PointState.IsFree);
        
        if(s.left_t>0 && s.left_t<=Sensor_LongRange){
            setStateAt(pos.nAdd(leftVector.nMultiply(s.left_t+1).nAdd(upVector)), PointState.Obstacle);
            for(int i=1;i<s.left_t;i++)
                setStateAt(pos.nAdd(leftVector.nMultiply(i+1).nAdd(upVector)), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_LongRange;i++)
                setStateAt(pos.nAdd(leftVector.nMultiply(i+1).nAdd(upVector)), PointState.IsFree);
            
        if(s.right_b>0 && s.right_b<=Sensor_ShortRange){
            setStateAt(pos.nAdd(rightVector.nMultiply(s.right_b+1)).nAdd(downVector), PointState.Obstacle);
            for(int i=1;i<s.right_b;i++)
                setStateAt(pos.nAdd(rightVector.nMultiply(i+1)).nAdd(downVector), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_ShortRange;i++)
                setStateAt(pos.nAdd(rightVector.nMultiply(i+1)).nAdd(downVector), PointState.IsFree);
        
        if(s.right_t>0 && s.right_t<=Sensor_ShortRange){
            setStateAt(pos.nAdd(rightVector.nMultiply(s.right_t+1)).nAdd(upVector), PointState.Obstacle);
            for(int i=1;i<s.right_t;i++)
                setStateAt(pos.nAdd(rightVector.nMultiply(i+1)).nAdd(upVector), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_ShortRange;i++)
                setStateAt(pos.nAdd(rightVector.nMultiply(i+1)).nAdd(upVector), PointState.IsFree);
        
        if(s.up_l>0 && s.up_l<=Sensor_ShortRange){
            setStateAt(pos.nAdd(upVector.nMultiply(s.up_l+1)).nAdd(leftVector), PointState.Obstacle);
            for(int i=1;i<s.up_l;i++)
                setStateAt(pos.nAdd(upVector.nMultiply(i+1)).nAdd(leftVector), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_ShortRange;i++)
                setStateAt(pos.nAdd(upVector.nMultiply(i+1)).nAdd(leftVector), PointState.IsFree);
        
        if(s.up_m>0 && s.up_m<=Sensor_ShortRange){
            setStateAt(pos.nAdd(upVector.nMultiply(s.up_m+1)), PointState.Obstacle);
            for(int i=1;i<s.up_m;i++)
                setStateAt(pos.nAdd(upVector.nMultiply(i+1)), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_ShortRange;i++)
                setStateAt(pos.nAdd(upVector.nMultiply(i+1)), PointState.IsFree);
        
        if(s.up_r>0 && s.up_r<=Sensor_ShortRange){
            setStateAt(pos.nAdd(upVector.nMultiply(s.up_r+1)).nAdd(rightVector), PointState.Obstacle);
            for(int i=1;i<s.up_r;i++)
                setStateAt(pos.nAdd(upVector.nMultiply(i+1)).nAdd(rightVector), PointState.IsFree);   
        }
        else
            for(int i=1;i<=Sensor_ShortRange;i++)
                setStateAt(pos.nAdd(upVector.nMultiply(i+1)).nAdd(rightVector), PointState.IsFree);
        markVisitedPointFree(r.getHistory());
        map.updatePointMap(states);
    }
    
    public void markVisitedPointFree(LinkedList<Vector> history) {
    	for(Vector v:history) {
    		for(int i=-1;i<=1;i++)
    			for(int j=-1;i<=1;i++) {
    				setStateAt(new Vector(v.x+i,v.y+j),PointState.IsFree);
    			}
    	}
    	for(int i=-1;i<=1;i++)
			for(int j=-1;i<=1;i++) {
				setStateAt(new Vector(Algorithm.startPoint.x+i,Algorithm.startPoint.y+j),PointState.IsFree);
				setStateAt(new Vector(Algorithm.endPoint.x+i,Algorithm.endPoint.y+j),PointState.IsFree);
			}
    }
//    public LinkedList<Vector> getRemainedPoint(Vector pos){
//        LinkedList<Vector> remainedPoint=new LinkedList<>();
//        Vector curPoint;
//        for(int i=1;i<Map.MAX_Y;i++){
//            curPoint=pos.nAdd(new Vector(-i,-i));
//            for(int j=0;j<i*2;j++){
//                if(checkIsUnknown(curPoint))
//                    remainedPoint.add(curPoint);
//                curPoint=curPoint.nAdd(new Vector(1,0));
//            }
//            for(int j=0;j<i*2;j++){
//                if(checkIsUnknown(curPoint))
//                    remainedPoint.add(curPoint);
//                curPoint=curPoint.nAdd(new Vector(0,1));
//            }
//            for(int j=0;j<i*2;j++){
//                if(checkIsUnknown(curPoint))
//                    remainedPoint.add(curPoint);
//                curPoint=curPoint.nAdd(new Vector(-1,0));
//            }
//            for(int j=0;j<i*2;j++){
//                if(checkIsUnknown(curPoint))
//                    remainedPoint.add(curPoint);
//                curPoint=curPoint.nAdd(new Vector(0,-1));
//            }
//        }
//        for(Vector v:remainedPoint)
//        	System.out.print(v+"  ");
//        return remainedPoint;
//    }
    
//    public boolean checkEnclosureLargeEnough(Vector v){
//        Vector cur;
//        for(int i=-1;i<=1;i++)
//            for(int j=-1;j<=1;j++){
//                cur=v.nAdd(new Vector(i,j));
//                if(!checkInsideBoundary(cur) || !(checkIsUnknown(cur) || checkIsFree(cur)))
//                    return false;
//            }
//        return true;
//    }
    public ArrayList<Vector> getRemainedPoint(){
    	ArrayList<Vector> remained=new ArrayList<Vector>();
    	for(int i=0;i<Map.MAX_X;i++)
    		for(int j=0;j<Map.MAX_Y;j++)
    			if(states[i][j]==PointState.Unknown)
    				remained.add(new Vector(i,j));
    	return remained;
    }

    public boolean checkScanRequired(Vector v, Direction ori) {
    	Vector rightVector=ori.getRight().toVector();
        Vector leftVector=ori.getLeft().toVector();
        Vector downVector=ori.getDown().toVector();
        boolean up_l=checkIsExplored(v.nAdd(ori.toVector()).nAdd(leftVector),ori,Sensor_ShortRange);
        boolean up_m=checkIsExplored(v.nAdd(ori.toVector()),ori,Sensor_ShortRange);
        boolean up_r=checkIsExplored(v.nAdd(ori.toVector()).nAdd(rightVector),ori,Sensor_ShortRange);
        boolean right_t=checkIsExplored(v.nAdd(rightVector).nAdd(ori.toVector()),ori.getRight(),Sensor_ShortRange);
        boolean right_b=checkIsExplored(v.nAdd(rightVector).nAdd(downVector),ori.getRight(),Sensor_ShortRange);
        boolean left_t=checkIsExplored(v.nAdd(leftVector).nAdd(ori.toVector()),ori.getLeft(),Sensor_LongRange);
        return !(up_l && up_m && up_r && right_t && right_b && left_t);
    }
    public boolean checkIsExplored(Vector v,Direction dir,int range) {
    	Vector addVector=dir.toVector();
    	Vector cur;
    	for(int i=0;i<range;i++) {
    		cur=v.nAdd(addVector.nMultiply(i+1));
    		if(checkIsObstacle(cur))
    			return true;
    		if(checkIsUnknown(cur))
    			return false;
    	}
    	return true;
    }
    public boolean checkExplorable(Vector v){
    	 boolean isUpperBlocked=!checkIsBlockFree(v.nAdd(new Vector(0,2)));
    	 boolean isLowerBlocked=!checkIsBlockFree(v.nAdd(new Vector(0,-2)));
    	 boolean isRightBlocked=!checkIsBlockFree(v.nAdd(new Vector(2,0)));
    	 boolean isLeftBlocked=!checkIsBlockFree(v.nAdd(new Vector(-2,0)));    	
    	 return !(isUpperBlocked && isLowerBlocked && isRightBlocked && isLeftBlocked);
      }
    public boolean checkIsBlockFree(Vector v){
    	 boolean isFree=true;
    	 for(int i=-1;i<=1;i++)
    		 for(int j=-1;j<=1;j++)
    			 if(!checkIsFree(v.nAdd(new Vector(i,j))))
    				 isFree=false;
    	 return isFree;
    }
    
    public boolean isRightFree(Robot robot){
        Vector rightVector=robot.getOri().getRight().toVector();
        Vector upVector=robot.getOri().toVector();
        Vector downVector=robot.getOri().getDown().toVector();
        boolean right_t=checkIsFree(robot.getPos().nAdd(rightVector.nMultiply(2)).nAdd(upVector));
        boolean right_m=checkIsFree(robot.getPos().nAdd(rightVector.nMultiply(2)));
        boolean right_b=checkIsFree(robot.getPos().nAdd(rightVector.nMultiply(2)).nAdd(downVector));
        if(right_t && right_b && right_m)           //check whether can move to the right
            return true;
        return false;
    }
    public boolean isUpFree(Robot robot){
        Vector rightVector=robot.getOri().getRight().toVector();
        Vector upVector=robot.getOri().toVector();
        Vector leftVector=robot.getOri().getLeft().toVector();
        boolean up_l=checkIsFree(robot.getPos().nAdd(upVector.nMultiply(2)).nAdd(leftVector));
        boolean up_m=checkIsFree(robot.getPos().nAdd(upVector.nMultiply(2)));
        boolean up_r=checkIsFree(robot.getPos().nAdd(upVector.nMultiply(2)).nAdd(rightVector));
        if(up_l && up_m && up_r)                    //check whether can move forward
            return true;
        else
            return false;
    }
    public boolean isLeftFree(Robot robot){
        Vector leftVector=robot.getOri().getLeft().toVector();
        Vector upVector=robot.getOri().toVector();
        Vector downVector=robot.getOri().getDown().toVector();
        boolean left_t=checkIsFree(robot.getPos().nAdd(leftVector.nMultiply(2)).nAdd(upVector));
        boolean left_m=checkIsFree(robot.getPos().nAdd(leftVector.nMultiply(2)));
        boolean left_b=checkIsFree(robot.getPos().nAdd(leftVector.nMultiply(2)).nAdd(downVector));
        if(left_t && left_m && left_b)           //check whether can move to the right
            return true;
        return false;
    }
    public void setStateAt(Vector v,PointState pState){
        if(checkInsideBoundary(v)){
            if(states[v.x][v.y]==PointState.Unknown)
                states[v.x][v.y]=pState;
            else if(states[v.x][v.y]!=pState){
                System.out.println("Data Inconsistency!");
                states[v.x][v.y]=pState;
            }
        }
    }
    public boolean checkInsideBoundary(Vector v){
        return map.checkInsideBoundary(v);
    }
    public boolean checkIsFree(Vector v){
        if(checkInsideBoundary(v))
            if(states[v.x][v.y]==PointState.IsFree || states[v.x][v.y]==PointState.VirtualWall)
                return true;
        return false;
    }
    public boolean checkIsUnknown(Vector v){
        if(checkInsideBoundary(v))
            if(states[v.x][v.y]==PointState.Unknown)
                return true;
        return false;
    }
    public boolean checkIsObstacle(Vector v){
    	if(!checkInsideBoundary(v))
    		return true;
        if(states[v.x][v.y]==PointState.Obstacle)
            return true;
        return false;
    }
    public void updateMap(){
        map.updatePointMap(states);
    }
    public boolean checkCovLimitReached(int covLimit){
//        int knownCount=0;
//        for(int i=0;i<Map.MAX_X;i++)
//            for(int j=0;j<Map.MAX_Y;j++)
//                if(states[i][j]!=PointState.Unknown)
//                    knownCount++;
        return getCoverage() >= covLimit;
    }
    public double getCoverage(){
    	int knownCount=0;
        for(int i=0;i<Map.MAX_X;i++)
            for(int j=0;j<Map.MAX_Y;j++)
                if(states[i][j]!=PointState.Unknown)
                    knownCount++;
        double cov = knownCount*100/Map.MAX_X/Map.MAX_Y;
        return cov;
    }
    public String getFirstString(){
        return Descriptor.getFirstStringFromStates(states);
    }
    public String getSecondString(){
        return Descriptor.getSecondStringFromStates(states);
    }
}
