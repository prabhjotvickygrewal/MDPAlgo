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

import java.awt.Color;

import robot.*;
import map.*;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private GridPanel gridPanel;
	private Robot robot;
	private Map map;
	public static final int BLOCK_SIZE=30;
	public static final int GAP=1;
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
		robot=new Robot();
		map=new Map(PointState.IsFree);
		
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
		
		gridPanel=new GridPanel(map,robot);
		panel.add(gridPanel);
		
		JButton btnStartExploring = new JButton("Start Exploring");
		btnStartExploring.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		btnStartExploring.setBounds(82, 68, 113, 23);
		panel_1.add(btnStartExploring);
		
		JButton btnShortestPath = new JButton("Shortest Path");
		btnShortestPath.setBounds(274, 68, 113, 23);
		panel_1.add(btnShortestPath);
		
		JButton btnExit = new JButton("Exit");
		btnExit.setBounds(473, 68, 89, 23);
		panel_1.add(btnExit);
		
		textField = new JTextField();
		textField.setBounds(82, 27, 189, 20);
		panel_1.add(textField);
		textField.setColumns(10);
		
		JButton btnImportMap = new JButton("Import Map");
		btnImportMap.setBounds(283, 26, 89, 23);
		panel_1.add(btnImportMap);
		
		JLabel lblTimeLimit = new JLabel("Time Limit");
		lblTimeLimit.setBounds(408, 11, 46, 14);
		panel_1.add(lblTimeLimit);
		
		JLabel lblCoverageLimit = new JLabel("Coverage Limit");
		lblCoverageLimit.setBounds(496, 11, 89, 14);
		panel_1.add(lblCoverageLimit);
		
		textField_1 = new JTextField();
		textField_1.setBounds(408, 27, 46, 20);
		panel_1.add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(506, 27, 46, 20);
		panel_1.add(textField_2);
		textField_2.setColumns(10);
	}
}
