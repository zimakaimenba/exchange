package com.exchangeinfomanager.bankuaichanyelian;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.*;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;



public class BkChanYeLianTreeNode  extends DefaultMutableTreeNode
{

    public BkChanYeLianTreeNode(String subject, String myowncode1  ){
        super(subject);
        this.myowncode = myowncode1;
        expanded = false;
        shouldberemovedwhensavexml = false;
    }

    private String suoshutdxbkzscode; //所属通达信板块指数代码
	private ArrayList<String> hanyupingyin; //汉语拼音
	
	public static int  TDXBK = 4, SUBBK = 5, BKGEGU = 6;
    private int nodetype;
	private String myowncode; //node自己的code,对通达信板块就是板块指数，和tdxbkzscode一致，对子板块，就是子板块code，对个股就是个股代码

	private boolean expanded;
   
	private HashSet<String> parsefilestockset; //板块解析文件内含有的该板块列表
	
	private boolean isofficallyselected ;
	private int inzdgzofficalcount =0;
	private int inzdgzcandidatecount =0;
	private Date selectedToZdgzTime;
	
	private boolean shouldberemovedwhensavexml;
	
	public void setShouldBeRemovedWhenSaveXml ( ) 
	{
		this.shouldberemovedwhensavexml = true;
	}
	public boolean shouldBeRemovedWhenSaveXml ( )
	{
		return this.shouldberemovedwhensavexml;
	}
	
    
	public void setTongDaXingBanKuaiCode (String tdxcode)
	{
		this.suoshutdxbkzscode = tdxcode;
	}
	public String getTongDaXingBanKuaiCode ()
	{
		return this.suoshutdxbkzscode ;
	}
//	public void setNodeOwnCode (String code) {
//		this.myowncode = code;
//	}
	public String getNodeOwnCode () {
		return this.myowncode;
	}

    
//    @Override
//    public void setUserObject(Object userObject){
//        super.setUserObject(userObject);
//        //ginkgo.nodeChanged();
//    }
    public void setParseFileStockSet (HashSet<String> parsefilestockset2)
    {
    	if(this.parsefilestockset == null)
    		this.parsefilestockset = new HashSet<String> ();
    	if(this.nodetype == BkChanYeLianTreeNode.SUBBK ) { //对于子板块来说，set是增加的
    		this.parsefilestockset.addAll(parsefilestockset2);
    	} else
    		this.parsefilestockset = parsefilestockset2;
    }
    public HashSet<String> getParseFileStockSet ()
    {
    	if(this.parsefilestockset == null)
    		return new HashSet<String> ();
    	else
    		return this.parsefilestockset;
    }
    public void setExpansion(boolean expanded){
        this.expanded = expanded;
    }
    
    public boolean isExpanded(){
        return expanded;
    }
    
    public int getNodeType()
    {
        return nodetype;
    }
    
    public void setNodeType(int newType){
    	nodetype = newType;
    	
    	HanYuPinYing hypy = new HanYuPinYing ();
    	hanyupingyin = new ArrayList<String> ();
   		String codehypy = hypy.getBanKuaiNameOfPinYin(myowncode); 
   		String namehypy = hypy.getBanKuaiNameOfPinYin(this.getUserObject().toString() );
   		hanyupingyin.add(codehypy);
   		hanyupingyin.add(namehypy);
    	
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
        	System.out.println(this.getUserObject().toString()+ "拼音是NULL" );
    	}
    	
    	return found;
    	
    }
    public void increaseZdgzOfficalCount ()
    {
    	inzdgzofficalcount ++;
    	System.out.println(this.getUserObject().toString() + "node offical count = " + inzdgzofficalcount);
    }
    public void decreaseZdgzOfficalCount ()
    {
    	inzdgzofficalcount --;
    	if(inzdgzofficalcount == 0 )
    		isofficallyselected = false;
    	System.out.println(this.getUserObject().toString() + "node offical count = " + inzdgzofficalcount);
    }
    
    public void increaseZdgzCandidateCount ()
    {
    	inzdgzcandidatecount ++;
    	System.out.println(this.getUserObject().toString() +"node cand count = " + inzdgzcandidatecount);
    }
    public void decreasedgzCandidateCount ()
    {
    	inzdgzcandidatecount --;
    	System.out.println(this.getUserObject().toString() + "node cand count = " + inzdgzcandidatecount);
    }
    public int getInZdgzOfficalCount ()
    {
    	return inzdgzofficalcount;
    	
    }
    public int getInZdgzCandidateCount ()
    {
    	return inzdgzcandidatecount; 
    }
    public void setOfficallySelected (boolean ofslt)
	{
		this.isofficallyselected = ofslt;
	}
	public boolean isOfficallySelected ()
	{
		return this.isofficallyselected;
	}
	
	/**
	 * @return the tdxbk
	 */
	public String getTdxBk() {
		
		 TreeNode[] nodepath = this.getPath();
		 String tdxbk =	 ((BkChanYeLianTreeNode)nodepath[1]).getUserObject().toString() ;
		return tdxbk;
	}
//	/**
//	 * @param tdxbk the tdxbk to set
//	 */
//	public void setTdxbk(TreePath path) {
//		this.tdxbk = tdxbk;
//	}
	/**
	 * @return the bkchanyelian
	 */
	public String getChanYeLian() {
		 String bkchanyelian = "";
		 TreeNode[] nodepath = this.getPath();
		 for(int i=1;i<nodepath.length;i++) {
			 bkchanyelian = bkchanyelian + ((BkChanYeLianTreeNode)nodepath[i]).getUserObject().toString() + "->";
		 }

		return bkchanyelian.substring(0,bkchanyelian.length()-2);
	}
//	/**
//	 * @param bkchanyelian the bkchanyelian to set
//	 */
//	public void setChanYeLian(String bkchanyelian) {
//		this.bkchanyelian = bkchanyelian;
//	}
//	public void setChanYeLian(TreePath path)
//	{
//		this.bkchanyelian = path.toString();
//		Object[] objpath = this.getUserObjectPath();
//		System.out.println(objpath.toString() );
//	}
	/**
	 * @return the selectedtime
	 */
	public String getSelectedToZdgzTime() {
		return this.formatDate(selectedToZdgzTime);
	}
	/**
	 * @param selectedtime the selectedtime to set
	 */
	public void setSelectedToZdgzTime(Date selectedtime) {
		this.selectedToZdgzTime = selectedtime;
	}
	public void setSelectedToZdgzTime(String selectedtime2) {
		this.selectedToZdgzTime = formatString(selectedtime2);
	}
	private String formatDate(Date tmpdate)
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		return formatterhwy.format(tmpdate);
		//return formatterhwy;
	}
	private Date formatString(String selectedtime2)
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		Date date = new Date() ;
		try {
			date = formatterhwy.parse(selectedtime2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
	public void clearCurParseFileStockSet() {
		if(this.parsefilestockset != null)
			this.parsefilestockset.clear();
	}
    
}
