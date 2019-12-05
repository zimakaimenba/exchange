package com.exchangeinfomanager.nodes;

import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.exchangeinfomanager.News.ExternalNewsType.GuanZhu;
import com.exchangeinfomanager.News.QiangRuoShiNode.QiangRuoShiNode;
import com.exchangeinfomanager.Trees.InvisibleNode;
import com.exchangeinfomanager.nodes.nodejibenmian.NodeJiBenMian;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.base.Strings;

public abstract class BkChanYeLianTreeNode  extends InvisibleNode 
{
	public static int  DAPAN = 3, TDXBK = 4,  BKGEGU = 7, TDXGG = 6, GPC = 8, SUBGPC = 9; //SUBBK = 5,

    public BkChanYeLianTreeNode(String myowncode, String name)
    {
		super(myowncode);

		if(name != null)
			this.myname = name;
		else 
			name = "";
        
      	HanYuPinYing hypy = new HanYuPinYing ();
    	hanyupingyin = new ArrayList<String> ();
   		String codehypy = hypy.getSouZiMuOfPinYin(myowncode); 
   		String namehypy = hypy.getSouZiMuOfPinYin(name );
   		hanyupingyin.add(codehypy);
   		hanyupingyin.add(namehypy);
   		
   		//NodeJiBenMian  TreeRelated
   		this.nodejbm = new NodeJiBenMian ();
  }
	
    private Logger logger = Logger.getLogger(BkChanYeLianTreeNode.class);
	private ArrayList<String> hanyupingyin; //ï¿½ï¿½ï¿½ï¿½Æ´ï¿½ï¿½
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
		
		return this.getUserObject().toString().trim();
	}
	/*
	 * ÁÙÊ±
	 */
	public void setMyOwnCode (String code)
	{
		super.setUserObject(code);
	}
	
	public String getCreatedTime() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setCreatedTime(String bankuaicreatedtime) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BkChanYeLianTreeNode node = (BkChanYeLianTreeNode) o;
        
        if( ! this.getMyOwnCode().equals(node.getMyOwnCode() ) )
        	return false;
        
        if( this.getType() != node.getType()  )
        	return false;
        
        return true;
    }

    @Override
    public int hashCode() {
    	int result = 0;

		result = this.getMyOwnCode().hashCode();
        result = 31 * result + this.getType();
        
        return result;
    }

}


