package com.exchangeinfomanager.nodes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.NodesServices.ServicesForNode;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.TimeIntervalUnion;
import com.exchangeinfomanager.nodes.nodejibenmian.ShuJuJiLuInfo;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;

import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import com.udojava.evalex.Expression;

public abstract class TDXNodes extends BkChanYeLianTreeNode
{
	public TDXNodes (String myowncode, String myownname)
	{
		super (myowncode,myownname);
	}
	
	protected NodeXPeriodData nodewkdata;
	protected NodeXPeriodData nodedaydata;
	protected NodeXPeriodData nodemonthdata;
	
	protected Collection<Tag> nodetags;
	

	private LocalDate lastdayofbxfx;
	
	protected List<Interval> qiangshirange;
	private List<Interval> ruoshirange;
	private List<Interval> dqguangzhurange;
	
	private ShuJuJiLuInfo ShuJuJiLu;
	
	public ShuJuJiLuInfo getShuJuJiLuInfo ()
	{
		if(this.ShuJuJiLu == null)
			ShuJuJiLu = new ShuJuJiLuInfo();
		
		return ShuJuJiLu;
	}

	public void addNewDuanQiGuanZhuRange (LocalDate start, LocalDate end) 
	{
		LocalDate joindate = start;
		LocalDate leftdate = end;
		DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);
		DateTime leftdt = new DateTime(leftdate.getYear(), leftdate.getMonthValue(), leftdate.getDayOfMonth(), 0, 0, 0, 0);
		Interval joinleftinterval = new Interval(joindt, leftdt);
		
