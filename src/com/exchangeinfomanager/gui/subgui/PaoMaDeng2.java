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
	  private String[] messages ;
	  private int xCoordinate = 5;
	  private int yCoordinate = 20;
	  private int msgindex =0;
	  private Timer timer;
	  private Boolean rollingstatus = false;

	/**
	 * Create the panel.
	 */
	public PaoMaDeng2() 
	{
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(rollingstatus) {
					rollingstatus = false;
					timer.stop();
				} else {
					rollingstatus = true;
					timer.start();
				}
			}
		});
	}
	public void refreshMessage (String message)
	{
		if(message !=null)
			this.message = message;
		else message = "";
		
		this.rollingstatus = true;
		timer = new Timer(100, new TimerListener());
		timer.start();
	}
	
	public void refreshMessage (String[] messages)
	{
		if(message !=null) {
			this.messages = messages;
			this.message = null;
		}
		
		this.rollingstatus = true;
		timer = new Timer(100, new TimerListener());
		timer.start();
	}
	 public void paintComponent(Graphics g) 
	 {
		   super.paintComponent(g);
		   
		    if(this.message != null) {
			   
			   int strlength = message.length();
			   
			   if (xCoordinate < -4*strlength) {
				    xCoordinate = +getWidth();
			   }

			   xCoordinate -= 3;
			   
			   g.drawString(message, xCoordinate, yCoordinate);
		   }
		   else {
			   g.drawString(messages[msgindex%messages.length], xCoordinate, yCoordinate);
			   msgindex ++;
			   try {
					Thread.sleep(900);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   };
		   
	  }
	 
	 class TimerListener implements ActionListener {
		   public void actionPerformed(ActionEvent e) {
		    repaint();
		   }
	 }

}
