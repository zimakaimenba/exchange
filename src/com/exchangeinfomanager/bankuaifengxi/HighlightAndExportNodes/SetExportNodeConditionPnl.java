package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.State;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker.StateValue;

import com.exchangeinfomanager.A.MainGui.StockInfoManager;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.StockCalendar.OnCalendarDateChangeListener;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;

public class SetExportNodeConditionPnl extends JPanel implements OnCalendarDateChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BanKuaiAndGeGuMatchingConditions globeexpc;
	private List<BanKuaiAndGeGuMatchingConditions> exportcond;
	private ExportTask exporttask;
	private LocalDate curselectdate;
	private String globeperiod;
	private StockInfoManager stockmanager;
	
	/**
	 * Create the panel.
	 */
	public SetExportNodeConditionPnl(BanKuaiAndGeGuMatchingConditions expc1) 
	{
		this.globeexpc = expc1;
		this.exportcond = new ArrayList<> ();
//		this.curselectdate = LocalDate.now();
		
		createMainBorardGui ();
	}
	
	public List<BanKuaiAndGeGuMatchingConditions> getCurrentSettingCondition ()
	{
		return this.exportcond;
	}

	private void createMainBorardGui() 
	{
		JLabel btnaddexportcond = JLabelFactory.createButton("",35, 25);
		btnaddexportcond.setHorizontalAlignment(SwingConstants.LEFT);
		btnaddexportcond.setToolTipText("<html>���õ�������:<br></html>");
		btnaddexportcond.setIcon(new ImageIcon(BanKuaiFengXi.class.getResource("/images/add-circular-outlined-button.png")));
		btnaddexportcond.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(SwingUtilities.isLeftMouseButton(arg0) ) 
					initializeExportConditions (btnaddexportcond);
				else if (SwingUtilities.isRightMouseButton(arg0)) {
					if( exportcond != null) { 
						exportcond.clear();
						
						btnaddexportcond.setText(String.valueOf(0));
						btnaddexportcond.setToolTipText("<html>������������(����Ҽ�ɾ������)<br></html>");
					}
				} else if (SwingUtilities.isMiddleMouseButton(arg0)) {}
			}
		});
		
		this.add(btnaddexportcond);
		
		JProgressBar progressBarExport = new JProgressBar();
		progressBarExport.setPreferredSize(new Dimension(85, 25));
		progressBarExport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (exporttask == null)   	exportBanKuaiWithGeGuOnCondition();
				else exporttask.cancel(true);
			}
		});
		progressBarExport.setString("���������������");
		progressBarExport.setToolTipText("���������������");
        progressBarExport.setStringPainted(true);
		
        this.add(progressBarExport);
	}
	/*
	 * 
	 */
	private JProgressBar getProgressBar () {
		for (Component tmpcomp : this.getComponents() ) {
            	if(tmpcomp instanceof JProgressBar)
            		return (JProgressBar)  tmpcomp;
        }
        return null;
	}
	/*
	 * 
	 */
	public void setCurrentDisplayDate (LocalDate date){
		this.curselectdate = date;
	}
	public void setCurrentExportPeriod (String period){
		this.globeperiod = period;
	}
	public void setStockInfoManager (StockInfoManager stkm){
		this.stockmanager = stkm;
	}
	
	protected void initializeExportConditions (JLabel btnaddexportcond) 
	{
		try {
			BanKuaiAndGeGuMatchingConditions expcCloned =  (BanKuaiAndGeGuMatchingConditions) this.globeexpc.clone();
			ExtraExportConditionsPnl extraexportcondition = new ExtraExportConditionsPnl (expcCloned,curselectdate);
			int extraresult = JOptionPane.showConfirmDialog(null,extraexportcondition , "���ӵ�������:", JOptionPane.OK_CANCEL_OPTION);
			if(extraresult == JOptionPane.OK_OPTION) { //������������ 
				expcCloned = extraexportcondition.getSettingCondition ();
				if( expcCloned.shouldExportOnlyCurrentBanKuai() ) {
//					expcCloned.setSettingBanKuai(exportbk);
				}
				exportcond.add(expcCloned);
				
				btnaddexportcond.setText(String.valueOf(exportcond.size() ));
				String tooltips = btnaddexportcond.getToolTipText() + "<html>" + expcCloned.getConditionsDescriptions() + "<br></html>";
				btnaddexportcond.setToolTipText(tooltips);
			}
		} catch (CloneNotSupportedException e) {e.printStackTrace();}

		return;
	}
	/*
	 * �ѵ�ǰ�İ�鵱�ܷ��������ĵ���
	 */
	private void exportBanKuaiWithGeGuOnCondition ()
	{
		if(exportcond == null || exportcond.size() == 0) {	JOptionPane.showMessageDialog(null,"δ���õ����������������õ���������");
			return;
		}

		int exchangeresult = JOptionPane.showConfirmDialog(null,"������ʱ�ϳ�������ȷ�������Ƿ���ȷ��\n�Ƿ񵼳���" , "ȷʵ������", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)			return;

		if(curselectdate == null)	curselectdate = LocalDate.now();
		String dateshowinfilename = null;
		if(globeperiod == null  || globeperiod.equals(NodeGivenPeriodDataItem.WEEK)) dateshowinfilename = "week" + curselectdate.with(DayOfWeek.FRIDAY).toString().replaceAll("-","");
		else if(globeperiod.equals(NodeGivenPeriodDataItem.DAY))	dateshowinfilename = "day" + curselectdate.toString().replaceAll("-","");
		else if(globeperiod.equals(NodeGivenPeriodDataItem.MONTH))	dateshowinfilename = "month" +  curselectdate.withDayOfMonth(curselectdate.lengthOfMonth()).toString().replaceAll("-","");
		String exportfilename = (new SetupSystemConfiguration()).getTDXModelMatchExportFile () + "TDXģ�͸���" + dateshowinfilename + ".EBK";
		File filefmxx = new File( exportfilename );
		if( !filefmxx.getParentFile().exists() ) {             //���Ŀ���ļ����ڵ�Ŀ¼�����ڣ��򴴽���Ŀ¼    
            if(!filefmxx.getParentFile().mkdirs()) {  
                System.out.println("����Ŀ���ļ�����Ŀ¼ʧ�ܣ�");  
                return ;  
            }  
        }  
		try {	if (filefmxx.exists()) { //In future, should add new function for if file exists, backup the old one.
					filefmxx.delete();
					filefmxx.createNewFile();
				} else
					filefmxx.createNewFile();
		} catch (Exception e) {		e.printStackTrace();return ;}
		
		if(this.stockmanager != null)	this.stockmanager.setGetNodeDataFromDbWhenSystemIdleThreadStatus(false);
		if(globeperiod == null)		globeperiod = NodeGivenPeriodDataItem.WEEK;
		
		exporttask = new ExportTask(exportcond, curselectdate,globeperiod,filefmxx);
		exporttask.addPropertyChangeListener(new PropertyChangeListener() {
		      @Override
		      public void propertyChange(final PropertyChangeEvent eventexport) 
		      {  
		    	  JProgressBar progressBarExport = getProgressBar ();
			      	switch (eventexport.getPropertyName()) {
			        case "progress":
			        	progressBarExport.setIndeterminate(false);
			        	progressBarExport.setString("���ڵ���..." + (Integer) eventexport.getNewValue() + "%(,���ȡ������)");
			        	progressBarExport.setToolTipText("���ȡ������");
			          break;
			        case "state":
			          switch ((StateValue) eventexport.getNewValue()) {
			          case DONE:
			            try {
			              final int count = exporttask.get();
			              int exchangeresult = JOptionPane.showConfirmDialog(null, "������ɣ��Ƿ��" + filefmxx.getAbsolutePath() + "�鿴","�������", JOptionPane.OK_CANCEL_OPTION);
			      		  if(exchangeresult != JOptionPane.CANCEL_OPTION) {
			      			try {	String path = filefmxx.getAbsolutePath();
				      				Runtime.getRuntime().exec("explorer.exe /select," + path);
				      		  } catch (IOException e1) {	e1.printStackTrace();}
				      		  
				      		  progressBarExport.setString("���������������");
				      		  exporttask = null;
				      		  System.gc();
			      		  } else {
			      			  progressBarExport.setString("���������������");exporttask = null;
			      			  System.gc();
			      		  }
			            } catch (final CancellationException e) {
			            	exporttask.cancel(true);
			            	exporttask = null;
			            	
			            	progressBarExport.setIndeterminate(false);  progressBarExport.setValue(0);
			            	JOptionPane.showMessageDialog(null, "�����������ɱ���ֹ��", "������������",JOptionPane.WARNING_MESSAGE);
			            	progressBarExport.setString("����������������");
			            	
			            	System.gc();
			            } catch (final Exception e) { e.printStackTrace(); }
			            break;
			          case STARTED:
			          case PENDING:
			        	  progressBarExport.setVisible(true);
			        	  progressBarExport.setIndeterminate(true);
			            break;
			          }
			          break;
			        }
			      }
		    });
		
		exporttask.execute();
	}

	@Override
	public void dateChanged(LocalDate newdate) {
		this.setCurrentDisplayDate(newdate);
	}


}

