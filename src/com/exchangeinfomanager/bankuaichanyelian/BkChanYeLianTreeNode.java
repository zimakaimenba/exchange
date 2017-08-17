package com.exchangeinfomanager.bankuaichanyelian;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.*;

import com.google.common.base.Splitter;



public class BkChanYeLianTreeNode  extends DefaultMutableTreeNode
{

    public BkChanYeLianTreeNode(String subject ){
        super(subject);
        expanded = false;
        parsefilestockset = new HashSet<String> ();
    }

	public static int  TDXBK = 4, SUBBK = 5, BKGEGU = 6;
    private int nodetype;
    private boolean expanded;
    private String noteText = null;
    private String hanyupingyin;
	private String tdxbkzscode; //所属通达信板块指数代码
	private HashSet<String> parsefilestockset;
	private int inzdgzofficalcount =0;
	private int inzdgzcandidatecount =0;
	
	//private TreePath pathintree;
	private String bkchanyelian;
	
	private Date selectedtime;
	private boolean isofficallyselected ;
	
//    private boolean willbedeleted = false;
//    private boolean isnewbk = false;
    

    
    @Override
    public void setUserObject(Object userObject){
        super.setUserObject(userObject);
        //ginkgo.nodeChanged();
    }
    public void setParseFileStockSet (HashSet<String> parsefilestockset2)
    {
    	if(this.nodetype == BkChanYeLianTreeNode.SUBBK ) { //对于子板块来说，set是增加的
    		this.parsefilestockset.addAll(parsefilestockset2);
    	} else
    		this.parsefilestockset = parsefilestockset2;
    }
    public HashSet<String> getParseFileStockSet ()
    {
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
    }
    
    public void setNoteText(String text){
        noteText = text;
    }
    public String getNoteText(){
        return noteText;
    }
    public void setTDXBanKuaiZhiShuCode (String code)
    {
    	tdxbkzscode = code;
    }
    public String getTDXBanKuaiZhiShuCode ()
    {
    	return this.tdxbkzscode;
    }
    public void setHanYuPingYin (String pingyin)
    {
    	this.hanyupingyin =  pingyin;
    }
    public String getHanYuPingYin ()
    {
    	return this.hanyupingyin;
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
	public String getSelectedtime() {
		return this.formatDate(selectedtime);
	}
	/**
	 * @param selectedtime the selectedtime to set
	 */
	public void setSelectedtime(Date selectedtime) {
		this.selectedtime = selectedtime;
	}
	public void setSelectedtime(String selectedtime2) {
		this.selectedtime = formatString(selectedtime2);
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
		this.parsefilestockset.clear();
	}
    
}
