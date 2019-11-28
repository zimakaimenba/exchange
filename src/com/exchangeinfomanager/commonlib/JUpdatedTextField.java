package com.exchangeinfomanager.commonlib;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.JTextField;

public class JUpdatedTextField extends JTextField 
{
	public static int  PASTEWITHCLEAR = 3, PASTETOLEFT = 4, PASTETORIGHT = 5, PASTETOCUR = 7;
	
	public JUpdatedTextField ()
	{
		super ();
		mouseleftclear = true;
		mouserightpaste = true;
		this.pastepostion = JUpdatedTextField.PASTETORIGHT; 
		
		createEvents ();
	}
	public JUpdatedTextField (String str)
	{
		super (str);
		mouseleftclear = true;
		mouserightpaste = true;
		this.pastepostion = JUpdatedTextField.PASTETORIGHT; 
		
		createEvents ();
	}
	private boolean mouseleftclear;
	private boolean mouserightpaste;
	private int pastepostion;

	public void setMouseLeftClearEnabled (boolean enabled)
	{
		this.mouseleftclear = enabled;
	}
	public void setMouseRightPasteEnabled (boolean enabled)
	{
		this.mouserightpaste = enabled;
	}
	public void setMouseRightPasteEnabled (boolean enabled,int position)
	{
		this.mouserightpaste = enabled;
		if(this.mouserightpaste)
			this.pastepostion =  position;
	}
	
	private void createEvents ()
	{
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				//System.out.println("this is the test");
				
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
                  setEditorToNull ();
              } else if (e.getButton() == MouseEvent.BUTTON3) {
              
            	  setEditorInfo ();
              	
              }
				
			}
			
		});
	}

	protected void setEditorToNull() 
	{
		if(this.mouseleftclear && this.isEnabled()) {
			this.setText("");
			this.setColumns(10);
			this.setCaretPosition(0);
		}
		
		
	}
	
	private void setEditorInfo ()
	{
		if(!this.mouserightpaste)
			return;
		
		if(!this.isEnabled())
			return;
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
    	Clipboard clipboard = toolkit.getSystemClipboard();
    	try	{
    		String result = (String) clipboard.getData(DataFlavor.stringFlavor);
    		
    		if(this.pastepostion == JUpdatedTextField.PASTETORIGHT) {
    			this.setText(this.getText() + result);
    		} else if(this.pastepostion == JUpdatedTextField.PASTETOCUR) {
    			this.getDocument().insertString(this.getCaretPosition(),result,null);
    		} else if(this.pastepostion == JUpdatedTextField.PASTETOLEFT) {
    			this.setText(result + this.getText()  );
    		} else if(this.pastepostion == JUpdatedTextField.PASTEWITHCLEAR) {
    			this.setText(result  );
    		}

    		
    		
    		
    	} catch(Exception e)	{
    		e.printStackTrace();
    	}
	}
}
