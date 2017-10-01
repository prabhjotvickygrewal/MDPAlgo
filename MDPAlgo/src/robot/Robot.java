
package robot;
import map.*;
import java.util.LinkedList;
/**
 *
 * @author user
 */
public class Robot {
    private Map map;
    private Direction ori;
    private Vector pos;
    private LinkedList<RobotAction> buffer;
    public Robot(){
        map=new Map();
        ori=Direction.Right;
        pos=new Vector(1,1);
        buffer=new LinkedList<RobotAction>();
    }
    public void bufferAction(RobotAction action){
        buffer.add(action);
    }
    public void execute(RobotAction action){
        switch(action){
            case MoveForward:
                pos.add(ori.toVector());
                break;
            case MoveBackward:
                pos.add(ori.getDown().toVector());
                break;
            case TurnRight:
                ori=ori.getRight();
                break;
            case TurnLeft:
                ori=ori.getLeft();
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
    public Direction getOri(){
        return ori;
    }
}
