package simulation;

import java.awt.*;

import javax.swing.JPanel;
import algorithm.*;
import map.*;
import robot.Robot;
import robot.Direction;
public class GridContainer extends JPanel{
	public static final int BLOCK_SIZE=30;
	public static final int GAP=1;
	private GridBlock[][] grid;
	
	public GridContainer(Map map, Robot r){
		this.setLayout(new FlowLayout(FlowLayout.LEFT, GAP,GAP));
		this.setPreferredSize(new Dimension(BLOCK_SIZE*Map.MAX_Y+GAP*(Map.MAX_Y+1), BLOCK_SIZE*Map.MAX_X+GAP*(Map.MAX_X+1)));
		this.setBackground(ColorConfig.BG);
		drawGrid(map, r);
	}
	
	public void drawGrid(Map map, Robot r){
		boolean isFirstTime=false;
		Vector cur;
	
		if(grid==null){
			grid=new GridBlock[Map.MAX_X][Map.MAX_Y];
			isFirstTime=true;
		}
		for(int i=0;i<Map.MAX_X;i++)
			for(int j=0;j<Map.MAX_Y;j++){
				cur=new Vector(i,j);
				Color target=ColorConfig.NORMAL;
				if(isFirstTime){
					grid[i][j]=new GridBlock(cur);
					grid[i][j].setPreferredSize(new Dimension(BLOCK_SIZE,BLOCK_SIZE));
				}
				switch(map.getPointStateAt(cur)){
				case Obstacle:
					target=ColorConfig.OBSTACLE;
					break;
				case IsFree:
					target=ColorConfig.NORMAL;
					break;
				case Unknown:
					target=ColorConfig.UNKNOWN;
				}
				if(isRobotHead(cur,r))
					target=ColorConfig.ROBOT_HEAD;
				else if(isRobot(cur, r))
					target=ColorConfig.ROBOT_BODY;
				else if(onShortestPath(cur,r))
					target=ColorConfig.PATH;
				else if(isStartZone(cur))
					target=ColorConfig.START;
				else if(isGoalZone(cur))
					target=ColorConfig.GOAL;
				grid[i][j].setBackground(target);
				
				if(isFirstTime)
					this.add(grid[i][j]);
			}
//		if(isFirstTime){
//			for(int j=Map.MAX_Y-1;j>=0;j--)
//				for(int i=0;i<Map.MAX_X;i++)
//					this.add(grid[i][j]);
//		}
	}
	public boolean isRobot(Vector v, Robot r){
		Vector pos=r.getPos();
		for(int i=-1;i<=1;i++)
			for(int j=-1;j<=1;j++)
				if(v.equals(pos.nAdd(new Vector(i,j))))
					return true;
		return false;
	}
	public boolean isRobotHead(Vector v, Robot r){
		return v.equals(r.getPos().nAdd(r.getOri().toVector()));
	}
	public boolean isStartZone(Vector v){
		for(int i=-1;i<2;i++)
			for(int j=-1;j<2;j++)
				if(v.equals(Algorithm.startPoint.nAdd(new Vector(i,j))))
					return true;
		return false;
	}
	public boolean isGoalZone(Vector v){
		for(int i=-1;i<2;i++)
			for(int j=-1;j<2;j++)
				if(v.equals(Algorithm.endPoint.nAdd(new Vector(i,j))))
					return true;
		return false;
	}
	public boolean onShortestPath(Vector cur,Robot r){
		if(r.isFastestRun())
			if(r.onShortestPath(cur))
				return true;
		return false;
	}
	public GridBlock getGrid(int x,int y){
		return grid[x][y];
	}
}
