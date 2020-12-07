package com.exchangeinfomanager.bankuaifengxi;

import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JPanel;

import com.google.common.base.Strings;

public class HighLightSevice  
{
	private BanKuaiGeGuMatchCondition condition;
	private String systeminstalledpath;
	private Properties prop;
	private String properitesfile;

	public HighLightSevice (BanKuaiGeGuMatchCondition bkcondition, String properitesfile1) 
	{
		this.condition = bkcondition;
		this.properitesfile = properitesfile1;
		
		setPropertiesInfo ();
	}
	
	private void setPropertiesInfo ()
	{

		File directory = new File("");//设定为当前文件夹
		try{
//		    logger.debug(directory.getCanonicalPath());//获取标准的路径
//		    logger.debug(directory.getAbsolutePath());//获取绝对路径
//		    this.systeminstalledpath = toUNIXpath(directory.getCanonicalPath()+ "\\");
		    Properties properties = System.getProperties();
		    this.systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //用户运行程序的当前目录
		} catch(Exception e) {
			System.exit(0);
		}
		
		String propxmlFileName = null ;
		try {
			prop = new Properties();
			String propFileName = this.systeminstalledpath  + this.properitesfile ; //"/config/bankuaifenxihighlightsetting.properties"
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} 
			inputStream.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	private boolean dyjx_status;
	private void setPropertiesToCondition ()
	{
		String dayujunxian_status  = prop.getProperty ("Dayujunxian_status");
		String dayujunxian  = prop.getProperty ("Dayujunxian");
		if(dayujunxian_status != null && dayujunxian_status.trim().toUpperCase().equals("SELECTED")) {
			this.dyjx_status = true;
			this.condition.setSettingMaFormula(dayujunxian);
		} else 
			this.condition.setSettingMaFormula(null);

		
		if( chbxzhangfu.isSelected() ) {
			String showzfmin; String showzfmax;
			if( !Strings.isNullOrEmpty(tfldzhangfumin.getText()) ) {
				showzfmin =  tfldzhangfumin.getText() ;
				expc.setSettingZhangFuMin (Double.parseDouble(showzfmin) );
			} else {
				expc.setSettingZhangFuMin (Double.parseDouble(null) );
			}
			
			if( !Strings.isNullOrEmpty(tfldzhangfumax.getText()) ) {
				showzfmax =  tfldzhangfumax.getText() ;
				expc.setSettingZhangFuMax (Double.parseDouble(showzfmax) );
			} else {
				expc.setSettingZhangFuMax (null );
			}
		} else {
			expc.setSettingZhangFuMax (null );
			expc.setSettingZhangFuMin (null );
		}

			
		if(ckbxma.isSelected() ) 
			expc.setSettingMaFormula(tfldweight.getText());
		else
			expc.setSettingMaFormula(null);
	
		if(ckbxcjemaxwk.isSelected() ) 
			expc.setSettingChenJiaoErMaxWk(  Integer.parseInt( tfldcjemaxwk.getText()) );
		else
			expc.setSettingChenJiaoErMaxWk(null);
		
		if(ckbxdpmaxwk.isSelected() ) 
			expc.setSettingDpMaxWk(Integer.parseInt( tflddisplaydpmaxwk.getText() ) );
		else
			expc.setSettingDpMaxWk(null );
		
		if(chckbxdpminwk.isSelected() ) {
			expc.setSettingDpMinWk(Integer.parseInt(tflddpminwk.getText())  );
		} else
			expc.setSettingDpMinWk (null);
		if(ckbxhuanshoulv.isSelected())
			expc.setSettingHuanShouLv(Double.parseDouble( tfldhuanshoulv.getText() ) );
		else 
			expc.setSettingHuanShouLv( null );
		
		if(chkliutongsz.isSelected()) {
			Double showltszmin;Double showltszmax;
			if( !Strings.isNullOrEmpty(tfldltszmin.getText()) ) {
				showltszmin =  Double.parseDouble( tfldltszmin.getText() );
			} else
				showltszmin = null;
			
			if( !Strings.isNullOrEmpty(tfldltszmax.getText()) ) {
				showltszmax = Double.parseDouble( tfldltszmax.getText() );
			} else
				showltszmax = null;
			
			expc.setSettingLiuTongShiZhiMin(showltszmin );
			expc.setSettingLiuTongShiZhiMax(showltszmax );
		} else {
			expc.setSettingLiuTongShiZhiMin(null );
			expc.setSettingLiuTongShiZhiMax(null );
		}

		if(chbxquekou.isSelected()) {
			expc.setHuiBuDownQueKou(true);
			expc.setZhangTing(true);
		} else {
			expc.setHuiBuDownQueKou(false);
			expc.setZhangTing(false);
		}

	}
	
	private  String toUNIXpath(String filePath) 
	{
	    return filePath.replace('\\', '/');
	}
}
