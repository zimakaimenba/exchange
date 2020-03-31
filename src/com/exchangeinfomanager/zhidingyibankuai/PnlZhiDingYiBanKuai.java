package com.exchangeinfomanager.zhidingyibankuai;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.exchangeinfomanager.Services.ServicesForZhiDingYiBanKuai;
import com.exchangeinfomanager.commonlib.WrapLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;

public class PnlZhiDingYiBanKuai extends JPanel {

	private ServicesForZhiDingYiBanKuai svszdy;
	private JCheckBox[] zdybkckbxs;
	private Map<String, String> zdybkmap;
	private JPanel pnlbklist;

	/**
	 * Create the panel.
	 */
	public PnlZhiDingYiBanKuai(ServicesForZhiDingYiBanKuai svszdy1) 
	{
		this.svszdy = svszdy1;
		setLayout(new BorderLayout(0, 0));
		this.setSize(new Dimension(550, 300));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		pnlbklist = new JPanel();
		scrollPane.setViewportView(pnlbklist);
		pnlbklist.setLayout(new WrapLayout(WrapLayout.CENTER, 5, 5));
		
		initilizeGui ();
	}

	private void initilizeGui()
	{
		zdybkmap = svszdy.getZhiDingYiBanKuaiLists();
		
		ArrayList<String> zdybknames = new ArrayList<String> ();
		try {
			zdybknames.addAll(zdybkmap.keySet());
			
			Collator collator = Collator.getInstance(Locale.CHINESE); //用中文排序
	 		Collections.sort(zdybknames,collator);
	 		
			zdybkckbxs = new JCheckBox[zdybknames.size()];
			Iterator<String> tmpit = zdybknames.iterator();
			int i=0;
			while (tmpit.hasNext()) {
				String tmpname = tmpit.next();
				zdybkckbxs[i] = new JCheckBox(tmpname);
				pnlbklist.add(zdybkckbxs[i]);
				
				i++;
			}
		} catch (java.lang.NullPointerException e) {
			JLabel zdyfilenull = new JLabel ("没有自定义板块");
			pnlbklist.add(zdyfilenull);
		}
	}
	
	public void getSelectedZdyBk ()
	{
		
	}
	
	public void getSelectedZdyBkTooltips ()
	{
		
	}
	
	public List<String> getSelectedZdyBkGeGu ()
	{
		List<String> result = new ArrayList<> ();
		for(JCheckBox tmpbox:zdybkckbxs) {
			
			if( tmpbox.isSelected() )
				result.addAll( svszdy.getSpecificZhiDingYiBanKuaiInfo(tmpbox.getText())  );
		}
		
		return result;
	}

}
