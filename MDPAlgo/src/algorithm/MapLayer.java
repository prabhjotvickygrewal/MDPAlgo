
package algorithm;
import map.*;
import robot.*;
import java.util.LinkedList;

/**
 *
 * @author WGUO002
 */
public class MapLayer {
    private Map map;
    private PointState[][] states;
    public static final int Sensor_ShortRange=5;
    public static final int Sensor_LongRange=8;

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
        
        if(s.left_l>0 && s.left_l<=Sensor_LongRange){
            setStateAt(pos.nAdd(leftVector.nMultiply(s.left_l+1)).nAdd(downVector), PointState.Obstacle);
            for(int i=1;i<s.left_l;i++)
                setStateAt(pos.nAdd(leftVector.nMultiply(i+1)).nAdd(downVector), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_LongRange;i++)
                setStateAt(pos.nAdd(leftVector.nMultiply(i+1)).nAdd(downVector), PointState.IsFree);
        
        if(s.left_m>0 && s.left_m<=Sensor_LongRange){
            setStateAt(pos.nAdd(leftVector.nMultiply(s.left_m+1)), PointState.Obstacle);
            for(int i=1;i<s.left_m;i++)
                setStateAt(pos.nAdd(leftVector.nMultiply(i+1)), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_LongRange;i++)
                setStateAt(pos.nAdd(leftVector.nMultiply(i+1)), PointState.IsFree);
            
        if(s.left_r>0 && s.left_r<=Sensor_LongRange){
            setStateAt(pos.nAdd(leftVector.nMultiply(s.left_r+1)).nAdd(upVector), PointState.Obstacle);
            for(int i=1;i<s.left_r;i++)
                setStateAt(pos.nAdd(leftVector.nMultiply(i+1)).nAdd(upVector), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_LongRange;i++)
                setStateAt(pos.nAdd(leftVector.nMultiply(i+1)).nAdd(upVector), PointState.IsFree);
        
        if(s.right_r>0 && s.right_r<=Sensor_ShortRange){
            setStateAt(pos.nAdd(rightVector.nMultiply(s.left_l+1)).nAdd(downVector), PointState.Obstacle);
            for(int i=1;i<s.right_r;i++)
                setStateAt(pos.nAdd(rightVector.nMultiply(i+1)).nAdd(downVector), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_ShortRange;i++)
                setStateAt(pos.nAdd(rightVector.nMultiply(i+1)).nAdd(downVector), PointState.IsFree);
            
        if(s.right_m>0 && s.right_m<=Sensor_ShortRange){
            setStateAt(pos.nAdd(rightVector.nMultiply(s.left_l+1)), PointState.Obstacle);
            for(int i=1;i<s.right_m;i++)
                setStateAt(pos.nAdd(rightVector.nMultiply(i+1)), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_ShortRange;i++)
                setStateAt(pos.nAdd(rightVector.nMultiply(i+1)), PointState.IsFree);
        
        if(s.right_l>0 && s.right_l<=Sensor_ShortRange){
            setStateAt(pos.nAdd(rightVector.nMultiply(s.left_l+1)).nAdd(upVector), PointState.Obstacle);
            for(int i=1;i<s.right_l;i++)
                setStateAt(pos.nAdd(rightVector.nMultiply(i+1)).nAdd(upVector), PointState.IsFree);
        }
        else
            for(int i=1;i<=Sensor_ShortRange;i++)
                setStateAt(pos.nAdd(rightVector.nMultiply(i+1)).nAdd(upVector), PointState.IsFree);
        
        if(s.up_l>0 && s.up_l<=Sensor_ShortRange){
            setStateAt(pos.nAdd(upVector.nMultiply(s.up_l+1)).nAdd(leftVector), PointState.Obstacle);
            for(int i=1;i<s.up_l;i++)
                setStateAt(pos.nAdd(upVector.nMultiply(s.up_l+1)).nAdd(leftVector), PointState.IsFree);
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
        map.updatePointMap(states);
    }
    public LinkedList<Vector> getRemainedPoint(Vector pos){
        LinkedList<Vector> remainedPoint=new LinkedList<>();
        Vector curPoint;
        for(int i=1;i<=Map.MAX_X;i++){
            curPoint=pos.nAdd(new Vector(-i,-i));
            for(int j=0;j<i*2;j++){
                if(checkIsUnknown(curPoint))
                    remainedPoint.add(curPoint);
                curPoint.add(new Vector(1,0));
            }
            for(int j=0;j<i*2;j++){
                if(checkIsUnknown(curPoint))
                    remainedPoint.add(curPoint);
                curPoint.add(new Vector(0,1));
            }
            for(int j=0;j<i*2;j++){
                if(checkIsUnknown(curPoint))
                    remainedPoint.add(curPoint);
                curPoint.add(new Vector(-1,0));
            }
            for(int j=0;j<i*2;j++){
                if(checkIsUnknown(curPoint))
                    remainedPoint.add(curPoint);
                curPoint.add(new Vector(0,-1));
            }
        }
        return remainedPoint;
    }
    public boolean checkEnclosureLargeEnough(Vector v){
        Vector cur;
        for(int i=-1;i<=1;i++)
            for(int j=-1;j<=1;j++){
                cur=v.nAdd(new Vector(i,j));
                if(!checkInsideBoundary(cur) || !(checkIsUnknown(cur) || checkIsFree(cur)))
                    return false;
            }
        return true;
    }
    public boolean checkExplorable(Vector v){
    	boolean isUpperBlocked=checkIsBlockFree(v.nAdd(new Vector(0,2)));
    	boolean isLowerBlocked=checkIsBlockFree(v.nAdd(new Vector(0,-2)));
    	boolean isRightBlocked=checkIsBlockFree(v.nAdd(new Vector(2,0)));
    	boolean isLeftBlocked=checkIsBlockFree(v.nAdd(new Vector(-2,0)));    	
    	return !(isUpperBlocked && isLowerBlocked && isRightBlocked && isLeftBlocked);
    }
    
    public void setStateAt(Vector v,PointState pState){
        if(checkInsideBoundary(v)){
            if(states[v.x][v.y]==PointState.Unknown)
                states[v.x][v.y]=pState;
            else if(states[v.x][v.y]!=pState)
                System.out.println("Data Inconsistency!");
        }
    }
    public boolean checkInsideBoundary(Vector v){
        return map.checkInsideBoundary(v);
    }
    public boolean checkIsFree(Vector v){
        if(checkInsideBoundary(v))
            if(states[v.x][v.y]==PointState.IsFree)
                return true;
        return false;
    }
    public boolean checkIsBlockFree(Vector v){
    	boolean isFree=true;
    	for(int i=-1;i<=1;i++)
    		for(int j=-1;j<=1;j++)
    			if(!checkIsFree(v.nAdd(new Vector(i,j))))
    				isFree=false;
    	return isFree;
    }
    public boolean checkIsUnknown(Vector v){
        if(checkInsideBoundary(v))
            if(states[v.x][v.y]==PointState.Unknown)
                return true;
        return false;
    }
    public void updateMap(){
        map.updatePointMap(states);
    }
    public boolean checkCovLimitReached(int covLimit){
        int knownCount=0;
        for(int i=0;i<Map.MAX_X;i++)
            for(int j=0;j<Map.MAX_Y;j++)
                if(states[i][j]!=PointState.Unknown)
                    knownCount++;
        return knownCount*100/Map.MAX_X/Map.MAX_Y > covLimit;
    }
}
