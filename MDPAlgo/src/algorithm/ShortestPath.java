package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import communication.Comm;
import simulation.*;
import map.Map;
import map.Point;
import map.PointState;
import map.Vector;
import robot.Direction;
import robot.Robot;
import robot.RobotAction;

public class ShortestPath {
	private ArrayList<Point> open;
	private ArrayList<Point> closed;
	private HashMap<Point, Point> parents;
	private Point currentPoint;
	private Point[] neighbours;
	private Direction currentDir;
	private double[][] gCosts;
	private Robot robot;
	private Robot virtualRobot;
	private Map map;					//explored map
//	private Map actualMap = null;		//From simulator
	private int loopCount;
	private boolean explorationMode=true;
//	private MapLayer mapLayer;
	
	public ShortestPath(Map map, Robot robot, boolean explorationMode) {
//		this.actualMap = null;
		initObject(map,robot, explorationMode);
	}
	
	public void initObject(Map map, Robot robot, boolean explorationMode) {
		this.explorationMode=explorationMode;
		this.robot = robot;
		this.map = map;
		this.open = new ArrayList<>();
		this.closed = new ArrayList<>();
		this.parents = new HashMap<>();
		this.neighbours = new Point[4];
		this.gCosts = new double[Map.MAX_X][Map.MAX_Y];
		this.virtualRobot=new Robot(robot.getMap(),robot.getOri(),robot.getPos());
		this.currentPoint =  map.getPointMap(virtualRobot.getPos().x, virtualRobot.getPos().y);
		this.currentDir = virtualRobot.getOri();
		virtualRobot.setVirtual(true);
		
		map.updateVirtualWall();
		
		for (int i = 0; i < Map.MAX_X; i++) {
			for (int j= 0; j < Map.MAX_Y; j++) {
				Point point = map.getPointMap(i,j);
				if(point.getState() != PointState.IsFree) {
					gCosts[i][j] = 999;
				}else {
					gCosts[i][j] = -1;
				}
			}
		}
		open.add(currentPoint);
		
		//Initialise starting point
		gCosts[virtualRobot.getPos().x][virtualRobot.getPos().y] = 0;
		this.loopCount = 0;
	}
	
	//returns the point to visit with lowest g(n) + h(n)
	private Point pointWithLowestCost(int goalX, int goalY) {
		int size = open.size();
		double minCost = 999;
		Point result = null;
		
		for (int i = size - 1; i >= 0; i--) {
			double gCost = gCosts[open.get(i).getPos().x][open.get(i).getPos().y];
			double cost = gCost + hCost(open.get(i), goalX, goalY);
			if(cost<minCost) {
				minCost = cost;
				result = open.get(i);
			}
		}
		return result;
	}
	
	//Returns the heuristic cost from a point to the given destination
	private double hCost(Point point, int goalX, int goalY) {
		double movementCost = Math.abs((goalY - point.getPos().y) + Math.abs(goalX - point.getPos().x)) * 10;
		
		if (movementCost == 0)
			return 0;
		
		double turnCost = 0;
		if(goalY - point.getPos().y != 0 && goalX - point.getPos().x != 0) {
			turnCost = 20;
		}
		else if(goalY - point.getPos().y == 0 && goalX - point.getPos().x == 0) {
			turnCost = 0;
		}
		else if (goalY - point.getPos().y == 0 || goalX - point.getPos().x == 0) {
			turnCost = 10;
		}
//		if(goalX - point.getPos().x != 0)
//			turnCost = turnCost + 20;
		
		return movementCost + turnCost;
	}
	
	//Returns the target direction of the bot from current pos to target point
	private Direction getTargetDir(int robotX, int robotY, Direction robotDir, Point target) {
		if(robotX - target.getPos().x > 0)
			return Direction.West;
		else if (target.getPos().x - robotX > 0)
			return Direction.East;
		else {
			if(robotY - target.getPos().y > 0)
				return Direction.South;
			else if (target.getPos().y - robotY > 0)
				return Direction.North;
			else return robotDir;
		}
	}
	
