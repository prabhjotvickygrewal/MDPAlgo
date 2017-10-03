/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation;
import algorithm.*;
import robot.*;
import map.*;
import javax.swing.SwingWorker;

/**
 *
 * @author WGUO002
 */
public class EventHandler {
    private GUI gui;
    private SwingWorker explore;
    public EventHandler(GUI gui){
        this.gui=gui;
    }
    
    public void startExploration(Robot r, Map map, String time, String cov, boolean isSimulating){
    	final int timeLimit,covLimit;
    	if(time.length()!=0)
    		timeLimit=Integer.parseInt(time);
    	else
    		timeLimit=100;
    	if(cov.length()!=0)
    		covLimit=Integer.parseInt(cov);
    	else
    		covLimit=100;
        Simulator simulator=new Simulator(map);
        final Algorithm algo=new Algorithm(simulator,r,isSimulating);
        explore=new SwingWorker<Integer, Integer>(){
            @Override
            public Integer doInBackground(){
                algo.explore(timeLimit, covLimit, gui);
                return 0;
            }
            public void done(){
            	gui.isExploring=false;
            }
        };
        explore.execute();
    }
    public void shortestPath(Robot r){
    	
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
}
