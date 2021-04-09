package com.exchangeinfomanager.commonlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;

public class ExecutePythonScripts 
{
	public static List<String> executePythonScriptForExtraData(String scriptkeywords, BkChanYeLianTreeNode stock)  
	{
//		try {
//			Process p = Runtime.getRuntime().exec("C:/Users/Administrator.WIN7U-20140921O/Anaconda3/python E:/stock/stockmanager/thirdparty/python/execsrc/importdailyextradatafromtushare.py");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		SetupSystemConfiguration sysconfig = new SetupSystemConfiguration();
		String pythoninterpreter = sysconfig.getPythonInterpreter();
		ProcessBuilder processBuilder = null;
		String pyscriptname = null;
		scriptkeywords = scriptkeywords.toUpperCase();
		switch (scriptkeywords) {
		case "EXTRADATAFROMTUSHARE":
			pyscriptname = sysconfig.getPythonScriptsPath () + "importdailyextradatafromtushare.py";//"E:/stock/stockmanager/thirdparty/python/execscripts/importdailyextradatafromtushare.py"; //
			processBuilder = new ProcessBuilder(pythoninterpreter,pyscriptname );
			break;
		case "SHAREHOLDER" :
			pyscriptname = sysconfig.getPythonScriptsPath () + "importshareholder.py";// "E:/stock/stockmanager/thirdparty/python/execscripts/importshareholder.py";
			processBuilder = new ProcessBuilder(pythoninterpreter,pyscriptname );
			break;
		case "FORCETOREFRESHSHAREHOLDER" :
			pyscriptname = sysconfig.getPythonScriptsPath () + "refreshStockGuDong.py " ;// "E:/stock/stockmanager/thirdparty/python/execscripts/importshareholder.py";
			processBuilder = new ProcessBuilder(pythoninterpreter,pyscriptname , stock.getMyOwnCode());
			break;
		}
		if(processBuilder == null)
			return null;
		
	    processBuilder.redirectErrorStream(true);
	    List<String> execresult = new ArrayList<> ();
	    try {
			Process process = processBuilder.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ( (line = in.readLine()) != null)
				execresult.add(line);
			
		} catch (IOException e1) {e1.printStackTrace();	}
	    
	    return execresult;
	}
}
