
package map;

/**
 *
 * @author user
 */
public class Point {
    private PointState pointState;
    private final Vector pos;
    public Point(Vector v, PointState pState){
        this.pos=v;
        this.pointState=pState;        
    }
    public Point(int x, int y, PointState pState){
        Vector v=new Vector(x,y);
        this.pos=v;
        this.pointState=pState;
    }
    public Vector getPos(){
        return pos;
    }
    public PointState getState(){
        return pointState;
    }
    public void setState(PointState pState){
        this.pointState=pState;
    }
}
