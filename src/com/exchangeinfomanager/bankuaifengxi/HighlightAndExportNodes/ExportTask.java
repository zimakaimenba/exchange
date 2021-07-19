package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.SortByKeyWords.NodeChenJiaoErComparator;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
import com.google.common.io.Files;

public class ExportTask extends SwingWorker<Integer, String>  
{
	private LocalDate selectiondate;
	private File outputfile;
	private List<BanKuaiAndGeGuMatchingConditions> expclist;
	private String period;
	 
	
	public ExportTask (List<BanKuaiAndGeGuMatchingConditions> exportcond,LocalDate selectiondate,String period,File outputfile)
	{
		this.expclist = exportcond;
		this.selectiondate = selectiondate;
		this.period = period;
		this.outputfile = outputfile;
	}
	/*
	 * Main task. Executed in background thread.
	 */
	@Override
	public Integer doInBackground() 
	{
		Charset charset = Charset.forName("GBK") ;
	
		ArrayList<TDXNodes> outputnodeslist = new ArrayList<TDXNodes> ();
		
		String ouputfilehead2 = "";
		for(BanKuaiAndGeGuMatchingConditions expc : expclist) {
			if (isCancelled()) 
				 return null;
			
			setProgress(30);
			try{
				ExportMatchedNode2 exportaction = new ExportMatchedNode2 (expc);
				Set<TDXNodes> outputnodes = exportaction.checkTDXNodeMatchedCurSettingConditons(selectiondate, period);
				for(TDXNodes tmpnode : outputnodes) {
					if(!outputnodeslist.contains(tmpnode))
						outputnodeslist.add(tmpnode);
				}
				ouputfilehead2 = ouputfilehead2 + expc.getConditionsDescriptions ();
			} catch(Exception e) {	e.printStackTrace();}
			
			setProgress(50);
		}	
		
		//�Ե����İ��͸��ɰ����ܳɽ�����������������뵽ͨ���ź����Զ����ճɽ�������
		Collections.sort(outputnodeslist, new NodeChenJiaoErComparator(selectiondate,period) );
		
		try {
			Files.append("<��������:" + selectiondate.toString() + ">"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("<��������:" + period + ">"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("<" + ouputfilehead2 + ">" + System.getProperty("line.separator") ,outputfile, charset);
		} catch (IOException e1) {			e1.printStackTrace();} 
		
		//д��������ļ�
		for(BkChanYeLianTreeNode node : outputnodeslist) {
			 String outputstock;
			 String cjs = ((TDXNodes)node).getNodeJiBenMian().getSuoShuJiaoYiSuo();
			 if(cjs.trim().toLowerCase().equals("sh"))	outputstock= "1" + node.getMyOwnCode().trim();
			 else	outputstock ="0" + node.getMyOwnCode().trim();
			
			 outputstock = outputstock +  System.getProperty("line.separator");
			 try {
					Files.append(outputstock,outputfile, charset);
				} catch (IOException e) {
					e.printStackTrace();
			}
		}
	
		//����gephi�ļ�
	//	GephiFilesGenerator gephigenerator = new GephiFilesGenerator (allbksks);
	//	gephigenerator.generatorGephiFile(outputfile, dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), globeperiod);
			
			setProgress( 90 );
			setProgress( 100 );
			
			outputnodeslist = null;
			outputfile = null;
	
			return 100;
	}
	
	/*
	 * Executed in event dispatching thread
	 */
	@Override
	public void done() 
	{
		try {
			int i = get();
		} catch (InterruptedException | ExecutionException | CancellationException e) {}
	  
		SystemAudioPlayed.playSound();
	}
}