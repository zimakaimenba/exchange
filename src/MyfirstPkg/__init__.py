import mysql.connector 
import tushare as ts
import time
from gevent.libev.corecext import NONE
import os.path
from os import path

pro = ts.pro_api()
ts.set_token('283f836520a56728f59ef0d345871fcaaab4380315c1da4aeecf3b34')


querystarttime = '20140101'
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

query = ("SELECT 股票名称, 股票代码, 所属交易所, 已退市 FROM A股 ")
try:
    cursor = cnx.cursor()
    cursor.execute(query)
    for(股票名称, 股票代码, 所属交易所, 已退市)  in cursor:
     if 已退市 != 1 and 股票代码 != None:
        #print("{} {} {} {}".format(股票名称, 股票代码, 所属交易所, 已退市 ) )
        retry_count = 3
        sucess_count =0
        stockcode =  股票代码  + '.' + 所属交易所
        print(stockcode)
        csvfilename = 'E:/stock/stockmanager/dailydata/tushare/shareholder/' + 股票代码 + '_floatholders.csv'
        if(path.exists(csvfilename) != 1) :
            while(sucess_count != 1) :
                try:
                    df = pro.top10_floatholders(ts_code = stockcode, start_date = querystarttime, end_date = queryendtime)
                    #print(df)
                    df.to_csv(csvfilename)
                    sucess_count +=   1
                    break
                except:
                    time.sleep(35)
        #df.info()
        csvfilename = 'E:/stock/stockmanager/dailydata/tushare/shareholder/' + 股票代码 + '_top10holders.csv'
        if(path.exists(csvfilename) != 1) :
            while(sucess_count != 2) :
                try:
                    df = pro.top10_holders(ts_code = stockcode, start_date = querystarttime, end_date = queryendtime)
                    #print(df)
                    df.to_csv(csvfilename)
                    sucess_count +=   1
                    time.sleep(2)
                    break
                except:
                    time.sleep(10)
        
    print("got all data ")        
finally:
    cnx.close()
