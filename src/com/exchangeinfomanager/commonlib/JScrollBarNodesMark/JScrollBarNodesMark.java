package com.exchangeinfomanager.commonlib.JScrollBarNodesMark;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.metal.MetalScrollBarUI;

import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiandGeGuTableBasic;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class JScrollBarNodesMark  extends JScrollBar
{
	public JScrollBarNodesMark (BanKuaiandGeGuTableBasic jtable)
	{
		super(JScrollBar.VERTICAL);
		this.setUnitIncrement(10);
		
		this.setUI(new MetalScrollBarUI() { //https://stackoverflow.com/questions/14176848/java-coloured-scroll-bar-search-result
	    	//https://java-swing-tips.blogspot.com/search/label/Matcher?m=0
	      @Override protected void paintTrack(
	            Graphics g, JComponent c, Rectangle trackBounds) {
	        super.paintTrack(g, c, trackBounds);
//	        Rectangle rect = tabbedPanegegu.getBounds();
//	        double sy = trackBounds.getHeight() / rect.getHeight();
//	        AffineTransform at = AffineTransform.getScaleInstance(1.0, sy);
	        g.setColor(Color.BLUE.darker());
	        
	        BanKuaiGeGuBasicTableModel tableTempGeGuModel = ((BanKuaiGeGuBasicTableModel)jtable.getModel()); 
	        BanKuai interbk = tableTempGeGuModel.getInterSetctionBanKuai();
		    if(interbk!= null) {
		    	List<BkChanYeLianTreeNode> allstks = tableTempGeGuModel.getAllNodes();
		    	int rownum = allstks.size();
		    	for(BkChanYeLianTreeNode tmpnode : allstks) {
		    		if( interbk.getBanKuaiGeGu (tmpnode.getMyOwnCode())  != null) {
		    			Integer rowindex = tableTempGeGuModel.getNodeRowIndex(tmpnode.getMyOwnCode() );
						if( rowindex  != null && rowindex >=0 ) {
							int modelRow = jtable.convertRowIndexToView(rowindex);
//							int width = tableTempGeGu.getCellRect(modelRow, 0, true).width;
//							int height = tableTempGeGu.getCellRect(modelRow, 0, true).height;
//						    Rectangle r = new Rectangle(width, height * modelRow);
//						    Rectangle s = at.createTransformedShape(r).getBounds();
						    int h = 2; //Math.max(2, s.height-2);
						    int zuobiaoy = (trackBounds.height* modelRow)/rownum ;
					        g.fillRect(trackBounds.x+2, zuobiaoy , trackBounds.width, h);
						}
		    		}
		    	}
		    }
	      }
	    });
	}
}
