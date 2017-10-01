
package robot;
import map.*;
/**
 *
 * @author user
 */
public enum Direction {
    Up, Down, Right, Left;
    public Direction getRight(){             //get the direction on map after turning
        switch(this){
            case Up: return Right;
            case Down: return Left;
            case Right: return Down;
            case Left: return Up;
            default: return Right;
        }
    }
    public Direction getLeft(){
        switch(this){
            case Up: return Left;
            case Down: return Right;
            case Right: return Up;
            case Left: return Down;
            default: return Left;
        }
    }
    public Direction getDown(){
        switch(this){
            case Up: return Down;
            case Down: return Up;
            case Right: return Left;
            case Left: return Right;
            default: return Down;
        }
    }
    public Vector toVector(){
        switch(this){
            case Up: return new Vector(0,1);
            case Down: return new Vector(0,-1);
            case Right: return new Vector(1,0);
            case Left: return new Vector(-1,0);
            default: return new Vector(0,0);
        }
    }
}