	//Returns turning cost of specified turn
	private Double getTurningCost(Direction i, Direction j) {
		double turnCost = 10;
		int numOfTurns = Math.abs(i.ordinal() - j.ordinal());
		if (numOfTurns > 2)
			numOfTurns = numOfTurns % 2;
		return (numOfTurns * turnCost);
	}
	
	//Returns the actual cost of moving from one point to a neighbouring point
	private double gCost(Point a, Point b, Direction dir) {
		double movementCost = 10;
		
		double turnCost;
		Direction targetDir = getTargetDir(a.getPos().x, a.getPos().y, dir,b);
		turnCost = getTurningCost(dir, targetDir);
		
		return movementCost + turnCost;
	}
		
	//Returns shortest path from one point to another
	public ArrayList<RobotAction> findShortestPath(int goalX, int goalY, GUI gui) {
		System.out.println("Calculating fastest path from (" + currentPoint.getPos().x + ", " + currentPoint.getPos().y + ") to (" + goalX + ", " + goalY + ")");
		
		Stack<Point> path;
		do {
			loopCount++;
			
			//Get point with minimum cost from open and assign it to current point
			currentPoint = pointWithLowestCost(goalX, goalY);
			
			//Turn the robot to point in the direction of the current point from previous point
			if(parents.containsKey(currentPoint))
				currentDir = getTargetDir(parents.get(currentPoint).getPos().x, parents.get(currentPoint).getPos().y, currentDir, currentPoint);
			
			closed.add(currentPoint);
			open.remove(currentPoint);
//			System.out.println("Added (" + currentPoint.getPos().x + ", " + currentPoint.getPos().y + ")");
			
			if(closed.contains(map.getPointMap(goalX, goalY))) {
//				System.out.println("Reached goal!");
				path = getPath(goalX, goalY);
				printShortestPath(path);
				return shortestPathMovements(path, goalX, goalY, gui);
			}
			
			//Setup neighbours of current cell
			if(map.checkInsideBoundary(currentPoint.getPos().x+1, currentPoint.getPos().y)) {
				neighbours[0] = map.getPointMap(currentPoint.getPos().x + 1,  currentPoint.getPos().y);
				if(neighbours[0].getState() != PointState.IsFree)
					neighbours[0] = null;
			}
			if(map.checkInsideBoundary(currentPoint.getPos().x-1, currentPoint.getPos().y)) {
				neighbours[1] = map.getPointMap(currentPoint.getPos().x - 1, currentPoint.getPos().y);
				if(neighbours[1].getState() != PointState.IsFree)
					neighbours[1] = null;
			}
			if(map.checkInsideBoundary(currentPoint.getPos().x,currentPoint.getPos().y-1)) {
				neighbours[2] = map.getPointMap(currentPoint.getPos().x, currentPoint.getPos().y - 1);
				if(neighbours[2].getState() != PointState.IsFree)
					neighbours[2] = null;
			}
			if(map.checkInsideBoundary(currentPoint.getPos().x,currentPoint.getPos().y+1)) {
				neighbours[3] = map.getPointMap(currentPoint.getPos().x, currentPoint.getPos().y + 1);
				if(neighbours[3].getState() != PointState.IsFree)
					neighbours[3] = null;
			}
			
			//Iterate through neighbours and update the g(n) values of each
			for (int i = 0; i < 4; i++) {
				if(neighbours[i] != null) {
					if(closed.contains(neighbours[i]))
						continue;
					if(!(open.contains(neighbours[i]))) {
						parents.put(neighbours[i], currentPoint);
						gCosts[neighbours[i].getPos().x][neighbours[i].getPos().y] = gCosts[currentPoint.getPos().x][currentPoint.getPos().y] + gCost(currentPoint,neighbours[i],currentDir);
						open.add(neighbours[i]);						
					}else {
						double currentGScore = gCosts[neighbours[i].getPos().x][neighbours[i].getPos().y];
						double newGScore = gCosts[currentPoint.getPos().x][currentPoint.getPos().y] + gCost(currentPoint, neighbours[i], currentDir);
						if(newGScore < currentGScore) {
							gCosts[neighbours[i].getPos().x][neighbours[i].getPos().y] = newGScore;
							parents.put(neighbours[i], currentPoint);
						}
					}
				}
			}
		}while (!open.isEmpty());
		
		System.out.println("Path could not be found.");
		return null;
	}
	
	
	//Generates path in reverse using parents HashMap
	private Stack<Point> getPath(int goalX, int goalY){
		Stack<Point> actualPath = new Stack<>();
		Point p = map.getPointMap(goalX, goalY);
		
		while(true) {
			actualPath.push(p);
			p = parents.get(p);
			if(p==null)
				break;
		}
		
		return actualPath;
	}
	
	
	//Returns movements for shortestpath
	private ArrayList<RobotAction> shortestPathMovements(Stack<Point> path, int goalX, int goalY, GUI gui) {
		StringBuilder movementString = new StringBuilder();
//		Robot virtualR=new Robot(robot.getMap(),robot.getOri(),robot.getPos());
		virtualRobot.setVirtual(true);
		Point p = path.pop();
		Direction targetDir;
		ArrayList<RobotAction> movement = new ArrayList<>();
		
		while((virtualRobot.getPos().x != goalX) || (virtualRobot.getPos().y != goalY)) {
			if(virtualRobot.getPos().x == p.getPos().x && virtualRobot.getPos().y == p.getPos().y) {
				p = path.pop();
			}
			
			targetDir = getTargetDir(virtualRobot.getPos().x,virtualRobot.getPos().y,virtualRobot.getOri(),p);
			
			RobotAction ra;
			
			if(virtualRobot.getOri() != targetDir) {
				ra = virtualRobot.getTargetMove(targetDir); 
			}
			else {
				ra = RobotAction.Forward;
			}
			System.out.println("Move " + ra.name() + " from (" + virtualRobot.getPos().x +", " + virtualRobot.getPos().y + ")");
			virtualRobot.execute(ra);
			movement.add(ra);
			movementString.append(ra.name());
		}
		
		return movement;
		
	}
	
