package com.exchangeinfomanager.bankuaifengxi.ai.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingWorker;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;

//https://hacpai.com/article/1543410156640
//https://www.lagou.com/lgeduarticle/17080.html
//https://my.oschina.net/fangliwen/blog/60041
public class VoiceEngine extends SwingWorker<Integer, String>
{
	private SystemConfigration sysconfig;
	private String infoneededtoread;

	public VoiceEngine (String readinfo)
	{
		this.sysconfig = SystemConfigration.getInstance();
		this.infoneededtoread = readinfo;
	}
	
	@Override
	protected Integer doInBackground() throws Exception 
	{
		if (isCancelled()) 
			 return null;

		if(!Strings.isNullOrEmpty(this.infoneededtoread))
			this.readInformationByWinPowerShell (this.infoneededtoread);
		
//		readInformationByDirectlyCallJacob ();
		
		return 100;
	}
	/*
	 * https://github.com/profesorfalken/jPowerShell?utm_source=hacpai.com
	 */
	public void readInformationByWinPowerShell (String info)
	{
//		File script = createPowerShellScirptFile ("���Բ��Բ��Բ���");
//		String powershell = script.getAbsolutePath();
		String powershell = this.sysconfig.getPowerShellScriptsPath() + "readstockanalysisscript.ps1";
		PowerShell powerShell = null;
		try  {
			 powerShell = PowerShell.openSession();
		       //Increase timeout to give enough time to the script to finish
		       Map<String, String> config = new HashMap<String, String>();
		       config.put("maxWait", "20000");
		       
		       //Execute script
		        String gb2312 = null;
				try {
					gb2312 = new String(info.getBytes("utf-8"), "gbk");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       
		       PowerShellResponse  response = powerShell.configuration(config).executeScript(powershell,gb2312 );
		       //Print results if the script
//		       System.out.println("Script output:" + response.getCommandOutput());
		} catch(PowerShellNotAvailableException ex) {
		       //Handle error when PowerShell is not available in the system
		       //Maybe try in another way?
			   ex.printStackTrace();
		} finally {
		    //Always close PowerShell session to free resources.
		    if (powerShell != null)
		        powerShell.close();
		}
		
		return;
	}
	
	private File createPowerShellScirptFile (String info)
	{
		Charset charset = Charset.forName("GBK") ;
		File outputfile = null;
		try {
			outputfile = File.createTempFile("voiceforstock", ".ps1");
			Files.append("# PowerShell����"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("# ��� System.speech.dll ����"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("Add-Type -AssemblyName System.speech"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("# ���� SpeechSynthesizer ����"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append(""+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("$syn=New-Object System.Speech.Synthesis.SpeechSynthesizer"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("#�����ʶ�������"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("$syn.Volume=80"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append(""+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("#�����ʶ�������"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("$syn.Rate=0"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("$syn.Speak(\" " + info + "\")"+ System.getProperty("line.separator") ,outputfile, charset);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outputfile;
	}

	protected void readInformationByDirectlyCallJacob()  
	{
		ActiveXComponent ax = null;
		
		try {
			ax = new ActiveXComponent("Sapi.SpVoice");
			Dispatch spVoice = (Dispatch) ax.getObject();
			
			ax = new ActiveXComponent("Sapi.SpFileStream");
			Dispatch spFileStream = (Dispatch) ax.getObject();
			
			ax = new ActiveXComponent("Sapi.SpAudioFormat");
			Dispatch spAudioFormat = (Dispatch) ax.getObject();
			
			//������Ƶ����ʽ
			Dispatch.put(spAudioFormat, "Type", new Variant(23));
			
			//�����ļ��������ʽ
			Dispatch.putRef(spFileStream, "Format", spAudioFormat);
			//������� �ļ����򿪷���������һ��.wav�ļ�
			Dispatch.call(spFileStream, "Open", new Variant("F:\\test.wav"), new Variant(3), new Variant(true));
			//���������������Ƶ�����Ϊ����ļ�����
			Dispatch.putRef(spVoice, "AudioOutputStream", spFileStream);
			//�������� 0��100
			Dispatch.put(spVoice, "Volume", new Variant(100));
			//�����ʶ��ٶ�
			Dispatch.put(spVoice, "Rate", new Variant(-2));
			//��ʼ�ʶ�
			Dispatch.call(spVoice, "Speak", new Variant("����������"));
			
			//�ر�����ļ�
			Dispatch.call(spFileStream, "Close");
			Dispatch.putRef(spVoice, "AudioOutputStream", null);
			
//			spAudioFormat.safeRelease();
//			spFileStream.safeRelease();
//			spVoice.safeRelease();
//			ax.safeRelease();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
