package com.exchangeinfomanager.bankuaifengxi.ai;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.cjwizard.WizardPage;

//http://www.leepoint.net/GUI/structure/40mvc.html
public class WizardGuiXmlHandlerController implements ChangeListener,PropertyChangeListener
{
	private WeeklyFengXiXmlHandler xmlhandler;
	private List<WeeklyFenXiWizardPage> wizardpage;
	
	 

	public WizardGuiXmlHandlerController(List<WeeklyFenXiWizardPage> wp,WeeklyFengXiXmlHandler xmlh) 
	{
		this.wizardpage = wp;
		this.xmlhandler = xmlh;
		
		for(WeeklyFenXiWizardPage wzp : wizardpage) {
			wzp.addPropertyChangeListener(this);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
        if (evt.getPropertyName().equals(WeeklyFengXiXmlHandler.XMLINDB_PROPERTY)) {
        	xmlhandler.setXmlindbchanged(true);
        } 
        else if (evt.getPropertyName().equals(WeeklyFengXiXmlHandler.XMLMATRIX_PROPERTY)) {
        	xmlhandler.setXmlmatrixfilechanged(true);
        }  else if (evt.getPropertyName().equals(WeeklyFengXiXmlHandler.XMLINDB_ADDED)) {
        	xmlhandler.setXmlmatrixfilechanged(true);
        }else if (evt.getPropertyName().equals(WeeklyFengXiXmlHandler.XMLMATRIX_RMVED)) {
        	xmlhandler.setXmlmatrixfilechanged(true);
        }
    }

	@Override
	public void stateChanged(ChangeEvent evt) 
	{
	}

	public void saveFenXiResult() 
	{
		if(xmlhandler.isXmlindbchanged()) {
			String comments = null ;
			comments = wizardpage.get(0).getWeeklyFenXiComments();
			
			xmlhandler.getZhongDianGuanZhu().setDaPanAnalysisComments(comments);
			xmlhandler.saveNodeFenXiXml ();
		}
		if(xmlhandler.isXmlmatrixfilechanged()) {
			xmlhandler.saveFenXiXmlMatrixFile ();
		}
		
	}

	
}
