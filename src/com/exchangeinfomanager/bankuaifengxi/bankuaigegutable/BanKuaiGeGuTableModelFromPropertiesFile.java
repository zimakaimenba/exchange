package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.google.common.base.Splitter;

public class BanKuaiGeGuTableModelFromPropertiesFile extends BanKuaiGeGuBasicTableModel 
{
	BanKuaiGeGuTableModelFromPropertiesFile (Properties prop) 
	{
		super (prop);
	}

	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableModelFromPropertiesFile.class);
}

