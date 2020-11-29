package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.Properties;
import java.util.Set;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;

import net.coderazzi.filters.gui.AutoChoices;
import net.coderazzi.filters.gui.TableFilterHeader;


public class BanKuaiGeGuTableFromPropertiesFile extends BanKuaiGeGuBasicTable 
{ 
	public BanKuaiGeGuTableFromPropertiesFile (StockInfoManager stockmanager1)
	{
		super (stockmanager1);
		
		setPropertiesInfo ();
		
		BanKuaiGeGuTableModelFromPropertiesFile bkgegumapmdl = new BanKuaiGeGuTableModelFromPropertiesFile(prop);
		this.setModel(bkgegumapmdl);
		
		createTableHeaderTooltips (prop);
		
		this.renderer =  new BanKuaiGeGuTableRendererFromPropertiesFile (prop); 
		
		
//		this.setDefaultRenderer(Object.class, this.renderer );
		
		//http://esus.com/creating-a-jtable-with-headers-with-jtooltips/
//		ToolTipHeader header = new ToolTipHeader(this.getColumnModel() );
//	    header.setToolTipStrings(bkgegumapmdl.getTableHeader());
//	    header.setToolTipText("Default ToolTip TEXT");
//	    this.setTableHeader(header);
		
		//sort http://www.codejava.net/java-se/swing/6-techniques-for-sorting-jtable-you-should-know
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);
		
//		TableFilterHeader filterHeader = new TableFilterHeader(this, AutoChoices.ENABLED); //https://coderazzi.net/tablefilter/index.html#    //https://stackoverflow.com/questions/16277700/i-want-to-obtain-auto-filtering-in-jtable-as-in-ms-excel
		
