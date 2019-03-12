package com.exchangeinfomanager.nodes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.tree.*;

import org.apache.log4j.Logger;
import org.jfree.data.ComparableObjectItem;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.nodejibenmian.NodeJiBenMian;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.operations.InvisibleNode;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

public abstract class BkChanYeLianTreeNode  extends InvisibleNode 
{
	public static int  DAPAN = 3, TDXBK = 4, SUBBK = 5, BKGEGU = 7, TDXGG = 6, GPC = 8, SUBGPC = 9;

    public BkChanYeLianTreeNode(String myowncode, String name)
    {
		super(myowncode);

		if(name != null)
			this.myname = name;
		else 
			name = "";
        
      	HanYuPinYing hypy = new HanYuPinYing ();
    	hanyupingyin = new ArrayList<String> ();
   		String codehypy = hypy.getBanKuaiNameOfPinYin(myowncode); 
   		String namehypy = hypy.getBanKuaiNameOfPinYin(name );
   		hanyupingyin.add(codehypy);
   		hanyupingyin.add(namehypy);
   		
   		//NodeJiBenMian  TreeRelated
   		this.nodejbm = new NodeJiBenMian ();
  }
	
    private Logger logger = Logger.getLogger(BkChanYeLianTreeNode.class);
	private ArrayList<String> hanyupingyin; //����ƴ��
    protected int nodetype;
	private String myname;
	protected NodeJiBenMian nodejbm;
	protected NodesTreeRelated nodetreerelated;
	
	
	public NodeJiBenMian getNodeJiBenMian ()
	{
		return this.nodejbm;
	}

	public NodesTreeRelated getNodeTreeRelated ()
	{
		return this.nodetreerelated;
	}
  
    public boolean checktHanYuPingYin (String nameorhypy)
    {
    	if(Strings.isNullOrEmpty(nameorhypy) )
    		return false;
    	boolean found = false;

    	try {
    		for(String parthypy: this.hanyupingyin)
        		if(parthypy.equals(nameorhypy)) {
        			found = true;
        			return found;
        		}
    	} catch (java.lang.NullPointerException ex) {
        	logger.debug(this.getUserObject().toString()+ "ƴ����NULL" );
    	}
    	
    	return found;
    	
    }
    	
	public int getType() {
		return this.nodetype;
	}
	
	
	public String getMyOwnName() {
		// TODO Auto-generated method stub
		return this.myname;
	}
	
	public void setMyOwnName(String bankuainame) {
		this.myname = bankuainame;
		
	}
	
	public String getMyOwnCode() {
		// TODO Auto-generated method stub
		return this.getUserObject().toString().trim();
	}
	
	public String getCreatedTime() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setCreatedTime(String bankuaicreatedtime) {
		// TODO Auto-generated method stub
		
	}
	/*
	 * 
	 */
//	public abstract NodeXPeriodDataBasic getNodeXPeroidData (String period);

//	public NodeXPeriodDataBasic getNodeXPeroidData (String period)
//	{
//		if(period.equals(StockGivenPeriodDataItem.WEEK))
//			return nodewkdata;
//		else if(period.equals(StockGivenPeriodDataItem.MONTH))
//			return nodemonthdata;
//		else if(period.equals(StockGivenPeriodDataItem.DAY))
//			return nodedaydata;
//		else 
//			return null;
//	}
		 
}


