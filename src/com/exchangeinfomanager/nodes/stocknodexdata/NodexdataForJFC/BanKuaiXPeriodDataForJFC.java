package com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC;

import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.TDXNodesXPeriodExternalData;



public class BanKuaiXPeriodDataForJFC extends TDXNodesXPeriodDataForJFC 
{
		public BanKuaiXPeriodDataForJFC (String nodecode, String nodeperiodtype1)
		{
			super(nodecode,nodeperiodtype1);
		}

		@Override
		public Boolean hasFxjgInPeriod(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}

//		@Override
//		public Double getSpecificTimeHuanShouLv(LocalDate requireddate, int difference) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Double getSpecificTimeZongShiZhi(LocalDate requireddate, int difference) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Double getSpecificTimeHighestZhangDieFu(LocalDate requireddate, int difference) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Double getSpecificTimeLowestZhangDieFu(LocalDate requireddate, int difference) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public Double getSpecificTimeLiuTongShiZhi(LocalDate requireddate, int difference) {
//			// TODO Auto-generated method stub
//			return null;
//		}

		
		
}