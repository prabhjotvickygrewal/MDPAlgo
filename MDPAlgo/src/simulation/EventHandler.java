/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;
import algorithm.*;
import communication.Comm;
import robot.*;
import map.*;
import javax.swing.SwingWorker;

/**
 *
 * @author WGUO002
 */
public class EventHandler {
    private GUI gui;
    private Algorithm algo;
    private ShortestPath sp;
    private SwingWorker explore;
    private SwingWorker shortestPath;
    public EventHandler(GUI gui){
        this.gui=gui;
    }
    
    
    public void startExploration(Robot r, Map map, String time, String cov, String speed, boolean isSimulating){
    	GUI.explored=true;
    	final int timeLimit,covLimit, stepPerSecond;
    	if(time.length()!=0)
    		timeLimit=Integer.parseInt(time);
    	else
    		timeLimit=600;
    	
    	if(cov.length()!=0)
    		covLimit=Integer.parseInt(cov);
    	else
    		covLimit=100;
    	
    	if(speed.length()!=0)
    		stepPerSecond=Integer.parseInt(speed);
    	else
    		stepPerSecond=2;
    	
    	Robot.delay=1000/stepPerSecond;
        Simulator simulator=new Simulator(map);
        algo=new Algorithm(simulator,r,isSimulating);
        explore=new SwingWorker<Integer, Integer>(){
            @Override
            public Integer doInBackground(){
                algo.explore(timeLimit, covLimit, gui);
                return 0;
            }
        };
        explore.execute();
    }
    public void shortestPath(Robot r, Map m, String time, String speed, boolean isSimulating){
    	int stepPerSecond, timeLimit;
    	r.setRunShortestPath(true);
    	if(GUI.explored)
    		sp=new ShortestPath(r.getMap(),r, false);
    	else
    		sp=new ShortestPath(m,r, false);
    	
    	if(time.length()!=0)
    		timeLimit=Integer.parseInt(time);
    	else
    		timeLimit=600;
    	if(algo==null){
            Simulator simulator=new Simulator(m);
            algo=new Algorithm(simulator,r,isSimulating);
    	}
        Algorithm.timeLimit=timeLimit;
    	
    	if(speed.length()!=0)
    		stepPerSecond=Integer.parseInt(speed);
    	else
    		stepPerSecond=2;
    	
    	Robot.delay=1000/stepPerSecond;
    	if(Algorithm.androidEnabled){
    		while(!Comm.checkAndroidMessage("shortestpath")){
    			r.setPos(new Vector(Algorithm.startPoint.x,Algorithm.startPoint.y));
    			r.setDefaultOri();
    			gui.getGridPanel().getGridContainer().drawGrid(r.getMap(), r);
    		}
    	}
    	shortestPath=new SwingWorker<Integer,Integer>(){
    		@Override
    		public Integer doInBackground(){
    			if(Algorithm.wayPoint!=null) {
    				sp.executeShortestPath(Algorithm.wayPoint.x, Algorithm.wayPoint.y, gui);
    			}
    	    	r.setRunShortestPath(true);
    			sp=new ShortestPath(GUI.explored?r.getMap():m,r,false);
    			sp.executeShortestPath(Algorithm.endPoint.x, Algorithm.endPoint.y, gui);
    			return 0;
    		}
    	};
    	shortestPath.execute();
    }
    public Map loadMap(String fileName, Map map){
        if(fileName==null)
        	return null;
        map.updatePointMap(Descriptor.getStatesFromFile(fileName));
        return map;
    }
    public void saveMap(String fileName, Map map){
    	PointState[][] states=new PointState[Map.MAX_X][Map.MAX_Y];
    	for(int i=0;i<Map.MAX_X;i++)
    		for(int j=0;j<Map.MAX_Y;j++)
    			states[i][j]=map.getPointStateAt(new Vector(i,j));
    	if(fileName!=null){
    		Descriptor.writeFileFromStates(fileName, states);
    	}
    }
    public void clickBlock(GridBlock block,Map map){
    	Vector v=block.getVector();
    	block.toggleBackground();
    	if(map.getPointStateAt(v)==PointState.IsFree)
    		map.setPointStateAt(v, PointState.Obstacle);
    	else if(map.getPointStateAt(v)==PointState.Obstacle)
    		map.setPointStateAt(v, PointState.IsFree);
    }

    public void exit(){
    	if(!Algorithm.isSimulating)
    		Comm.close();
    	System.exit(0);
    }
}
