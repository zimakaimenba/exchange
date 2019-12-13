package com.exchangeinfomanager.StockCalendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;

import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.tongdaxinreport.TDXFormatedOpt;

public class WholeMonthCompViewOfZSBL extends WholeMonthNewsComponentsView 
{

	private JPopupMenu Pmenu;
	private JMenuItem menuItemEdit;
	private JLabel createMilestoneDateForZhiShu;

	public WholeMonthCompViewOfZSBL(ServicesForNews meetingServices, String title) 
	{
		super(meetingServices, title);
		// TODO Auto-generated constructor stub
		this.createMilestoneDateForZhiShu = JLabelFactory.createButton("导出指数关键日期");
		super.pnmmonthnresnorth.add(createMilestoneDateForZhiShu);
		
		createMilestoneDateForZhiShu.addMouseListener( new CreateDapanMileStoneDateController () );
	}
	
	 private class CreateDapanMileStoneDateController extends MouseAdapter 
	 {

			@Override
	        public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getButton() == MouseEvent.BUTTON1) {
					TDXFormatedOpt.parserZhiShuGuanJianRiQiToTDXCode();
	            } else if (e.getButton() == MouseEvent.BUTTON3) {
	            
	            }
	        }
	  }


}