		setColumnPreferredWidth ();
//		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
//		int columnIndexToSort = 3; //��������ռ������
//		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
//		sorter.setSortKeys(sortKeys);
//		sorter.sort();
		
//		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
//		headerRenderer.setBackground(new Color(239, 198, 46));
//
//		for (int i = 0; i < myJTable.getModel().getColumnCount(); i++) {
//		        this.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
//		}
		
	}
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableFromPropertiesFile.class);
	
	private BanKuaiGeGuTableRendererFromPropertiesFile renderer;
	private String systeminstalledpath;
	String[] jtableTitleStringsTooltips = new String[8];
	private Properties prop;
	
	private void setPropertiesInfo ()
	{

		File directory = new File("");//�趨Ϊ��ǰ�ļ���
		try{
//		    logger.debug(directory.getCanonicalPath());//��ȡ��׼��·��
//		    logger.debug(directory.getAbsolutePath());//��ȡ����·��
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
		} catch(Exception e) {
			System.exit(0);
		}
		
//		FileInputStream inputStream = null;
		String propxmlFileName = null ;
		try {
//			Properties properties = System.getProperties();
//		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
//		    
//			Properties prop = new Properties();
//			String propFileName =  this.systeminstalledpath  + "/config/config.properties";
//			inputStream = new FileInputStream(propFileName);
//			if (inputStream != null) {
//				prop.load(inputStream);
//			} else {
//				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
//			}
			
			prop = new Properties();
			String propFileName = this.systeminstalledpath  + "/config/bankuaigegu2.properties";
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} 
			inputStream.close();
			
			
			
		} catch (Exception e) {
			logger.info("property file '" + propxmlFileName + "' not found in the classpath") ;
		} finally {
			
		}
	}
	
	private void setColumnPreferredWidth ()
	{
		String column_width0  = prop.getProperty ("0column_preferredWidth");
		if(column_width0 != null) {
			this.getColumnModel().getColumn(0).setPreferredWidth(Integer.valueOf(column_width0) );
		}
		String column_width1  = prop.getProperty ("1column_preferredWidth");
		if(column_width1 != null) {
			this.getColumnModel().getColumn(1).setPreferredWidth(Integer.valueOf(column_width1) );
		}
		String column_width2  = prop.getProperty ("2column_preferredWidth");
		if(column_width2 != null) {
			this.getColumnModel().getColumn(2).setPreferredWidth(Integer.valueOf(column_width2) );
		}
		String column_width3  = prop.getProperty ("3column_preferredWidth");
		if(column_width3 != null) {
			this.getColumnModel().getColumn(3).setPreferredWidth(Integer.valueOf(column_width3) );
		}
		String column_width4  = prop.getProperty ("4column_preferredWidth");
		if(column_width4 != null) {
			this.getColumnModel().getColumn(4).setPreferredWidth(Integer.valueOf(column_width4) );
		}
		String column_width5  = prop.getProperty ("5column_preferredWidth");
		if(column_width5 != null) {
			this.getColumnModel().getColumn(5).setPreferredWidth(Integer.valueOf(column_width5) );
		}
		String column_width6  = prop.getProperty ("6column_preferredWidth");
		if(column_width6 != null) {
			this.getColumnModel().getColumn(6).setPreferredWidth(Integer.valueOf(column_width6) );
		}
		String column_width7  = prop.getProperty ("7column_preferredWidth");
		if(column_width7 != null) {
			this.getColumnModel().getColumn(7).setPreferredWidth(Integer.valueOf(column_width7) );
		}
//		this.getColumnModel().getColumn(1).setPreferredWidth(110);
//		this.getColumnModel().getColumn(7).setPreferredWidth(30);
//		this.getColumnModel().getColumn(4).setPreferredWidth(40);
//		this.getColumnModel().getColumn(6).setPreferredWidth(40);
		
	}
	
	private void createTableHeaderTooltips (Properties prop)
	{
		String column_name0  = prop.getProperty ("0column_name");
		String column_background_highlight_info0  = prop.getProperty ("0column_background_highlight_info");
		if(column_background_highlight_info0 != null)
			column_background_highlight_info0 = "��ͻ��:" + column_background_highlight_info0;
		else column_background_highlight_info0 = "";
		String column_foreground_highlight_info0  = prop.getProperty ("0column_foreground_highlight_info");
		if(column_foreground_highlight_info0 != null)
			column_foreground_highlight_info0 = "ǰͻ��:" + column_foreground_highlight_info0;
		else column_foreground_highlight_info0 = "";
			
		String column_name1  = prop.getProperty ("1column_name");
		String column_background_highlight_info1  = prop.getProperty ("1column_background_highlight_info");
		if(column_background_highlight_info1 != null)
			column_background_highlight_info1 = "��ͻ��:" + column_background_highlight_info1;
		else column_background_highlight_info1 = "";
		String column_foreground_highlight_info1  = prop.getProperty ("1column_foreground_highlight_info");
		if(column_foreground_highlight_info1 != null)
			column_foreground_highlight_info1 = "ǰͻ��:" + column_foreground_highlight_info1;
		else column_foreground_highlight_info1 = "";
		
		String column_name2  = prop.getProperty ("2column_name");
		String column_background_highlight_info2  = prop.getProperty ("2column_background_highlight_info");
		if(column_background_highlight_info2 != null)
			column_background_highlight_info2 = "��ͻ��:" + column_background_highlight_info2;
		else column_background_highlight_info2 = "";
		String column_foreground_highlight_info2  = prop.getProperty ("2column_foreground_highlight_info");
		if(column_foreground_highlight_info2 != null)
			column_foreground_highlight_info2 = "ǰͻ��:" + column_foreground_highlight_info2;
		else column_foreground_highlight_info2 = "";
		
		String column_name3  = prop.getProperty ("3column_name");
		String column_background_highlight_info3  = prop.getProperty ("3column_background_highlight_info");
		if(column_background_highlight_info3 != null)
			column_background_highlight_info3 = "��ͻ��:" + column_background_highlight_info3;
		else column_background_highlight_info3 = "";
		String column_foreground_highlight_info3  = prop.getProperty ("3column_foreground_highlight_info");
		if(column_foreground_highlight_info3 != null)
			column_foreground_highlight_info3 = "ǰͻ��:" + column_foreground_highlight_info3;
		else column_foreground_highlight_info3 = "";
		
		String column_name4  = prop.getProperty ("4column_name");
		String column_background_highlight_info4  = prop.getProperty ("4column_background_highlight_info");
		if(column_background_highlight_info4 != null)
			column_background_highlight_info4 = "��ͻ��:" + column_background_highlight_info4;
		else column_background_highlight_info4 = "";
		String column_foreground_highlight_info4  = prop.getProperty ("4column_foreground_highlight_info");
		if(column_foreground_highlight_info4 != null)
			column_foreground_highlight_info4 = "ǰͻ��:" + column_foreground_highlight_info4;
		else column_foreground_highlight_info4 = "";
		
		String column_name5  = prop.getProperty ("5column_name");
		String column_background_highlight_info5  = prop.getProperty ("5column_background_highlight_info");
		if(column_background_highlight_info5 != null)
			column_background_highlight_info5 = "��ͻ��:" + column_background_highlight_info5;
		else column_background_highlight_info5 = "";
		String column_foreground_highlight_info5  = prop.getProperty ("5column_foreground_highlight_info");
		if(column_foreground_highlight_info5 != null)
			column_foreground_highlight_info5 = "ǰͻ��:" + column_foreground_highlight_info5;
		else column_foreground_highlight_info5 = "";
		
		String column_name6  = prop.getProperty ("6column_name");
		String column_background_highlight_info6  = prop.getProperty ("6column_background_highlight_info");
		if(column_background_highlight_info6 != null)
			column_background_highlight_info6 = "��ͻ��:" + column_background_highlight_info6;
		else column_background_highlight_info6 = "";
		String column_foreground_highlight_info6  = prop.getProperty ("6column_foreground_highlight_info");
		if(column_foreground_highlight_info6 != null)
			column_foreground_highlight_info6 = "ǰͻ��:" + column_foreground_highlight_info6;
		else column_foreground_highlight_info6 = "";
		
		String column_name7  = prop.getProperty ("7column_name");
		String column_background_highlight_info7  = prop.getProperty ("7column_background_highlight_info");
		if(column_background_highlight_info7 != null)
			column_background_highlight_info7 = "��ͻ��:" + column_background_highlight_info7;
		else column_background_highlight_info7 = "";
		String column_foreground_highlight_info7  = prop.getProperty ("7column_foreground_highlight_info");
		if(column_foreground_highlight_info7 != null)
			column_foreground_highlight_info7 = "ǰͻ��:" + column_foreground_highlight_info7;
		else column_foreground_highlight_info7 = "";
		
		jtableTitleStringsTooltips[0] = column_name0 + "(" + column_background_highlight_info0 + "." + column_foreground_highlight_info0 +  ")";
		jtableTitleStringsTooltips[1] =	column_name1+ "(" + column_background_highlight_info1 + "." + column_foreground_highlight_info1 + ")";
		jtableTitleStringsTooltips[2] = column_name2+ "(" + column_background_highlight_info2 + "." + column_foreground_highlight_info2 + ")";
		jtableTitleStringsTooltips[3] = column_name3+ "(" + column_background_highlight_info3 + "." + column_foreground_highlight_info3 + ")";
		jtableTitleStringsTooltips[4] =	column_name4+ "(" + column_background_highlight_info4 + "." + column_foreground_highlight_info4 + ")";
		jtableTitleStringsTooltips[5] =	column_name5+ "(" + column_background_highlight_info5 + "." + column_foreground_highlight_info5 + ")";
		jtableTitleStringsTooltips[6] =	column_name6+ "(" + column_background_highlight_info6 + "." + column_foreground_highlight_info6 + ")";
		jtableTitleStringsTooltips[7] =	column_name7+ "(" + column_background_highlight_info7 + "." + column_foreground_highlight_info7+ ")";
		
//		shanghaistockcodeprefixlist = Splitter.on("/").omitEmptyStrings().splitToList(shanghaistockcodeprefix);
	}
	private String reformateStrings (String str)
	{
		if(str.toUpperCase().equals("NULL"))
			return "";
		else
			return str;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JTable#getCellRenderer(int, int)
	 */
	public TableCellRenderer getCellRenderer(int row, int column) 
	{
		return renderer;
	}
	
	//Implement table header tool tips.
//	= { "����(����������)", "����(�ڷ����ļ�/���Բ���)",
//			"���ɽ����(ͻ������ȱ��/�Ƿ�)","����CJEZB������","CJEZbDpMaxWk(ͻ��CJEZbDpMaxWk)","N������СMinWk(����ָ������)","��ƽ���ɽ���MAXWK","�߼���������(��ͨ��ֵ)"}
	;
//	{ "����", "����","�߼���������","���ɽ����","����CJEZB������","CJEDpMaxWk","����CJLZB������","��ƽ���ɽ���MAXWK"};
    protected JTableHeader createDefaultTableHeader() 
    {
        return new JTableHeader(columnModel) {
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                int realIndex = 
                        columnModel.getColumn(index).getModelIndex();
                return jtableTitleStringsTooltips[realIndex];
            }
        };
    }
    @Override
	public void BanKuaiGeGuMatchConditionValuesChanges(BanKuaiGeGuMatchCondition expc)
    {
    	((BanKuaiGeGuTableModelFromPropertiesFile)this.getModel()).setDisplayMatchCondition(expc);
    	this.repaint();
	}
	
    public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        StockOfBanKuai stkofbk = ((BanKuaiGeGuTableModelFromPropertiesFile)this.getModel()).getStock(rowIndex);
        if(colIndex == 0) { // highlight the zhangfu of the week
        	String period = ((BanKuaiGeGuBasicTableModel)this.getModel()).getCurDisplayPeriod();
        	LocalDate requireddate = ((BanKuaiGeGuTableModelFromPropertiesFile)this.getModel()).getShowCurDate ();
        	NodeXPeriodData nodexdata = stkofbk.getStock().getNodeXPeroidData(period);
        	Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
        	if(zhangdiefu != null) {
        		zhangdiefu = zhangdiefu * 100;
        		tip = "���Ƿ�" + zhangdiefu .toString() + "%";
        	}
        } else if(colIndex == 1) { //highlight when the stock was added to the bankuai
        	Set<Interval> timeinbk = stkofbk.getInAndOutBanKuaiInterval ();
        	String timett = "";
        	for(Interval tmpinterval : timeinbk ) {
        		DateTime newstartdt = tmpinterval.getStart();
				DateTime newenddt = tmpinterval.getEnd();
				
				LocalDate requiredstartday = LocalDate.of(newstartdt.getYear(), newstartdt.getMonthOfYear(), newstartdt.getDayOfMonth());
				LocalDate requiredendday = LocalDate.of(newenddt.getYear(), newenddt.getMonthOfYear(), newenddt.getDayOfMonth());
        		timett = "[" + requiredstartday + "~" + requiredendday + "]";
        	}
        	tip = timett;
        } else {
        	try {
        		tip =  getValueAt(rowIndex, colIndex).toString();
        	} catch ( java.lang.NullPointerException ex) {}
        }
//        try {
//        	if(colIndex == 2) { //Ȩ��column��tipҪ����
//				org.jsoup.nodes.Document doc = Jsoup.parse("");
//				org.jsoup.select.Elements content = doc.select("body");
//				content.append( "10~1 ��ռ��ҵӪ�ձ���<br>" );
//				content.append( "0 : Ӫ��ռ�ȼ���û�и���׶�<br>" );
//				content.append( "-1 : ���޹�ϵ<br>" );
//				
//				tip = doc.toString();
//        	} else
//        		tip = getValueAt(rowIndex, colIndex).toString();
//        } catch (RuntimeException e1) {
//            //catch null pointer exception if mouse is over an empty line
//        }

        return tip;
    }
	
    
    private  String toUNIXpath(String filePath) 
   	{
   		    return filePath.replace('\\', '/');
   	}	
}