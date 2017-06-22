package com.exchangeinfomanager.gui.subgui;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.JPanel;

public class PaoMaDeng2 extends JPanel 
{
	  private String message = "";
	  private int xCoordinate = 50;
	  private int yCoordinate = 20;

	/**
	 * Create the panel.
	 */
	public PaoMaDeng2() 
	{
		 //this.message = message;

//		 Timer timer = new Timer(100, new TimerListener());
//		 timer.start();
	}
	public void refreshMessage (String message)
	{
		if(message !=null)
			this.message = message;
		else message = "";
		
		Timer timer = new Timer(100, new TimerListener());
		 timer.start();
		
	}
	
	 public void paintComponent(Graphics g) 
	 {
		   super.paintComponent(g);

//		   if (xCoordinate > getWidth()) {
//		    xCoordinate = -100;
//		   }
		   int strlength = message.length();
		   
		   if (xCoordinate < -4*strlength) {
			    xCoordinate = +getWidth();
		   }

		   xCoordinate -= 3;
		   g.drawString(message, xCoordinate, yCoordinate);
		   //System.out.println(xCoordinate);
	  }
	 
	 public void paintComponent1(Graphics g) 
	 {
		 String[] msg = {"test 1","test 2","test 3                     test3"};
		   super.paintComponent(g);

//		   if (xCoordinate > getWidth()) {
//		    xCoordinate = -100;
//		   }
//		   if (xCoordinate < 0) {
//			    xCoordinate = +getWidth();
//		   }

		   //xCoordinate -= 5;
		   for(int i=0;i<msg.length;i++) {
		    g.drawString(msg[i], xCoordinate, yCoordinate);
		    System.out.println("1x"+xCoordinate);
		    try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		    g.drawString("                             ", xCoordinate, yCoordinate);
		    
		    System.out.println("2x"+xCoordinate);
		   }
	  }
	 
	 class TimerListener implements ActionListener {
		   public void actionPerformed(ActionEvent e) {
		    repaint();
		   }
	 }

}
