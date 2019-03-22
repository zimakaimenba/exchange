package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.time.LocalDate;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;

import com.exchangeinfomanager.commonlib.CommonUtility;

class  BanKuaiFengXiCategoryLineRenderer extends LineAndShapeRenderer
{
	public BanKuaiFengXiCategoryLineRenderer ()
	{
		super ();
		
//		java.awt.geom.Ellipse2D.Double shape = new java.awt.geom.Ellipse2D.Double(-2.0, -2.0, 4.0, 4.0);
//		this.setBaseShape(shape);
		this.setShapesVisible(false);
		// or maybe plot1.getRenderer().setSeriesShape(0, shape);
//	    DecimalFormat decimalformat1 = new DecimalFormat("##");
//	    this.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", decimalformat1));
//	    this.setItemLabelsVisible(true);
//	    this.setSeriesVisible(true);
//		this.setBaseItemLabelsVisible(true);
		this.setBaseToolTipGenerator(new BanKuaiFengXiCategoryLineToolTipGenerator () );
		
	}
	
}

class BanKuaiFengXiCategoryLineToolTipGenerator implements CategoryToolTipGenerator 
{

	@Override
	public String generateToolTip(CategoryDataset dataset, int row, int column) 
	{
		Comparable rowkey = dataset.getRowKey(row);
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	
    	Number quekounumber = (Number)dataset.getValue(row, column);
    	
    	
    	return selecteddate.toString() + rowkey.toString() + ":" + quekounumber.toString();
	}
	
}