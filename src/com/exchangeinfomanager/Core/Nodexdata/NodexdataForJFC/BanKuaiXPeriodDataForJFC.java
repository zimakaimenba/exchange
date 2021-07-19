package com.exchangeinfomanager.Core.Nodexdata.NodexdataForJFC;

import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.TDXNodesXPeriodExternalData;



public class BanKuaiXPeriodDataForJFC extends TDXNodesXPeriodDataForJFC 
{
		public BanKuaiXPeriodDataForJFC (String nodecode, String nodeperiodtype1)
		{
			super(nodecode,nodeperiodtype1);
		}

		@Override
		public Boolean hasFxjgInPeriod(LocalDate requireddate) {
			// TODO Auto-generated method stub
			return null;
		}
}