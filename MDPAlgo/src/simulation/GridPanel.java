package simulation;

import javax.swing.JPanel;
import map.*;
import robot.*;

public class GridPanel extends JPanel{
	private GridContainer gridContainer;
	
	public GridPanel(Map map, Robot r){
		gridContainer=new GridContainer(map,r);
		this.add(gridContainer);
	}
	public GridContainer getGridContainer(){
		return gridContainer;
	}
}
