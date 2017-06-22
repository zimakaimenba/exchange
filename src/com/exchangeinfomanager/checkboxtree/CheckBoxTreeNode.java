/*  
 CCmI Editor - A Collaborative Cross-Modal Diagram Editing Tool
  
 Copyright (C) 2011  Queen Mary University of London (http://ccmi.eecs.qmul.ac.uk/)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.exchangeinfomanager.checkboxtree;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;




/**
 * A special {@code DefaultMutableTreeNode} that has, in addition, the property of being (or not) 
 * selected.    
 *
 */
@SuppressWarnings("serial")
public  class CheckBoxTreeNode extends DefaultMutableTreeNode 

{  

    private static final long serialVersionUID = 1L;  

    public final static int SINGLE_SELECTION = 0;  

    public final static int DIG_IN_SELECTION = 4;  

    protected int selectionMode;  

    protected boolean isSelected;  
    
    private String xmlattrid; //XML file的每一项XML的ID
    private String xmlattrsltedcolor; //XML file的每一项XML的ID

    /**
	 * @return the xmltag
	 */
	public String getXmltag() {
		return xmlattrid;
	}

	/**
	 * @param xmltag the xmltag to set
	 */
	public void setXmltag(String xmltag) {
		this.xmlattrid = xmltag;
	}

	public CheckBoxTreeNode() {  
        this(null);  
    }  

    public CheckBoxTreeNode(Object userObject) 
    {  
        this(userObject, true, false);

    }  

    public CheckBoxTreeNode(Object userObject, boolean allowsChildren,boolean isSelected) 
    {  
        super(userObject, allowsChildren);  
        this.isSelected = isSelected;  
        setSelectionMode(DIG_IN_SELECTION);

    }  

   

	public void setSelectionMode(int mode) {  
        selectionMode = mode;  
    }  

    public int getSelectionMode() {  
        return selectionMode;  
    }  

    /** 
     * 选中父节点时也级联选中子节点 
     * @param isSelected 
     */  
    @SuppressWarnings("unchecked")  
    public void setSelected(boolean isSelected) {  
        this.isSelected = isSelected;  
        if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {  
            Enumeration e = children.elements();  
            while (e.hasMoreElements()) {  
                CheckBoxTreeNode node = (CheckBoxTreeNode) e.nextElement();  
                node.setSelected(isSelected);  
            }  
        }  
    }  

    public boolean isSelected() {  
        return isSelected;  
    }

	/**
	 * @return the xmlattrsltedcolor
	 */
	public String getXmlattrsltedcolor() {
		return xmlattrsltedcolor;
	}

	/**
	 * @param xmlattrsltedcolor the xmlattrsltedcolor to set
	 */
	public void setXmlattrsltedcolor(String xmlattrsltedcolor) {
		this.xmlattrsltedcolor = xmlattrsltedcolor;
	}  
    
    
}