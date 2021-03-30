import mysql.connector 
import tushare as ts
import time
from gevent.libev.corecext import NONE
import os.path
from os import path
from datetime import date, timedelta

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

query = ("SELECT 代码, 交易日期    FROM 通达信板块每日交易信息   WHERE 代码 = '999999' AND 交易日期 BETWEEN '2015-01-01' AND '2022-01-01'")
try:
    cursor = cnx.cursor()
    cursor.execute(query)
    for(代码, 交易日期)  in cursor:
        print(" {} {}".format(代码, 交易日期 ) )
        retry_count = 3
        sucess_count =0
        
        csvfilename = 'E:/stock/stockmanager/dailydata/tushare/dailyexchangedata/' + 交易日期.strftime("%Y%m%d") + 'dailyexchangedata.csv'
        if(path.exists(csvfilename) != 1) :
            while(sucess_count != 1) :
                try:
                    df = pro.daily_basic(ts_code='', trade_date= 交易日期.strftime("%Y%m%d"), fields='ts_code,trade_date,turnover_rate,turnover_rate_f,volume_ratio,pe,pe_ttm,pb,total_share,float_share,free_share,total_mv,circ_mv,dv_ratio,dv_ttm')
                    #print(df)
                    df.to_csv(csvfilename)
                    sucess_count +=   1
                    break
                except Exception as ex:
                    print(ex)
                    time.sleep(35)
        #df.info()
        
        
    print("got all data ")        
finally:
    cnx.close()
