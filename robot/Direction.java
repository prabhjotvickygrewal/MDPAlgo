
package robot;
import map.*;
/**
 *
 * @author user
 */
public enum Direction {
    North, South, East, West;
    public Direction getRight(){             //get the direction on map after turning
        switch(this){
            case North: return East;
            case South: return West;
            case East: return South;
            case West: return North;
            default: return East;
        }
    }
    public Direction getLeft(){
        switch(this){
            case North: return West;
            case South: return East;
            case East: return North;
            case West: return South;
            default: return West;
        }
    }
    public Direction getDown(){
        switch(this){
            case North: return South;
            case South: return North;
            case East: return West;
            case West: return East;
            default: return South;
        }
    }
    public Vector toVector(){
        switch(this){
            case North: return new Vector(0,1);
            case South: return new Vector(0,-1);
            case East: return new Vector(1,0);
            case West: return new Vector(-1,0);
            default: return new Vector(0,0);
        }
    }
}
