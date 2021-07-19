package com.exchangeinfomanager.commonlib.jstockcombobox;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;

public class JStockComboBoxEditor implements ComboBoxEditor 
{

    JTextField editor;
    BkChanYeLianTreeNode       editedItem;

    @Override
    public void addActionListener(final ActionListener l) {

    }

    @Override
    public Component getEditorComponent() {
        if (this.editor == null) {
            this.editor = new JTextField();
        }
        return this.editor;
    }

    @Override
    public Object getItem() {
        return this.editedItem;
    }

    @Override
    public void removeActionListener(final ActionListener l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void selectAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setItem(final Object anObject) 
    {
    	try{
    		this.editedItem = (BkChanYeLianTreeNode) anObject;
    	} catch (java.lang.ClassCastException e)    	{
    		this.editor.setText("");
    		return;
    	}
        try{
        	this.editor.setText( this.editedItem.getMyOwnCode() + this.editedItem.getMyOwnName() );
         
    	}catch (java.lang.NullPointerException e) {
        	this.editor.setText("");
        }
    }

}