		if(dqguangzhurange == null) {
			dqguangzhurange = new ArrayList<> ();
			dqguangzhurange.add(joinleftinterval);
			return;
		}
		Collection<Interval> newintervallist = new ArrayList<> ();
		ListIterator<Interval> iter = dqguangzhurange.listIterator();
		while(iter.hasNext()) {
			Interval tmpinterval = iter.next();
		    if(tmpinterval.overlaps(joinleftinterval)){
		    	Interval newinterval = TimeIntervalUnion.union(tmpinterval,joinleftinterval);
		    	newintervallist.add(newinterval);
		        iter.remove();
		    }
		}
		if(newintervallist.isEmpty()) dqguangzhurange.add(joinleftinterval);
		else dqguangzhurange.addAll(newintervallist);
	}
	public Interval isInDuanQiGuanZhuRange (LocalDate joindate) 
	{
		if(dqguangzhurange == null || dqguangzhurange.isEmpty()) 	return null;
		
		DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);

		for(int i=0; i<dqguangzhurange.size();i++) {
			Interval interval = dqguangzhurange.get(i);
			if (interval.contains(joindt) || interval.getEnd().isEqual(joindt))  {
				return interval;
			}
		}
		
		return null;
	}
	public Boolean isEverBeingDuanQiGuanZhu ()
	{
		if(this.dqguangzhurange != null && dqguangzhurange.size() >0 )			return true;
		else			return false;
	}
	/*
	 * 
	 */
	public void addNewQiangShiRange (LocalDate start, LocalDate end) 
	{
		LocalDate joindate = start;
		LocalDate leftdate = end;
		DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);
		DateTime leftdt = new DateTime(leftdate.getYear(), leftdate.getMonthValue(), leftdate.getDayOfMonth(), 0, 0, 0, 0);
		Interval joinleftinterval = new Interval(joindt, leftdt);
		
		if(qiangshirange == null) {
			qiangshirange = new ArrayList<> ();
			qiangshirange.add(joinleftinterval);
			return;
		}
		Collection<Interval> newintervallist = new ArrayList<> ();
		ListIterator<Interval> iter = qiangshirange.listIterator();
		while(iter.hasNext()) {
			Interval tmpinterval = iter.next();
		    if(tmpinterval.overlaps(joinleftinterval)){
		    	Interval newinterval = TimeIntervalUnion.union(tmpinterval,joinleftinterval);
		    	newintervallist.add(newinterval);
		        iter.remove();
		    }
		}
		if(newintervallist.isEmpty()) qiangshirange.add(joinleftinterval);
		else qiangshirange.addAll(newintervallist);
	}
	public Interval isInQiangShiBanKuaiRange (LocalDate joindate)
	{
		if(qiangshirange == null || qiangshirange.isEmpty()) 	return null;
		
		DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);

		for(int i=0; i<qiangshirange.size();i++) {
			Interval interval = qiangshirange.get(i);
			if (interval.contains(joindt) || interval.getEnd().isEqual(joindt))  {
				return interval;
			}
		}
		
		return null;
	}
	/*
	 * 
	 */
	public void addNewRuoShiRange (LocalDate start, LocalDate end) 
	{
		LocalDate joindate = start;
		LocalDate leftdate = end;
		DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);
		DateTime leftdt = new DateTime(leftdate.getYear(), leftdate.getMonthValue(), leftdate.getDayOfMonth(), 0, 0, 0, 0);
		Interval joinleftinterval = new Interval(joindt, leftdt);
		
		if(ruoshirange == null) {
			ruoshirange = new ArrayList<> ();
			ruoshirange.add(joinleftinterval);
			return;
		}
		Collection<Interval> newintervallist = new ArrayList<> ();
		ListIterator<Interval> iter = ruoshirange.listIterator();
		while(iter.hasNext()) {
			Interval tmpinterval = iter.next();
		    if(tmpinterval.overlaps(joinleftinterval)){
		    	Interval newinterval = TimeIntervalUnion.union(tmpinterval,joinleftinterval);
		    	newintervallist.add(newinterval);
		        iter.remove();
		    }
		}
		if(newintervallist.isEmpty()) ruoshirange.add(joinleftinterval);
		else ruoshirange.addAll(newintervallist);
	}
	public Interval isInRuoShiBanKuaiRange (LocalDate joindate) 
	{

		if(ruoshirange == null || ruoshirange.isEmpty()) 	return null;
		
		DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);

		for(int i=0; i<ruoshirange.size();i++) {
			Interval interval = ruoshirange.get(i);
			if (interval.contains(joindt) || interval.getEnd().isEqual(joindt))  {
				return interval;
			}
		}
		
		return null;
	}
		

	public NodeXPeriodData getNodeXPeroidData (String period)
	{
		if(period.equals(NodeGivenPeriodDataItem.WEEK))
			return nodewkdata;
		else if(period.equals(NodeGivenPeriodDataItem.MONTH))
			return nodemonthdata;
		else if(period.equals(NodeGivenPeriodDataItem.DAY))
			return nodedaydata;
		else 
			return null;
	}
	
	public String getNodeXPeroidDataInHtml (LocalDate requireddate, String period)
	{
		String html;
		NodeXPeriodData nodexdata = this.getNodeXPeroidData(period);
		if(super.getType() == BkChanYeLianTreeNode.TDXGG ) {
			 html =  nodexdata.getNodeXDataInHtml((DaPan)this.getRoot(),requireddate, 0);
		} else {
			html = nodexdata.getNodeXDataInHtml((DaPan)this.getRoot(),requireddate, 0);
		}
		
		org.jsoup.nodes.Document htmldoc = Jsoup.parse(html);
		org.jsoup.select.Elements htmlcontent = htmldoc.select("body");
		for(org.jsoup.nodes.Element htmlbody : htmlcontent) {
			org.jsoup.nodes.Element htmldiv = htmlbody.prependElement("div");
			htmldiv.append( super.getMyOwnCode() + super.getMyOwnName() );
		}	
		
		return htmldoc.toString();
	}
	
	public void setNodeTags (Collection<Tag> tags)
	{
		this.nodetags = tags;
	}
	public  Collection<Tag> getNodeTags () {
		return this.nodetags;
	}
	public boolean isTagInCurrentNodeTags (Tag tag)
	{
		if(this.nodetags.contains(tag))
			return true;
		else
			return false;
	}
	/*
	 * 
	 */
	public Boolean isNodeDataAtNotCalWholeWeekMode ()
	{
		if(this.lastdayofbxfx == null)
			return false;
		else
			return true;
	}
	public void setNodeDataAtNotCalWholeWeekMode (LocalDate lastdayofbxfx)
	{
		this.lastdayofbxfx = lastdayofbxfx;
	}
	public LocalDate getNodeDataAtNotCalWholeWeekModeLastDate ()
	{
		return this.lastdayofbxfx;
	}
	
	public abstract ServicesForNode getServicesForNode (Boolean getornot);
	
	public Boolean checkNodeDataMatchedWithFormula (String formula)
	{
		Boolean checkresult = false;
		List<String> exportfactors = Splitter.on("AND").omitEmptyStrings().splitToList(formula);
		for(String factor : exportfactors) {
			String factoreq = factor.replaceAll(" ", "");
			
			ArrayList<String> vars = new ArrayList<String>();
            Pattern p = Pattern.compile("\'.*?\'", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(factor);
            while (m.find()) 
                vars.add(m.group());
            
            String checkedvar = "";
            for(String var : vars) {
            	if(var.equals(checkedvar))	continue;
            	else  checkedvar = var;
            		
            	LocalDate sltdate = null ; String datestr = null;
            	Pattern pdate = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE);
                Matcher mdate = pdate.matcher(var);
                while (mdate.find()) {
                	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                	sltdate = LocalDate.parse(mdate.group(), formatter);
                	datestr = mdate.group();
                }
                int indexofdate = var.indexOf(datestr);
            	String kw = var.substring(1, indexofdate);
            	String period = var.substring(indexofdate+8, var.length()-1);
            	if(period.isEmpty()) period = "WEEK";
            	
                String sltvalue = getKeywordValue (kw,sltdate, period, factor);
                if(sltvalue == null) {	System.out.println(this.getMyOwnCode() + this.getMyOwnName() +  factoreq + "没有获取计算结果\n");
                	checkresult = null;
                	break;
                }
                	
                if(kw.equals("CLOSEVSMA") ) {
                	if(sltvalue.equals("true"))  checkresult = true;
                	else	checkresult = false;
                }
                else factoreq = factoreq.replace(var, sltvalue);
            }
            
            if(!factoreq.contains("CLOSEVSMA") && checkresult != null) 
            	try{
    		    	BigDecimal result = null; 
    		    	result = new Expression(factoreq).eval(); //https://github.com/uklimaschewski/EvalEx
    		    	String sesultstr = result.toString();
    			    if(sesultstr.equals("0"))   	checkresult = false;
    			    else   	checkresult = true;
    		    } catch (com.udojava.evalex.Expression.ExpressionException e) {e.printStackTrace();return false;}
            
            if(checkresult == null || checkresult == false  ) 
            	break;
		}
		
		return checkresult;
	}
	private String getKeywordValue(String kw, LocalDate selectdate, String curperiod,String factor)
	{
		  Object value = null;
		  factor = factor.replaceAll(" ", "");
		  NodeXPeriodData nodexdata = this.getNodeXPeroidData(curperiod);
          
		  if(kw.equals("CLOSEVSMA")) {
	          Pattern p = Pattern.compile("\'.*?\'", Pattern.CASE_INSENSITIVE);
	          Matcher m = p.matcher(factor);
	          int indexofeq ;  int indexofend = 0;
	          while (m.find()) {
	        	  indexofeq = m.start();
	        	  indexofend = m.end();
	          }
	          String maformula = factor.substring(indexofend + 1, factor.length()-1);
			  value =  nodexdata.getNodeDataByKeyWord( kw, selectdate,  maformula);
		  }
		  else try {
			  value = nodexdata.getNodeDataByKeyWord(kw,selectdate,"");
		  } catch (java.lang.NullPointerException e) {  e.printStackTrace();
			  System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "Get keyword failed. keyword is" + kw + selectdate.toString() + ". FACTOR is" + factor + "PERIOD is " + curperiod);
		  }
		  
		  if(value != null)
			  return value.toString();
		  else
			  return null;
	}
	
}
