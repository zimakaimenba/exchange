import mysql.connector 
import tushare as ts
import time
from gevent.libev.corecext import NONE
import os.path
from os import path
from datetime import date, timedelta, datetime

pro = ts.pro_api()
ts.set_token('283f836520a56728f59ef0d345871fcaaab4380315c1da4aeecf3b34')


querystarttime = '20190101'
queryendtime = '20211231'

# Connect
config = {
  'user': 'root',
  'password': '987654321',
  'host': '127.0.0.1',
  'database': 'stockinfomanagementtest',
  'raise_on_warnings': True
}
cnx = mysql.connector.connect(** config)

query = ("SELECT MIN(交易日期)  minjytime , MAX(交易日期) maxjytime \r\n" + 
                "FROM 通达信上交所股票每日交易信息\r\n" + 
                "WHERE \r\n" + 
                "  自由流通换手率 IS NOT NULL ")
try:
    cursor = cnx.cursor()
    cursor.execute(query)
    for( maxjytime)  in cursor:
        print("  {}".format( maxjytime ) )
        maxjydate = datetime.strptime(maxjytime, "%Y%m/%d")
    retry_count = 3
    sucess_count =0
    
    output_date = maxjydate + datetime.timedelta(days=1)
    while ( output_date.date() < datetime.now().date() ) :   
        csvfilename = 'E:/stock/stockmanager/dailydata/tushare/dailyexchangedata/' + output_date.strftime("%Y%m%d") + 'dailyexchangedata.csv'
        if(path.exists(csvfilename) != 1) :
            while(sucess_count != 1) :
                try:
                    df = pro.daily_basic(ts_code='', trade_date= output_date.strftime("%Y%m%d"), fields='ts_code,trade_date,turnover_rate,turnover_rate_f,volume_ratio,pe,pe_ttm,pb,total_share,float_share,free_share,total_mv,circ_mv,dv_ratio,dv_ttm')
                    if df.shape[0] != 0:
                        df.to_csv(csvfilename)
                    sucess_count +=   1
                    break
                except Exception as ex:
                    print(ex)
                    time.sleep(35)
        output_date = output_date + datetime.timedelta(days=1)

    print("got all data ")        
finally:
    cnx.close()
