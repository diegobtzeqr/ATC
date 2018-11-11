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

public class Plane implements Runnable {
	private String callsign;
	private String model;
	private double x;
	private double y;
	private double altitude;
	private double speed;
	private double speedX;
	private double speedY;
	private double speedZ;
	private int heading;
	private int finalHeading;
	private char direction;
	private double finalAltitude;
	private double speedRate;
	private double finalSpeed;
	private ATC atc;
	private boolean landing;
	private boolean danger;
	private boolean gate;
	
	public Plane(String cs, String mdl, double xCoord, double yCoord, double zCoord, int hdng, double spd, ATC at) {
		callsign = cs;
		model = mdl;
		x = xCoord;
		y = yCoord;
		altitude = zCoord;
		speed = spd / 3.6;
		speedX = speed * Math.cos(hdng);
		speedY = speed * Math.sin(hdng);
		speedZ = 0;
		heading = hdng;
		finalHeading = hdng;
		direction = 'R';
		finalAltitude = zCoord;
		speedRate = 0;
		finalSpeed = speed;
		atc = at;
		landing = false;
		danger = false;
		gate = false;
	}
	
	public String getCallsign() {
		return callsign;
	}
	
	private void move() {
		x += speedX;
		y += speedY;
		altitude += speedZ;
	}
	
	public void setGoalHeading(int finalHead, char dir) {
		this.finalHeading = finalHead;
		this.direction = dir;
		switch (dir) {
			case 'R':
				if (finalHeading < heading) {
					finalHeading += 360;
				}
				break;
			case 'L':
				if (finalHeading > heading) {
					finalHeading -= 360;
				}
				break;
		}
	}
	
	private void headingChange() {
		if (finalHeading != heading) {
			switch (direction) {
				case 'R':
					if (finalHeading < (heading + 5)) {
						heading = finalHeading;
					} else {
						heading += 5;
					}
					break;
				case 'L':
					if (finalHeading > (heading - 5)) {
						heading = finalHeading;
					} else {
						heading -= 5;
					}
					break;
			}
		} else if (finalHeading < 0) {
			finalHeading += 360;
			heading = finalHeading;
		} else {
			heading = finalHeading % 360;
			finalHeading = heading;
		}
		
		speedX = speed * Math.cos(Math.toRadians(heading));
		speedY = speed * Math.sin(Math.toRadians(heading));
	}
	
	public void setAltitudeChange(double finalAlt, double dist) {
		this.finalAltitude = finalAlt;
		this.speedZ = - ((altitude - finalAltitude) / (dist / speed));
	}
	
	private void altitudeChange() {
		if (altitude >= finalAltitude - 75 && altitude <= finalAltitude + 75) {
			speedZ = 0;
		}
	}
	
	public void setSpeedGoal(double spdGoal, double dist) {
		this.finalSpeed = spdGoal / 3.6;
		this.speedRate = - ((speed - finalSpeed) / (dist / speed));
	}
	
	public void speedChange() {
		if (!(speed >= finalSpeed - 0.015 && speed <= finalSpeed + 0.015)) {
			speed += speedRate;
		}
	}
	
	public void land() {
		setSpeedGoal(0, 95);
		speedZ = 0;
		altitude = 0;
		landing = true;
	}
	
	private boolean hasLanded() {
		if (speed <= 0.8) {
			speed = 0;
			return true;
		}
		return false;
	}
	
	public void debug() {
		System.out.println(model + "/" + callsign + " - Altitude: " + altitude + ". Speed: " + speed * 3.6 + ". X: " + x + ". Y: " + y + ". Heading: " + heading);
	}
	
	public void run() {
		while (!Thread.interrupted()) {
			headingChange();
			altitudeChange();
			speedChange();
			move();
			
			if (landing) {
				if (hasLanded()) {
					gate = true;
					break;
				}
			} else {
				atc.control(this);
				atc.checkSafety();
			}
			
			try {
	            Thread.sleep(35);
	        } catch (InterruptedException e) {
	            
	        }
		}
	}
	
	public void render(Graphics g) {
		if (!gate) {
			if (danger) {
				g.setColor(Color.RED);
				g.drawString("DANGER", (int) x - 15, (int) - + 15);
			} else {
				g.setColor(new Color(0, 255 - (((int) altitude * 2 / 255) % 256), 255));
			}
			
			g.fillRect((int) x, (int) y, 10, 10);
			g.setColor(Color.WHITE);
			g.setFont(new Font("TimesRoman", Font.BOLD, 13));
			g.drawString(callsign, (int) x + 15, (int) y + 15);
		}
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public double getAltitude() {
		return altitude;
	}
	
	public double getSpeed() {
		return speed * 3.6;
	}
	
	public void setDangerTrue() {
		danger = true;
	}
	
	public void setATC(ATC at) {
		this.atc = at;
	}
}
