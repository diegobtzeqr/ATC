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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;

public class ATC {
	// Required attributes.
	private ArrayList<Plane> planes;
	private ExecutorService executor;
	private boolean danger;
	protected BufferedImage background;
	
	// ATC constructor
	public ATC() {
		planes = new ArrayList<>();
		executor = Executors.newCachedThreadPool();
		danger = false;
		
		// Get background image.
		try {
			background = ImageIO.read(new File("MexicoCity.jpg"));
		} catch (IOException e) {
			System.out.println("Couldn't load background image.");
		}
	}
	
	// Add plane to ArrayList.
	public void addPlane (Plane plane) {
		planes.add(plane);
	}
	
	// Remove plane from ArrayList
	public void removePlane (Plane plane) {
		planes.remove(plane);
	}
	
	// Get all the planes.
	public ArrayList<Plane> getPlanes() {
		return planes;
	}
	
	// Start Thread executor, execute each plane as a independent thread, shutdown executor when it finishes
	// executing all the planes.
	public void start() {
		try {
			for (Plane p : planes) {
                executor.execute(p);                
            }
        } catch(Exception err) {
            
        }
		
		executor.shutdown();
	}
	
	// See if there's any possible conflict caused by planes close to each other.
	public void checkSafety() {
		double temp;
		
		// Compare plane's distance.
		for (Plane p1: planes) {
			for (Plane p2: planes) {
				// Compare only if none of them is landing and if vertical separation is too small.
				if (p2 != p1 && p1.getSpeed() >= 16 && p2.getSpeed() >= 16 && Math.abs((p1.getAltitude() - p2.getAltitude())) < 2500) {
					// Calculate distance from each other.
					temp = Math.sqrt(Math.pow((p1.getX() - p2.getX()), 2) + Math.pow((p1.getY() - p2.getY()), 2));
					
					// In case they are in risk, set danger boolean variable to true in both planes and ATC.
					if (temp <= p1.getSpeed() * 4.9) {
						p1.setDangerTrue();
						p2.setDangerTrue();
						danger = true;
					}
				}
			}
		}
	}
	
	// Tell planes what to do based on their location.
	// Depending on the location, altitude, speed and heading may or may not be modified.
	public void control(Plane p) {
		// Planes arriving by South entrance.
		if (p.getX() >= 770 && p.getX() <= 810 && p.getY() >= 980 && p.getY() <= 1020) {
			p.setAltitudeChange(20000, 970);
			p.setSpeedGoal(30, 970);
		}
		  // Planes arriving by Northwest entrance.
		  else if (p.getX() >= 145 && p.getX() <= 175 && p.getY() >= -10 && p.getY() <= 15) {
			p.setAltitudeChange(18000, 210);
			p.setSpeedGoal(28, 210);
		} 
		  // Planes arriving by Northeast entrance.
		  else if (p.getX() >= 915 && p.getX() <= 945 && p.getY() >= 35 && p.getY() <= 65) {
			p.setAltitudeChange(20000, 498);
			p.setSpeedGoal(30, 498);
			p.setGoalHeading(145, 'R');
		} else if (p.getX() >= 635 && p.getX() <= 665 && p.getY() >= 260 && p.getY() <= 290) {
			p.setGoalHeading(250, 'R');
		} 
		  // Common landing path for all planes.
	      else if (p.getX() >= 425 && p.getX() <= 455 && p.getY() >= 55 && p.getY() <= 85) {
			p.setAltitudeChange(18000, 195);
			p.setSpeedGoal(28, 195);
			p.setGoalHeading(65, 'L');
		} else if (p.getX() >= 275 && p.getX() <= 305 && p.getY() >= 150 && p.getY() <= 180) {
			p.setAltitudeChange(13000, 235);
			p.setSpeedGoal(25, 235);
		} else if (p.getX() >= 375 && p.getX() <= 405 && p.getY() >= 360 && p.getY() <= 390) {
			p.setAltitudeChange(11625, 210);
			p.setSpeedGoal(22, 210);
			p.setGoalHeading(130, 'R');
		} else if (p.getX() >= 270 && p.getX() <= 290 && p.getY() >= 540 && p.getY() <= 560) {
			p.setAltitudeChange(10250, 225);
			p.setSpeedGoal(19, 225);
			p.setGoalHeading(90, 'L');
		} else if (p.getX() >= 265 && p.getX() <= 295 && p.getY() >= 770 && p.getY() <= 790) {
			p.setAltitudeChange(7300, 333);
			p.setSpeedGoal(16, 333);
			p.setGoalHeading(330, 'L');
		} 
		  // Land the planes.
		  else if (p.getX() >= 595 && p.getX() <= 605 && p.getY() >= 680 && p.getY() <= 695) {
			p.land();
		}
	}
	
	// Display elements.
	public void render(Graphics g) {
		g.drawImage(background, 0, 0, null);
		for (Plane p : planes) {
			p.render(g);
		}
		
		// If planes are in danger display warning message.
		if (danger) {
			g.setFont(new Font("TimesRoman", Font.BOLD, 20));
			g.setColor(Color.WHITE);
			g.fillRect(890, 480, 282, 32);
			g.setColor(Color.RED);
			g.drawString("PLANES ARE IN DANGER!!!", 900, 500);
		}
	}
}