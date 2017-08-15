package net.ginkgo.dom4jcopy;

import javax.swing.tree.*;



public class GinkgoNode extends DefaultMutableTreeNode
{

	public GinkgoNode()
	{
        expanded = false;
    }

    
    //public static int INACTIVE = 0, ACTIVE = 1, COMPLETE = 2, NOW = 3, TDXBK = 4, SUBBK = 5, BKGEGU = 6;
	public static int  TDXBK = 4, SUBBK = 5, BKGEGU = 6;
    private int status;
    private boolean expanded;
    private String noteText = null;
    private String hanyupingyin;
	private String tdxbkzscode;
//    private boolean willbedeleted = false;
//    private boolean isnewbk = false;
    
    
    public GinkgoNode(String subject ){
        super(subject);
        //this.status = status;
        //this.ginkgo = ginkgo;
        //this.nodestockNew = nodeNew; //仅对个股节点作用，如果是新加入个股，就标记为new, 最后存盘时候要存入数据库 
    }
    
    @Override
    public void setUserObject(Object userObject){
        super.setUserObject(userObject);
        //ginkgo.nodeChanged();
    }
    
    public void setExpansion(boolean expanded){
        this.expanded = expanded;
    }
    
    public boolean isExpanded(){
        return expanded;
    }
    
    public int getStatus(){
        return status;
    }
    
    public void setStatus(int newStatus){
        //if (newStatus<0 || newStatus>3) newStatus = 0; 
        status = newStatus;
    }
    
    public void setNoteText(String text){
        noteText = text;
    }
    public void setTDXBanKuaiZhiShuCode (String code)
    {
    	tdxbkzscode = code;
    }
    public String getTDXBanKuaiZhiShuCode ()
    {
    	return this.tdxbkzscode;
    }
    public String getNoteText(){
        return noteText;
    }
    public void setHanYuPingYin (String pingyin)
    {
    	this.hanyupingyin =  pingyin;
    }
    public String getHanYuPingYin ()
    {
    	return this.hanyupingyin;
    }
    
//    public boolean isWillBeDeleted ()
//    {
//    	return willbedeleted;
//    }
//    public void setWillBeDeleted ()
//    {
//    	this.willbedeleted = true;
//    }
//    public boolean isNewBanKuai ()
//    {
//    	return this.isnewbk;
//    }
//    public void setIsNewBanKuai ()
//    {
//    	this.isnewbk = true;
//    }
//    boolean nodestockNew; //记录node的类型，系统板块？子板块？股票名称？
//	/**
//	 * @return the nodeType
//	 */
//	public boolean getStrockNodeNew() {
//		return nodestockNew;
//	}
//
//	/**
//	 * @param nodeType the nodeType to set
//	 */
//	private void setStockNodeType(boolean nodeNew) {
//		this.nodestockNew = nodeNew;
//	}
    
    
}
