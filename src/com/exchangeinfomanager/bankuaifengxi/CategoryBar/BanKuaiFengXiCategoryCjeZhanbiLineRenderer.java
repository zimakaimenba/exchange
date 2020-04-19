package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Shape;
import java.time.LocalDate;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.util.ShapeUtilities;
import com.exchangeinfomanager.nodes.stocknodexdata.TDXNodesXPeriodExternalData;

import com.exchangeinfomanager.commonlib.CommonUtility;

public class BanKuaiFengXiCategoryCjeZhanbiLineRenderer extends BanKuaiFengXiCategoryLineRenderer
{
	private int settingclusterid = -1;
	
	public BanKuaiFengXiCategoryCjeZhanbiLineRenderer ()
	{
		super ();
	}
	
	@Override
    public Shape getItemShape(int row, int col) 
	{
		if(settingclusterid == -1) {
			if ( col == shouldcolumn) {
//	            return ShapeUtilities.createDiagonalCross(5, 2);
	            return ShapeUtilities.createUpTriangle(5);
	        } else
	        if ( col == shouldcolumnlast) {
//	       	 return super.getItemShape(row, col);
	       	 return ShapeUtilities.createDownTriangle(3);
	        } else  {
	    		java.awt.geom.Ellipse2D.Double shape = new java.awt.geom.Ellipse2D.Double(-0.5, -0.5, 0.5, 0.5);
	            return shape;
	        }
		} else {
			CategoryPlot plot = getPlot ();
			DefaultCategoryDataset linechartdatasetforcjezb = (DefaultCategoryDataset) plot.getDataset(2);
	    	
			String selected =  linechartdatasetforcjezb.getColumnKey(col).toString();
	    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
	    	Integer klearnclusterid = ((TDXNodesXPeriodExternalData)super.nodexdata).getApacheMathKLearnClusteringID(selecteddate);
	    	
			if ( col == shouldcolumn) {
	            return ShapeUtilities.createUpTriangle(5);
	        } else
	        if ( col == shouldcolumnlast) {
	       	 return ShapeUtilities.createDownTriangle(3);
	        } else 
	        if( klearnclusterid != null && klearnclusterid == this.settingclusterid) {
	        	Shape shape = ShapeUtilities.createRegularCross(2, 2);
	        	return shape;
	        	
	        } else
	        {
	    		java.awt.geom.Ellipse2D.Double shape = new java.awt.geom.Ellipse2D.Double(-0.5, -0.5, 0.5, 0.5);
	            return shape;
	        }
		}
    }
	/*
	 * 
	 */
	public void setKLearningClusterBaseLineId (int clusterid1)
	{
		this.settingclusterid = clusterid1;
	}
}
