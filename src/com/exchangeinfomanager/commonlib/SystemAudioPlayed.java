package com.exchangeinfomanager.commonlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;


public class SystemAudioPlayed 
{
	protected static SetupSystemConfiguration sysconfig;

	public static synchronized void playSound() 
	{
		  new Thread(new Runnable()
		  {
		  // The wrapper thread is unnecessary, unless it blocks on the
		  // Clip finishing; see comments.
		    public void run()
		    {
		      sysconfig = new SetupSystemConfiguration();
		      try {
		    	final String audiofilepath = sysconfig.getSystemAudioPlayed();
//		    	URL url = this.getClass().getClassLoader().getResource( audiofilepath );
		    	
		    	File audioFile = new File(audiofilepath);
		    		    	
		        Clip clip = AudioSystem.getClip();
		        clip.addLineListener(event -> {
		            if(LineEvent.Type.STOP.equals(event.getType())) {
		                clip.close();
		            }
		        });
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
