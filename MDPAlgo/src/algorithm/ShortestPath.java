package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

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
	private Map map;
//	private Map actualMap = null;
	private int loopCount;
	private boolean explorationMode;
	private MapLayer mapLayer;
	private SensorData sensorData;
	
	public ShortestPath(Map map, Robot robot) {
		this.map = null;
		initObject(map,robot);
	}
	
//	public ShortestPath(Map map, Robot robot, Map actualMap) {
//		this.actualMap = actualMap;
//		this.explorationMode = false;
//		initObject(map, robot);
//	}
	
	public void initObject(Map map, Robot robot) {
		this.robot = robot;
		this.map = map;
		this.open = new ArrayList<>();
		this.closed = new ArrayList<>();
		this.parents = new HashMap<>();
		this.neighbours = new Point[4];
		this.currentPoint =  map.getPointMap(robot.getPos().x, robot.getPos().y);
		this.currentDir = robot.getOri();
		this.gCosts = new double[Map.MAX_X][Map.MAX_Y];
		
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
		gCosts[robot.getPos().x][robot.getPos().y] = 0;
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
		double turnCost = 20;
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
	public String executeShortestPath(int goalX, int goalY) {
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
			System.out.println("Added (" + currentPoint.getPos().x + ", " + currentPoint.getPos().y + ")");
			
			if(closed.contains(map.getPointMap(goalX, goalY))) {
				System.out.println("Reached goal!");
				path = getPath(goalX, goalY);
				printShortestPath(path);
				return shortestPathMovements(path, goalX, goalY);
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
	private String shortestPathMovements(Stack<Point> path, int goalX, int goalY) {
		StringBuilder movementString = new StringBuilder();
		
		Point p = path.pop();
		Direction targetDir;
		ArrayList<RobotAction> movement = new ArrayList<>();
		Robot r = new Robot(true);
		//Speed?
		
		while((r.getPos().x != goalX) || (r.getPos().y != goalY)) {
			if(r.getPos().x == p.getPos().x && r.getPos().y == p.getPos().y) {
				p = path.pop();
			}
			
			targetDir = getTargetDir(r.getPos().x,r.getPos().y,r.getOri(),p);
			
			RobotAction ra;
			
			if(r.getOri() != targetDir) {
				ra = getTargetMove(r.getOri(), targetDir); 
			}
			else {
				ra = RobotAction.Forward;
			}
			
			System.out.println("Move " + ra.name() + " from (" + r.getPos().x +", " + r.getPos().y + ")");
			r.execute(ra);
			movement.add(ra);
			movementString.append(ra.name());
		}
		
		if(robot.getSimulation() || explorationMode) {
			for (RobotAction mm : movement) {
				if(mm == RobotAction.Forward) {
					if(!canMoveForward()) {
						System.out.println("Early termination of shortest path execution.");
						return "T";
					}
				}
				
				robot.execute(mm);
				//Update map
				
				//During exploration, use sensor data to update map
				if(explorationMode) {
					mapLayer.processSensorData(sensorData, robot);
					//Update map
				}
			}
		}else {
			int fCount = 0;
			for (RobotAction mm : movement) {
				if(mm == RobotAction.Forward) {
					fCount++;
					if(fCount == 10) {
						robot.moveForwardMultiple(fCount);
						fCount = 0;
						//Update map
						}
					}else if (mm == RobotAction.Left || mm == RobotAction.Right) {
						if(fCount > 0) {
							robot.moveForwardMultiple(fCount);
							fCount = 0;
							//update map
						}
						
						robot.execute(mm);
						//update map
					}
				}
			if(fCount > 0) {
				robot.moveForwardMultiple(fCount);
				//Update map
			}
		}
		System.out.println("\nMovements: " + movementString.toString());
		return movementString.toString();
	}
	
	//Returns true if robot can move one point forward in current orientation
	private boolean canMoveForward() {
		int x = robot.getPos().x;
		int y = robot.getPos().y;
		
		switch(robot.getOri()) {
		case North:
			if(map.checkIsFree(map.getPointMap(x-1, y+2)) && map.checkIsFree(map.getPointMap(x, y+2)) && map.checkIsFree(map.getPointMap(x+1, y+2))) {
				return true;
			}
			break;
		case East:
			if(map.checkIsFree(map.getPointMap(x+2, y-1)) && map.checkIsFree(map.getPointMap(x+2, y)) && map.checkIsFree(map.getPointMap(x+2, y+1))) {
				return true;
			}
			break;
		case South:
			if(map.checkIsFree(map.getPointMap(x-1, y-2)) && map.checkIsFree(map.getPointMap(x, y-2)) && map.checkIsFree(map.getPointMap(x+1, y-2))) {
				return true;
			}
			break;
		case West:
			if(map.checkIsFree(map.getPointMap(x-2, y-1)) && map.checkIsFree(map.getPointMap(x-2, y)) && map.checkIsFree(map.getPointMap(x-2, y+1))) {
				return true;
			}
			break;				
		}
		return false;
	}
	
	
	//Returns the movements to execute to get from one direction to another
	private RobotAction getTargetMove(Direction a, Direction b) {
		switch(a) {
		case North:
			switch(b) {
			case North:
				return RobotAction.Error;
			case South:
				return RobotAction.Left;
			case West:
				return RobotAction.Left;
			case East:
				return RobotAction.Right;
			}
			break;
		case South:
			switch(b) {
			case North:
				return RobotAction.Left;
			case South:
				return RobotAction.Error;
			case West:
				return RobotAction.Right;
			case East:
				return RobotAction.Left;
			}
			break;
		case West:
			switch(b) {
			case North:
				return RobotAction.Right;
			case South:
				return RobotAction.Left;
			case West:
				return RobotAction.Error;
			case East:
				return RobotAction.Left;
			}
			break;
		case East:
			switch(b) {
			case North:
				return RobotAction.Left;
			case South:
				return RobotAction.Right;
			case West:
				return RobotAction.Left;
			case East:
				return RobotAction.Error;
			}
		}
		return RobotAction.Error;
	}
	
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
	
	public Point findNearestExploredPoint(LinkedList<Vector> v) {
		Point[] neighbours = new Point[4];
		int size = v.size(); 												// No of unknown points
		for (int i = 0 ; i < size; i++) {
			//Setup neighbours of current cell
			if(map.checkInsideBoundary(v.get(i).x+1, v.get(i).y)) {
				neighbours[0] = map.getPointMap(v.get(i).x + 1,  v.get(i).y);
				if(neighbours[0].getState() != PointState.IsFree)
					neighbours[0] = null;
			}
			if(map.checkInsideBoundary(v.get(i).x-1, v.get(i).y)) {
				neighbours[1] = map.getPointMap(v.get(i).x - 1, v.get(i).y);
				if(neighbours[1].getState() != PointState.IsFree)
					neighbours[1] = null;
			}
			if(map.checkInsideBoundary(v.get(i).x,v.get(i).y-1)) {
				neighbours[2] = map.getPointMap(v.get(i).x, v.get(i).y - 1);
				if(neighbours[2].getState() != PointState.IsFree)
					neighbours[2] = null;
			}
			if(map.checkInsideBoundary(v.get(i).x,v.get(i).y+1)) {
				neighbours[3] = map.getPointMap(v.get(i).x, v.get(i).y + 1);
				if(neighbours[3].getState() != PointState.IsFree)
					neighbours[3] = null;
			}
			
			for (int j = 0 ; j < 4; j++) {
				if(neighbours[j] != null)
					return neighbours[j];
			}
		}
		return null;
	}
}