	public void executeMovement(ArrayList<RobotAction> movement, GUI gui){
		if(!Algorithm.isSimulating && robot.isFastestRun() && !explorationMode)
			Comm.sendToRobot("8,1");     //for executing fastest run
		
		int fCount = 0;
		for (RobotAction mm : movement) {
			if(mm == RobotAction.Forward) {
				fCount++;
			}
			else if (mm == RobotAction.Left || mm == RobotAction.Right || mm == RobotAction.Backward) {
				if(fCount > 0) {
					robot.moveForwardMultiple(fCount,gui);
					if(!robot.isFastestRun() && explorationMode) {
//						while(!Algorithm.scan(gui));
						Calibration.forceCalibration();
						gui.getGridPanel().getGridContainer().drawGrid(map, robot);
					}
					fCount = 0;
				}						
				robot.execute(mm);
				if(explorationMode) {
					gui.getGridPanel().getGridContainer().drawGrid(map, robot);
				}
//				else{
//					gui.getGridPanel().getGridContainer().drawGrid(map, robot);
//				}
				if(explorationMode && Algorithm.checkTimeLimitReached())
					return;
			}
		}
		if(fCount > 0) {
			robot.moveForwardMultiple(fCount,gui);
			if(explorationMode) {
				while(!Algorithm.scan(gui));
			}
//			else{
//				gui.getGridPanel().getGridContainer().drawGrid(map, robot);
//			}
//		    if(explorationMode && Algorithm.checkTimeLimitReached())
//				return;
		}
		
		//for terminating message sending and update GUI according to execution
		if(robot.isFastestRun() && !explorationMode){
			if(!Algorithm.isSimulating)
				Comm.sendToAndroid("9");
			fCount = 0;
			for (RobotAction mm : movement) {
				if(mm == RobotAction.Forward) {
					fCount++;
				}
				else if (mm == RobotAction.Left || mm == RobotAction.Right || mm == RobotAction.Backward) {
					if(fCount > 0) {
						robot.updateGUI(fCount, gui);
						if(!Algorithm.isSimulating)
							while(!Comm.checkActionCompleted());
						fCount = 0;
					}						
					if(!Algorithm.isSimulating)
						while(!Comm.checkActionCompleted());
					robot.setOriByTurn(mm);
					gui.getGridPanel().getGridContainer().drawGrid(map, robot);
				}
			}
			if(fCount > 0) {
				robot.updateGUI(fCount,gui);
				if(!Algorithm.isSimulating)
					while(!Comm.checkActionCompleted());
			}
		}
		
		System.out.println("\nMovements: " + movement.toString());
		return;
	}
	

