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
    	final int timeLimit,covLimit, stepPerSecond;
    	if(time.length()!=0)
    		timeLimit=Integer.parseInt(time);
    	else
    		timeLimit=100;
    	
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
            public void done(){
            	GUI.explored=true;
            }
        };
        explore.execute();
    }
    public void shortestPath(Robot r, Map m, String speed, boolean isSimulating){
    	int stepPerSecond;
    	if(GUI.explored)
    		sp=new ShortestPath(r.getMap(),r, false);
    	else
    		sp=new ShortestPath(m,r, false);
    	
    	if(algo==null){
            Simulator simulator=new Simulator(m);
            algo=new Algorithm(simulator,r,isSimulating);
    	}
    	
    	if(speed.length()!=0)
    		stepPerSecond=Integer.parseInt(speed);
    	else
    		stepPerSecond=2;
    	
    	Robot.delay=1000/stepPerSecond;
    	shortestPath=new SwingWorker<Integer,Integer>(){
    		@Override
    		public Integer doInBackground(){
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
    public void exit(){
    	if(!Algorithm.isSimulating)
    		Comm.close();
    	System.exit(0);
    }
}
