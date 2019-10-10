package com.exchangeinfomanager.commonlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class SystemAudioPlayed 
{
	protected static SystemConfigration sysconfig;

	public static synchronized void playSound() 
	{
		  new Thread(new Runnable()
		  {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		    public void run()
		    {
		      sysconfig = SystemConfigration.getInstance();
		      try {
		    	final String audiofilepath = sysconfig.getSystemAudioPlayed();
//		    	URL url = this.getClass().getClassLoader().getResource( audiofilepath );
		    	
		    	File audioFile = new File(audiofilepath);
		    		    	
		        Clip clip = AudioSystem.getClip();
		        AudioInputStream inputStream =  AudioSystem.getAudioInputStream( audioFile );
		        clip.open(inputStream);
		        clip.start(); 
		    	
		    	
		      } catch (Exception e) {
		    	  e.printStackTrace();
		        System.err.println(e.getMessage());
		      }
		    }
		  }).start();
		}

}
