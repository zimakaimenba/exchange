package com.exchangeinfomanager.bankuaichanyelian;

import java.util.HashSet;

import javax.swing.tree.*;



public class BkChanYeLianTreeNode  extends DefaultMutableTreeNode
{

    public BkChanYeLianTreeNode(String subject ){
        super(subject);
        expanded = false;
        parsefilestockset = new HashSet<String> ();
        //this.status = status;
        //this.ginkgo = ginkgo;
        //this.nodestockNew = nodeNew; //仅对个股节点作用，如果是新加入个股，就标记为new, 最后存盘时候要存入数据库 
    }


    
    //public static int INACTIVE = 0, ACTIVE = 1, COMPLETE = 2, NOW = 3, TDXBK = 4, SUBBK = 5, BKGEGU = 6;
	public static int  TDXBK = 4, SUBBK = 5, BKGEGU = 6;
    private int nodetype;
    private boolean expanded;
    private String noteText = null;
    private String hanyupingyin;
	private String tdxbkzscode;
	private HashSet<String> parsefilestockset;
	private int inzdgzofficalcount =0;
	private int inzdgzcandidatecount =0;
//    private boolean willbedeleted = false;
//    private boolean isnewbk = false;
    

    
    @Override
    public void setUserObject(Object userObject){
        super.setUserObject(userObject);
        //ginkgo.nodeChanged();
    }
    public void setParseFileStockSet (HashSet<String> parsefilestockset2)
    {
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
    public void setAddInZdgzOfficalCount ()
    {
    	inzdgzofficalcount ++;
    }
    public void setMinesInZdgzOfficalCount ()
    {
    	inzdgzofficalcount --;
    }
    
    public void setAddInZdgzCandidateCount ()
    {
    	inzdgzcandidatecount ++;
    }
    public void setMinesInZdgzCandidateCount ()
    {
    	inzdgzcandidatecount --;
    }
    public int getInZdgzOfficalCount ()
    {
    	return inzdgzofficalcount;
    }
    public int getInZdgzCandidateCount ()
    {
    	return inzdgzcandidatecount; 
    }
    
	    
    
}
