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
		btnaddexportcond.setToolTipText("<html>设置导出条件:<br></html>");
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
						btnaddexportcond.setToolTipText("<html>导出条件设置(鼠标右键删除设置)<br></html>");
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
		progressBarExport.setString("点击导出条件个股");
		progressBarExport.setToolTipText("点击导出条件个股");
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
			int extraresult = JOptionPane.showConfirmDialog(null,extraexportcondition , "附加导出条件:", JOptionPane.OK_CANCEL_OPTION);
			if(extraresult == JOptionPane.OK_OPTION) { //其他导出条件 
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
	 * 把当前的板块当周符合条件的导出
	 */
	private void exportBanKuaiWithGeGuOnCondition ()
	{
		if(exportcond == null || exportcond.size() == 0) {	JOptionPane.showMessageDialog(null,"未设置导出条件，请先设置导出条件！");
			return;
		}

		int exchangeresult = JOptionPane.showConfirmDialog(null,"导出耗时较长，请先确认条件是否正确。\n是否导出？" , "确实导出？", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)			return;

		if(curselectdate == null)	curselectdate = LocalDate.now();
		String dateshowinfilename = null;
		if(globeperiod == null  || globeperiod.equals(NodeGivenPeriodDataItem.WEEK)) dateshowinfilename = "week" + curselectdate.with(DayOfWeek.FRIDAY).toString().replaceAll("-","");
		else if(globeperiod.equals(NodeGivenPeriodDataItem.DAY))	dateshowinfilename = "day" + curselectdate.toString().replaceAll("-","");
		else if(globeperiod.equals(NodeGivenPeriodDataItem.MONTH))	dateshowinfilename = "month" +  curselectdate.withDayOfMonth(curselectdate.lengthOfMonth()).toString().replaceAll("-","");
		String exportfilename = (new SetupSystemConfiguration()).getTDXModelMatchExportFile () + "TDX模型个股" + dateshowinfilename + ".EBK";
		File filefmxx = new File( exportfilename );
		if( !filefmxx.getParentFile().exists() ) {             //如果目标文件所在的目录不存在，则创建父目录    
            if(!filefmxx.getParentFile().mkdirs()) {  
                System.out.println("创建目标文件所在目录失败！");  
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
			        	progressBarExport.setString("正在导出..." + (Integer) eventexport.getNewValue() + "%(,点击取消导出)");
			        	progressBarExport.setToolTipText("点击取消导出");
			          break;
			        case "state":
			          switch ((StateValue) eventexport.getNewValue()) {
			          case DONE:
			            try {
			              final int count = exporttask.get();
			              int exchangeresult = JOptionPane.showConfirmDialog(null, "导出完成，是否打开" + filefmxx.getAbsolutePath() + "查看","导出完成", JOptionPane.OK_CANCEL_OPTION);
			      		  if(exchangeresult != JOptionPane.CANCEL_OPTION) {
			      			try {	String path = filefmxx.getAbsolutePath();
				      				Runtime.getRuntime().exec("explorer.exe /select," + path);
				      		  } catch (IOException e1) {	e1.printStackTrace();}
				      		  
				      		  progressBarExport.setString("点击导出条件个股");
				      		  exporttask = null;
				      		  System.gc();
			      		  } else {
			      			  progressBarExport.setString("点击导出条件个股");exporttask = null;
			      			  System.gc();
			      		  }
			            } catch (final CancellationException e) {
			            	exporttask.cancel(true);
			            	exporttask = null;
			            	
			            	progressBarExport.setIndeterminate(false);  progressBarExport.setValue(0);
			            	JOptionPane.showMessageDialog(null, "导出条件个股被终止！", "导出条件个股",JOptionPane.WARNING_MESSAGE);
			            	progressBarExport.setString("导出设置条件个股");
			            	
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

