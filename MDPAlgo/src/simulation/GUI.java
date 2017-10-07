package simulation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import algorithm.Algorithm;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.JCheckBox;

import java.awt.Color;
import java.util.LinkedList;

import robot.*;
import communication.*;
import map.*;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField timeLimitText;
	private JTextField covLimitText;
	private JTextField speedText;
	private GridPanel gridPanel;
	private Robot robot;
	private Map realMap;
    private Map robotMap;
	public static final int BLOCK_SIZE=30;
	public static final int GAP=1;
    private static EventHandler eventHandler;
    public static boolean explored=false;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
//        LinkedList<Vector> obstacle=new LinkedList<>();
//        obstacle.add(new Vector(4,5));
//        obstacle.add(new Vector(4,6));
//        obstacle.add(new Vector(4,7));
//        obstacle.add(new Vector(4,8));
//        obstacle.add(new Vector(4,9));
//        obstacle.add(new Vector(4,10));
//        obstacle.add(new Vector(5,5));
//        obstacle.add(new Vector(5,6));
//        obstacle.add(new Vector(5,7));
//        obstacle.add(new Vector(5,8));
//        obstacle.add(new Vector(5,9));
//        obstacle.add(new Vector(5,10));
//        obstacle.add(new Vector(11,5));
//        obstacle.add(new Vector(11,6));
//        obstacle.add(new Vector(11,7));
//        obstacle.add(new Vector(11,8));
//        obstacle.add(new Vector(11,9));
//        obstacle.add(new Vector(11,10));
//        obstacle.add(new Vector(10,5));
//        obstacle.add(new Vector(10,6));
//        obstacle.add(new Vector(10,7));
//        obstacle.add(new Vector(10,8));
//        obstacle.add(new Vector(10,9));
//        obstacle.add(new Vector(10,10));
//        obstacle.add(new Vector(13,2));
//        obstacle.add(new Vector(13,3));
//        obstacle.add(new Vector(13,4));
//        obstacle.add(new Vector(13,5));
//        obstacle.add(new Vector(14,11));
//        obstacle.add(new Vector(14,12));
//        obstacle.add(new Vector(14,13));
//        obstacle.add(new Vector(17,7));
//        realMap=new Map(obstacle);
		robot=new Robot();
		realMap=new Map(PointState.IsFree);
		eventHandler=new EventHandler(this);
        robotMap=robot.getMap();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 657, 663);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(10, 11, 617, 478);
		contentPane.add(panel);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 500, 617, 102);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		

        gridPanel=new GridPanel(realMap,robot);
        panel.add(gridPanel);
        
        JCheckBox isSimulation = new JCheckBox("Simulation");
        isSimulation.setBounds(25, 68, 90, 23);
        panel_1.add(isSimulation);

		JButton btnStartExploring = new JButton("Start Exploring");
		btnStartExploring.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				eventHandler.startExploration(
						robot, realMap,timeLimitText.getText(),covLimitText.getText(),speedText.getText(), isSimulation.isSelected());
			}
		});
		btnStartExploring.setBounds(115, 68, 120, 23);
		panel_1.add(btnStartExploring);
		
		JButton btnShortestPath = new JButton("Shortest Path");
		btnShortestPath.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0){
				eventHandler.shortestPath(robot, realMap, timeLimitText.getText(),speedText.getText(), isSimulation.isSelected());
			}
		});
		btnShortestPath.setBounds(235, 68, 120, 23);
		panel_1.add(btnShortestPath);
		
		JButton btnRestartRobot = new JButton("Restart Robot");
		btnRestartRobot.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0){
				robot.restart();
                gridPanel.getGridContainer().drawGrid(realMap, robot);
			}
		});
		btnRestartRobot.setBounds(355, 68, 120, 23);
		panel_1.add(btnRestartRobot);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0){
				eventHandler.exit();
			}
		});
		btnExit.setBounds(475, 68, 120, 23);
		panel_1.add(btnExit);
		
		textField = new JTextField();
		textField.setBounds(25, 27, 120, 20);
		panel_1.add(textField);
		textField.setColumns(10);
		
		JButton btnLoadMap = new JButton("Load Map");
        btnLoadMap.addMouseListener(new MouseAdapter(){
        	@Override
        	public void mouseClicked(MouseEvent arg0){
                 realMap=eventHandler.loadMap(textField.getText(), realMap);
                 gridPanel.getGridContainer().drawGrid(realMap, robot);
            }
        });
		btnLoadMap.setBounds(150, 26, 90, 23);
		panel_1.add(btnLoadMap);
		
        JButton btnSaveMap = new JButton("Save Map");
        btnSaveMap.addMouseListener(new MouseAdapter(){
        	@Override
        	public void mouseClicked(MouseEvent arg0){
        		robotMap=robot.getMap();
        		eventHandler.saveMap(textField.getText(), !explored?realMap:robotMap);
        	}
        });
		btnSaveMap.setBounds(245, 26, 90, 23);
		panel_1.add(btnSaveMap);
                
		JLabel lblTimeLimit = new JLabel("Time Limit");
		lblTimeLimit.setBounds(360, 11, 60, 14);
		panel_1.add(lblTimeLimit);
		
		JLabel lblCoverageLimit = new JLabel("Coverage Limit");
		lblCoverageLimit.setBounds(440, 11, 90, 14);
		panel_1.add(lblCoverageLimit);
		
		JLabel lblSpeed = new JLabel("Speed");
		lblSpeed.setBounds(550, 11, 60, 14);
		panel_1.add(lblSpeed);
		
		timeLimitText = new JTextField();
		timeLimitText.setBounds(370, 27, 46, 20);
		panel_1.add(timeLimitText);
		timeLimitText.setColumns(10);
		
		covLimitText = new JTextField();
		covLimitText.setBounds(460, 27, 46, 20);
		panel_1.add(covLimitText);
		covLimitText.setColumns(10);
		
		speedText = new JTextField();
		speedText.setBounds(550, 27, 46, 20);
		panel_1.add(speedText);
		speedText.setColumns(10);
		
	}
        
        public GridPanel getGridPanel(){
            return gridPanel;
        }
}
