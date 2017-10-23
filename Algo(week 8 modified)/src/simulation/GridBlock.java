package simulation;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import map.Vector;

public class GridBlock extends JPanel{
	private Vector vector;
	public GridBlock(Vector v){
		vector=v;
		JLabel label=new JLabel(v.toString());
		label.setForeground(ColorConfig.NORMAL);
		label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
		this.add(label);
		this.setBackground(ColorConfig.NORMAL);
	}
	public Vector getVector(){
		return vector;
	}
	public void toggleBackground(){
		if(this.getBackground()==ColorConfig.NORMAL)
			this.setBackground(ColorConfig.OBSTACLE);
		else
			this.setBackground(ColorConfig.NORMAL);
	}
	
}
