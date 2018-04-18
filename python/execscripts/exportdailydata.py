# -*- coding: utf-8 -*-
#coding:utf-8
#!/usr/bin/python3

import tushare as ts
import pymysql
import os
import shutil

# 打开数据库连接
db = pymysql.connect("localhost","root","987654321","stockinfomanagementtest",charset="utf8" )
 
# 使用cursor()方法获取操作游标 
cursor = db.cursor()
 
# SQL 查询语句
sql = "SELECT * FROM a股"
       
try:
   # 执行SQL语句
   cursor.execute(sql)
   # 获取所有记录列表
   results = cursor.fetchall()
   for row in results:
      stockname = row[1]
      stockcode =  row[2]
       # 打印结果
      df = ts.get_hist_data(stockcode) #一次性获取全部日k线数据
      
      execpath = os.path.realpath(__file__)
      #print("cur path is" + execpath)
      fatherpath = os.path.abspath( os.path.join(execpath,os.path.pardir)  )
      #print("father path is " + fatherpath)
      fatherpath2 = os.path.abspath( os.path.join(fatherpath,os.path.pardir)  )
      #print("father2 path is " + fatherpath2)
       
      outputpath = fatherpath2 + '\execexports\\'
      outputfile = outputpath +  stockcode + '.csv'
      #print (outputfile)
      if df is not None:
        df.to_csv(outputfile)
      
except Exception as ex:
    print(ex)
 
# 关闭数据库连接
db.close()


