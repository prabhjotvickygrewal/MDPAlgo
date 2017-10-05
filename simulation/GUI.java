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
	private GridPanel gridPanel;
	private Robot robot;
	private Map realMap;
        private Map robotMap;
	public static final int BLOCK_SIZE=30;
	public static final int GAP=1;
        private static EventHandler eventHandler;
        public static boolean isExploring=false;
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
//        obstacle.add(new Vector(14,4));
//        obstacle.add(new Vector(15,4));
//        obstacle.add(new Vector(16,4));
//        obstacle.add(new Vector(17,4));
//        obstacle.add(new Vector(18,4));
//        obstacle.add(new Vector(14,5));
//        obstacle.add(new Vector(14,6));
//        obstacle.add(new Vector(14,7));
//        obstacle.add(new Vector(14,3));
//        obstacle.add(new Vector(14,8));
//        obstacle.add(new Vector(10,5));
//        obstacle.add(new Vector(10,6));
//        obstacle.add(new Vector(10,7));
//        obstacle.add(new Vector(10,3));
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
        isSimulation.setBounds(30, 68, 90, 23);
        panel_1.add(isSimulation);

		JButton btnStartExploring = new JButton("Start Exploring");
		btnStartExploring.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
                            eventHandler.startExploration(robot, realMap,timeLimitText.getText(),covLimitText.getText(),isSimulation.isSelected());
                            isExploring=true;
			}
		});
		btnStartExploring.setBounds(120, 68, 120, 23);
		panel_1.add(btnStartExploring);
		
		JButton btnShortestPath = new JButton("Shortest Path");
		btnShortestPath.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0){
				eventHandler.shortestPath(robot);
			}
		});
		btnShortestPath.setBounds(240, 68, 120, 23);
		panel_1.add(btnShortestPath);
		
		JButton btnRestartRobot = new JButton("Restart Robot");
		btnRestartRobot.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0){
				robot.restart();
                gridPanel.getGridContainer().drawGrid(realMap, robot);
			}
		});
		btnRestartRobot.setBounds(360, 68, 120, 23);
		panel_1.add(btnRestartRobot);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0){
				eventHandler.exit();
			}
		});
		btnExit.setBounds(480, 68, 120, 23);
		panel_1.add(btnExit);
		
		textField = new JTextField();
		textField.setBounds(50, 27, 120, 20);
		panel_1.add(textField);
		textField.setColumns(10);
		
		JButton btnLoadMap = new JButton("Load Map");
        btnLoadMap.addMouseListener(new MouseAdapter(){
        	@Override
        	public void mouseClicked(MouseEvent arg0){
        		isExploring=true;
                 realMap=eventHandler.loadMap(textField.getText(), realMap);
                 gridPanel.getGridContainer().drawGrid(realMap, robot);
            }
        });
		btnLoadMap.setBounds(180, 26, 89, 23);
		panel_1.add(btnLoadMap);
		
        JButton btnSaveMap = new JButton("Save Map");
        btnSaveMap.addMouseListener(new MouseAdapter(){
        	@Override
        	public void mouseClicked(MouseEvent arg0){
        		robotMap=robot.getMap();
        		eventHandler.saveMap(textField.getText(), robotMap);
        	}
        });
		btnSaveMap.setBounds(280, 26, 89, 23);
		panel_1.add(btnSaveMap);
                
		JLabel lblTimeLimit = new JLabel("Time Limit");
		lblTimeLimit.setBounds(408, 11, 60, 14);
		panel_1.add(lblTimeLimit);
		
		JLabel lblCoverageLimit = new JLabel("Coverage Limit");
		lblCoverageLimit.setBounds(496, 11, 89, 14);
		panel_1.add(lblCoverageLimit);
		
		timeLimitText = new JTextField();
		timeLimitText.setBounds(408, 27, 46, 20);
		panel_1.add(timeLimitText);
		timeLimitText.setColumns(10);
		
		covLimitText = new JTextField();
		covLimitText.setBounds(506, 27, 46, 20);
		panel_1.add(covLimitText);
		covLimitText.setColumns(10);
	}
        
        public GridPanel getGridPanel(){
            return gridPanel;
        }
}
