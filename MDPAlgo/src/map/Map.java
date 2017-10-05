
package map;
import java.util.LinkedList;
/**
 *
 * @author user
 */
public class Map {
    public static final int MAX_X=20;
    public static final int MAX_Y=15;
    private Point[][] pointMap;
    
    //Initiate map with all points unknown
    public Map(){
        pointMap=new Point[MAX_X][MAX_Y];
        for(int i=0;i<MAX_X;i++)
            for(int j=0;j<MAX_Y;j++){
                pointMap[i][j]=new Point(i,j,PointState.Unknown);
            }
    }
    public Map(PointState s){
        pointMap=new Point[MAX_X][MAX_Y];
        for(int i=0;i<MAX_X;i++)
            for(int j=0;j<MAX_Y;j++){
                pointMap[i][j]=new Point(i,j,s);
            }
    }
    //update map with obstacles
    public Map(LinkedList<Vector> obstacleList){
        pointMap=new Point[MAX_X][MAX_Y];
        for(int i=0;i<MAX_X;i++)
            for(int j=0;j<MAX_Y;j++){
                pointMap[i][j]=new Point(i,j,PointState.IsFree);
            }
        for(Vector v:obstacleList)
            pointMap[v.x][v.y].setState(PointState.Obstacle);
    }
    
    
    public void updatePointMap(PointState[][] states){
        for(int i=0;i<MAX_X;i++)
            for(int j=0;j<MAX_Y;j++)
                pointMap[i][j].setState(states[i][j]);
    }
    
    
    public Point getPointMap(int row, int col){
        return pointMap[row][col];
    }
    
    
    public PointState getPointStateAt(Vector v){
        return pointMap[v.x][v.y].getState();
    }
    
    
    public void setPointStateAt(Vector v, PointState state){
        if(checkInsideBoundary(v))
            pointMap[v.x][v.y].setState(state);
    }

    public void updateVirtualWall() {
    	for(int i=0;i<MAX_X;i++)
            for(int j=0;j<MAX_Y;j++) {
            	if((i == 0 || i == MAX_X-1 || j ==0 || j == MAX_Y-1) && (pointMap[i][j].getState() != PointState.Obstacle))
            		pointMap[i][j].setState(PointState.VirtualWall);
                if(pointMap[i][j].getState() == PointState.Obstacle) {
                	for(int a = -1; a <= 1;a++)
                		for (int b = -1; b < 1; b++) {
                			if(checkInsideBoundary(i+a,j+b) && pointMap[i+a][j+b].getState() != PointState.Obstacle) {
                				pointMap[i+a][j+b].setState(PointState.VirtualWall);
                			}
                		}
                }
            }
    }
    
    public boolean checkInsideBoundary(Vector v){
    if(v.x>=0 && v.x<MAX_X && v.y>=0 && v.y<MAX_Y)
        return true;
    else
        return false;
    }
    
    public boolean checkInsideBoundary(Point p){
        if(p.getPos().x>=0 && p.getPos().x<MAX_X && p.getPos().y>=0 && p.getPos().y<MAX_Y)
            return true;
        else
            return false;
        }
    public boolean checkInsideBoundary(int x, int y){
        if(x>=0 && x<MAX_X && y>=0 && y<MAX_Y)
            return true;
        else
            return false;
        }
    
    public boolean checkIsFree(Vector v){
        if(checkInsideBoundary(v))
            if(getPointStateAt(v)==PointState.IsFree || getPointStateAt(v)==PointState.VirtualWall)
                return true;
        return false;
    }
    public boolean checkIsFree(Point p){
        if(checkInsideBoundary(p))
            if(p.getState()==PointState.IsFree || p.getState()==PointState.VirtualWall)
                return true;
        return false;
    } 
    
    public void printMap(){
        for(int j=MAX_Y-1;j>=0;j--){
            for(int i=0;i<MAX_X;i++)
                switch(pointMap[i][j].getState()){
                    case IsFree: System.out.print(". "); break;
                    case Obstacle: System.out.print("x "); break;
                    case Unknown: System.out.print("? "); break;
                    default: break;
                }
            System.out.println();
        }
    }
}
