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
import java.util.ArrayList;
import javax.swing.*;

public class PanelAtc extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	
	private Thread animator;
	private Graphics dbg;
	private Image dbImage = null;
	private ATC atc;
	
	//Constantes para la medida de la pantalla
	private static final int PWIDTH = 1200;
	private static final int PHEIGHT = 1000;
	
	public PanelAtc(boolean danger) {
		atc = new ATC();
		// NE planes - 70
		atc.addPlane(new Plane("SLI145", "E190", 950.0, 0.0, 22000.0, 110, 32.0, atc));
		atc.addPlane(new Plane("IJ2240", "A320", 1093.65, -394.67, 22000.0, 110, 32.0, atc));
		atc.addPlane(new Plane("AM2", "B789", 1360.42, -1127.63, 22000.0, 110, 32.0, atc));
		// S planes - 20
		atc.addPlane(new Plane("AM2463", "B738", 790.0, 1000.0, 28000.0, 250, 36.0, atc));
		atc.addPlane(new Plane("AFR178", "A388", 961.01, 1469.85, 28000.0, 250, 36.0, atc));
		atc.addPlane(new Plane("AM90", "B788", 1268.83, 2315.57, 28000.0, 250, 36.0, atc));
		atc.addPlane(new Plane("VOI705", "A320", 1371.43, 2597.48, 28000.0, 250, 36.0, atc));
		// NW planes - 25
		atc.addPlane(new Plane("VOI667", "A321", 200.0, 0.0, 20000.0, 65, 30.0, atc));
		atc.addPlane(new Plane("ACA996", "A319", 94.35, -226.58, 20000.0, 65, 30.0, atc));
		atc.addPlane(new Plane("VIV3307", "A320", -32.44, -498.47, 20000.0, 65, 30.0, atc));
		atc.addPlane(new Plane("AM189", "B737", -526.9, -1558.85, 20000.0, 65, 30.0, atc));
		
		if (danger) {
			atc.addPlane(new Plane("Dangerous", "C300", 1080.72, 1798.74, 28000.0, 250, 36.0, atc));
		}
		
		setBackground(Color.white);
		setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
		setFocusable(true);
		requestFocus();
		readyForTermination();
	}
	
	public PanelAtc(ArrayList<Plane> planes) {
		atc = new ATC();
		
		for (Plane p: planes) {
			p.setATC(atc);
			atc.addPlane(p);
		}
		
		setBackground(Color.white);
		setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
		setFocusable(true);
		requestFocus();
		readyForTermination();
	}
	
	public void addNotify()
	{
		super.addNotify();
		start();
	}
	
	private void start()
	{
		if (animator == null){
			animator = new Thread(this);
			animator.start();
			atc.start();
		}
	}
	
	public void run(){
		while(true){
			render();
			paintScreen();
			try{
				Thread.sleep(1000/60);
			}catch(InterruptedException ex){}
		}
	}
	
	private void render(){
		if(dbImage == null){
			dbImage = createImage(PWIDTH,PHEIGHT);
			if(dbImage == null){
				System.out.println("dbImage is null");
				return;
			}else{
				dbg = dbImage.getGraphics();
			}
		}
		dbg.setColor(Color.white);
		dbg.fillRect(0,0,PWIDTH,PHEIGHT);
		atc.render(dbg);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		if(dbImage != null)
			g.drawImage(dbImage, 0, 0, null);
	}
	
	private void readyForTermination() {
		
	}

	private void paintScreen(){
		Graphics g;
		try{
			g = this.getGraphics();
			if((g != null) && (dbImage != null))
				g.drawImage(dbImage,0,0,null);
			Toolkit.getDefaultToolkit().sync();
		} catch(Exception e) {
			System.out.println("Graphics context error: " + e);
		}
	}
}