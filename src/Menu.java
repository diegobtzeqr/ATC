/*  ATC is a simulator of Mexico City's Local Air Traffic Controller.
    Copyright (C) 2018 Diego Betanzos Esquer
    
    This file is part of ATC.

    ATC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ATC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*; 

public class Menu extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel callsign, planeType, distance, welcome, instructions1, instructions2, addPlaneLabel, addInstructions, entryPointLabel, numberPlanes;
	private JTextField callsignTxt, planeTypeTxt, distanceTxt; 
	private JButton addPlane, testSafe, testDanger, simulate, exit;
	private String cs, pt, ep;
	private String[] entryPointNames = {"South", "NorthEast", "NorthWest"};
	private JComboBox<Object> entryPoints;
	private double dist;
	private ArrayList<Plane> planes;
	
	public Menu(){
		super();
		this.setLayout(new BorderLayout());
		callsign = new JLabel ("Callsign: ", SwingConstants.RIGHT);
		planeType = new JLabel ("Plane Type: ", SwingConstants.RIGHT);
		distance = new JLabel ("Distance from entry point in meters: ", SwingConstants.RIGHT);
		callsignTxt = new JTextField(15);
		planeTypeTxt = new JTextField(15);
		distanceTxt = new JTextField(15);
		welcome = new JLabel("Mexico City's local ATC simulator");
		instructions1 = new JLabel("Intructions: Add as many planes as you'd like to simulate and then click on the \"Simulate\" button.");
		instructions2 = new JLabel("In case you are just testing the app, you can run one of the predefined tests by clicking on the corresponding button.");
		addPlaneLabel = new JLabel("Add a new plane: ", SwingConstants.RIGHT);
		addInstructions = new JLabel("Input data of new planes: ", SwingConstants.CENTER);
		entryPointLabel = new JLabel("Entry Point: ", SwingConstants.RIGHT);
		addPlane = new JButton("Add Plane");
		simulate = new JButton("Simulate");
		testSafe = new JButton("Safe Test");
		exit = new JButton("Exit");
		testDanger = new JButton("Danger Test");
		welcome.setFont(new Font("TimesRoman", Font.BOLD, 20));
		instructions1.setFont(new Font("TimesRoman", Font.PLAIN, 16));
		instructions2.setFont(new Font("TimesRoman", Font.PLAIN, 16));
		addInstructions.setFont(new Font("TimesRoman", Font.BOLD, 16));
		entryPoints = new JComboBox<Object>(entryPointNames);
		entryPoints.setSelectedIndex(0);
		planes = new ArrayList<>();
		numberPlanes = new JLabel("Number of planes in simulation: " + planes.size() + " ", SwingConstants.RIGHT);
		
		Panel p = new Panel();
		p.setLayout(new GridLayout(1,2));
		p.add(welcome);
		p.add(exit);
		
		Panel p1 = new Panel();
		p1.setLayout(new GridLayout(5,2));
		p1.add(entryPointLabel);
		p1.add(entryPoints);
		p1.add(distance);
		p1.add(distanceTxt);
		p1.add(callsign);
		p1.add(callsignTxt);
		p1.add(planeType);
		p1.add(planeTypeTxt);
		p1.add(addPlaneLabel);
		p1.add(addPlane);
		
		Panel p2 = new Panel();
		p2.setLayout(new GridLayout(5,1));
		p2.add(p);
		p2.add(instructions1);
		p2.add(instructions2);
		
		Panel p3 = new Panel();
		p3.setLayout(new GridLayout(1,2));
		p3.add(testSafe);
		p3.add(testDanger);
		
		p2.add(p3);
		p2.add(addInstructions);
		
		Panel p4 = new Panel();
		p4.setLayout(new GridLayout(1,2));
		p4.add(numberPlanes);
		p4.add(simulate);
		
		this.add(p1, BorderLayout.CENTER);
		this.add(p2, BorderLayout.NORTH);
		this.add(p4, BorderLayout.SOUTH);
		simulate.addActionListener(this);
		addPlane.addActionListener(this);
		testSafe.addActionListener(this);
		testDanger.addActionListener(this);
		entryPoints.addActionListener(this);
		exit.addActionListener(this);
		this.setSize(1200, 1000);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(true);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource().equals(addPlane)) {
			cs = callsignTxt.getText();
			pt = planeTypeTxt.getText();
			dist = Double.parseDouble(distanceTxt.getText()) / 10.0;
			ep = (String) entryPoints.getSelectedItem();
			
			switch(ep) {
			case "South":
				planes.add(new Plane(cs, pt, 790.0 + dist * Math.sin(Math.toRadians(20)), 1000.0 + dist * Math.cos(Math.toRadians(20)), 28000.0, 250, 36.0, null));
				break;
			case "NorthEast":
				planes.add(new Plane(cs, pt, 950.0 + dist * Math.cos(Math.toRadians(70)), - dist * Math.sin(Math.toRadians(70)), 22000.0, 110, 32.0, null));
				break;
			case "NorthWest":
				planes.add(new Plane(cs, pt, 200.0 - dist * Math.sin(Math.toRadians(25)), - dist * Math.cos(Math.toRadians(25)), 20000.0, 65, 30.0, null));
				break;
			}
			
			entryPoints.setSelectedIndex(0);
			callsignTxt.setText("");
			planeTypeTxt.setText("");
			distanceTxt.setText("");
			numberPlanes.setText("Number of planes in simulation: " + planes.size() + " ");
		}
		
		if (ae.getSource().equals(simulate)) {
			this.setVisible(false);
			JFrame app = new JFrame("ATC");
		    app.getContentPane().add(new PanelAtc(planes), BorderLayout.CENTER);
		    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    app.setUndecorated(true);
		    app.pack();
		    app.setLocationRelativeTo(null);
		    app.setResizable(false);  
		    app.setVisible(true);
		}
		
		if (ae.getSource().equals(testSafe)) {
			this.setVisible(false);
			JFrame app = new JFrame("ATC");
		    app.getContentPane().add(new PanelAtc(false), BorderLayout.CENTER);
		    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    app.setUndecorated(true);
		    app.pack();
		    app.setLocationRelativeTo(null);
		    app.setResizable(false);  
		    app.setVisible(true);
		}
		
		if (ae.getSource().equals(testDanger)) {
			this.setVisible(false);
			JFrame app = new JFrame("ATC");
		    app.getContentPane().add(new PanelAtc(true), BorderLayout.CENTER);
		    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    app.setUndecorated(true);
		    app.pack();
		    app.setLocationRelativeTo(null);
		    app.setResizable(false);  
		    app.setVisible(true);
		}
		
		if (ae.getSource().equals(exit)) {
			System.exit(0);
		}
	}
	
	public static void main(String args[]) {
		@SuppressWarnings("unused")
		Menu m = new Menu();
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		String option;
		
		do {
			System.out.println("    ATC  Copyright (C) 2018 Diego Betanzos Esquer\r\n" + 
					"    ATC comes with ABSOLUTELY NO WARRANTY; for details type `show w'.\r\n" + 
					"    This is free software, and you are welcome to redistribute it\r\n" + 
					"    under certain conditions; type `show c' for details.");
			System.out.print("\n    Option: ");
			option = sc.nextLine();
			
			System.out.println("");
			
			switch(option) {
				case "show w":
					System.out.println("    ATC is distributed in the hope that it will be useful,\r\n" + 
							"    but WITHOUT ANY WARRANTY; without even the implied warranty of\r\n" + 
							"    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\r\n" + 
							"    GNU General Public License for more details.\n");
					break;
				case "show c":
					System.out.println("    ATC is free software: you can redistribute it and/or modify\r\n" + 
							"    it under the terms of the GNU General Public License as published by\r\n" + 
							"    the Free Software Foundation, either version 3 of the License, or\r\n" + 
							"    (at your option) any later version.\n");
					break;
				default:
					System.out.println("Invalid Option.\n");
					break;
			}
		} while (true);
	}
}