    public boolean isUpFree(){
        Vector rightVector=robot.getOri().getRight().toVector();
        Vector upVector=robot.getOri().toVector();
        Vector leftVector=robot.getOri().getLeft().toVector();
        boolean up_l=map.checkIsFree(robot.getPos().nAdd(upVector.nMultiply(2)).nAdd(leftVector));
        boolean up_m=map.checkIsFree(robot.getPos().nAdd(upVector.nMultiply(2)));
        boolean up_r=map.checkIsFree(robot.getPos().nAdd(upVector.nMultiply(2)).nAdd(rightVector));
        if(up_l && up_m && up_r)                    //check whether can move forward
            return true;
        else
            return false;
    }
	//Returns the movements to execute to get from one direction to another
//	private RobotAction getTargetMove(Direction a, Direction b) {
//		switch(a) {
//		case North:
//			switch(b) {
//			case North:
//				return RobotAction.Error;
//			case South:
//				return RobotAction.Backward;
//			case West:
//				return RobotAction.Left;
//			case East:
//				return RobotAction.Right;
//			}
//			break;
//		case South:
//			switch(b) {
//			case North:
//				return RobotAction.Backward;
//			case South:
//				return RobotAction.Error;
//			case West:
//				return RobotAction.Right;
//			case East:
//				return RobotAction.Left;
//			}
//			break;
//		case West:
//			switch(b) {
//			case North:
//				return RobotAction.Right;
//			case South:
//				return RobotAction.Left;
//			case West:
//				return RobotAction.Error;
//			case East:
//				return RobotAction.Backward;
//			}
//			break;
//		case East:
//			switch(b) {
//			case North:
//				return RobotAction.Left;
//			case South:
//				return RobotAction.Right;
//			case West:
//				return RobotAction.Backward;
//			case East:
//				return RobotAction.Error;
//			}
//		}
//		return RobotAction.Error;
//	}
	
	//Prints the shortest path from stack
	private void printShortestPath(Stack<Point> path) {
		System.out.println("\nLooped " + loopCount + " times.");
		System.out.println("The number of steps are: " + (path.size() - 1) + "\n");
		
		Stack<Point> printPath = (Stack<Point>) path.clone();
		Point p;
		System.out.println("Path: ");
		while(!printPath.isEmpty()){
			p = printPath.pop();
			if(!printPath.isEmpty())
				System.out.println("(" + p.getPos().x + ", " + p.getPos().y + ") --> ");
			else
				System.out.println("(" + p.getPos().x + ", " + p.getPos().y + ")");
		}
		System.out.println("\n");
	}
	
	//Prints the current g(n) values for the points
	public void printGCosts() {
		for(int i = 0; i < Map.MAX_X; i++) {
			for(int j = 0; j < Map.MAX_Y; j++) {
				System.out.println(gCosts[Map.MAX_Y-1-i][j]);
				System.out.println(";");
			}
			System.out.println("\n");
		}
	}
	
	public void updateVirtualRobot(Robot r){
		this.virtualRobot=new Robot(r.getMap(),r.getOri(),r.getPos());
		this.currentPoint =  map.getPointMap(virtualRobot.getPos().x, virtualRobot.getPos().y);
		this.currentDir = virtualRobot.getOri();
		virtualRobot.setVirtual(true);
		open.remove(0);
		open.add(currentPoint);
	}
	
	public Robot getVirtualRobot() {
		return virtualRobot;
	}

}
