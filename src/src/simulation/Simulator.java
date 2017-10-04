
package simulation;
import map.*;
import algorithm.*;
import robot.*;
/**
 *
 * @author WGUO002
 */
public class Simulator {
    private Map map;
    public Simulator(Map map){
        this.map=map;
    }
    
    public SensorData getSensorData(Robot r){
        SensorData data=new SensorData();
        Vector pos=r.getPos();
        Direction ori=r.getOri();
        Vector rightVector=ori.getRight().toVector();
        Vector leftVector=ori.getLeft().toVector();
        Vector downVector=ori.getDown().toVector();
        data.up_l=getObstacleAt(pos.nAdd(ori.toVector()).nAdd(leftVector), ori, MapLayer.Sensor_ShortRange);
        data.up_m=getObstacleAt(pos.nAdd(ori.toVector()), ori, MapLayer.Sensor_ShortRange);
        data.up_r=getObstacleAt(pos.nAdd(ori.toVector()).nAdd(rightVector), ori, MapLayer.Sensor_ShortRange);
        data.left_m=getObstacleAt(pos.nAdd(leftVector), ori.getLeft(), MapLayer.Sensor_LongRange);
        data.right_t=getObstacleAt(pos.nAdd(rightVector).nAdd(ori.toVector()), ori.getRight(), MapLayer.Sensor_ShortRange);
        data.right_b=getObstacleAt(pos.nAdd(rightVector).nAdd(downVector), ori.getRight(), MapLayer.Sensor_ShortRange);
        
        return data;
    }
    public int getObstacleAt(Vector pos,Direction dir,int range){
        boolean obstacleFound=false;
        int i;
        Vector v=pos.nAdd(dir.toVector());
        for(i=1;i<=range;i++){
            if(!checkInsideBoundary(v))
                break;
            else if(!checkIsFree(v)){
                obstacleFound=true;
                break;
            }
            v.add(dir.toVector());
        }
        if(!obstacleFound)
            return 0;
        return i;
    }
    public boolean checkInsideBoundary(Vector v){
        return map.checkInsideBoundary(v);
    }
    public boolean checkIsFree(Vector v){
        return map.checkIsFree(v);
    }
    public void setObstacleAt(Vector v){
        map.setPointStateAt(v, PointState.Obstacle);
    }
    public void setFreeAt(Vector v){
        map.setPointStateAt(v, PointState.Obstacle);
    }
}
