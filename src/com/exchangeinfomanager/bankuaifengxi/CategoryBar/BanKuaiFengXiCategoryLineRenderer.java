package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.time.LocalDate;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.ShapeUtilities;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;

class  BanKuaiFengXiCategoryLineRenderer extends LineAndShapeRenderer
{
	private NodeXPeriodDataBasic nodexdata;
	private int shouldcolumn = -1;
	private int shouldcolumnlast = -2;
	private TDXNodes node;
	private Color displayedcolumncolorindex;
	public BanKuaiFengXiCategoryLineRenderer ()
	{
		super ();
		
//		java.awt.geom.Ellipse2D.Double shape = new java.awt.geom.Ellipse2D.Double(-2.0, -2.0, 4.0, 4.0);
//		this.setBaseShape(shape);
//		this.setShapesVisible(false);
		this.setBaseShapesVisible(true);
		// or maybe plot1.getRenderer().setSeriesShape(0, shape);
//	    DecimalFormat decimalformat1 = new DecimalFormat("##");
//	    this.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", decimalformat1));
//	    this.setItemLabelsVisible(true);
//	    this.setSeriesVisible(true);
//		this.setBaseItemLabelsVisible(true);
		this.setBaseToolTipGenerator(new BanKuaiFengXiCategoryLineToolTipGenerator () );
		
	}
	
	 @Override
     public Shape getItemShape(int row, int col) 
	 {
         if ( col == shouldcolumn) {
//             return ShapeUtilities.createDiagonalCross(5, 2);
             return ShapeUtilities.createUpTriangle(5);
         } if ( col == shouldcolumnlast) {
//        	 return super.getItemShape(row, col);
        	 return ShapeUtilities.createDownTriangle(3);
         } else {
     		java.awt.geom.Ellipse2D.Double shape = new java.awt.geom.Ellipse2D.Double(-0.5, -0.5, 0.5, 0.5);
             return shape;
         }
     }
//	 @Override
//	 public Paint getItemPaint (int row, int col)
//	 {
//		return displayedcolumncolorindex;
//		 
//	 }
	 
	
	 /*
		 * 单个个股用这个
		 */
		public void setDisplayNodeXPeriod(NodeXPeriodDataBasic nodexdata1) 
	    {
			this.nodexdata = nodexdata1;
//			BanKuaiFengXiCategoryBarToolTipGenerator tooltipgenerator = (BanKuaiFengXiCategoryBarToolTipGenerator)this.getBaseToolTipGenerator();
//	    	tooltipgenerator.setDisplayNodeXPeriod(nodexdata1);
//	    	
//	    	BkfxItemLabelGenerator labelgenerator = (BkfxItemLabelGenerator)this.getBaseItemLabelGenerator();
//	    	labelgenerator.setDisplayNodeXPeriod(nodexdata1);
		}
		/*
		 * 
		 */
		public void setBarColumnShouldChangeColor (int column)
	    {
			if(this.shouldcolumn != column) { //说明用户的选择有了改变，需要修改
				this.shouldcolumnlast = this.shouldcolumn;
				this.shouldcolumn = column;
			}
			
	    }
		/*
		 * 
		 */
	    public void setDisplayNode (TDXNodes curdisplayednode) 
	    {
	    	this.node = curdisplayednode;
//	    	BanKuaiFengXiCategoryBarToolTipGenerator tooltipgenerator = (BanKuaiFengXiCategoryBarToolTipGenerator)this.getBaseToolTipGenerator();
//	    	tooltipgenerator.setDisplayNode(curdisplayednode);
	    }
	    /*
	     * 
	     */
//	    public void setDisplayMaxwkLevel (int maxl) 
//	    {
//	    	this.displayedmaxwklevel = maxl;
//	    	BkfxItemLabelGenerator lbg = (BkfxItemLabelGenerator)this.getBaseItemLabelGenerator();
//	    	lbg.setDisplayedMaxWkLevel(maxl);
//	    }
	    /*
	     * 用户可以自定义显示颜色
	     */
		public void setBarDisplayedColor(Color colorindex) 
		{
			this.displayedcolumncolorindex = colorindex;
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