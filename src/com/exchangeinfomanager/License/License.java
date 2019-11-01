package com.exchangeinfomanager.License;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;

//https://stackoverflow.com/questions/8462498/how-to-determine-internet-network-interface-in-java
public class License 
{
	private SystemConfigration sysconf;

	public License ()
	{
		sysconf = SystemConfigration.getInstance();
	}
	
	public Boolean isLicenseValide ()
	{
		try {
//			System.out.println("Ip: " + GetNetworkAddress.GetAddress("ip"));
//			System.out.println("Mac: " + GetNetworkAddress.GetAddress("mac"));
			
			String curhardwareMac = GetNetworkAddress.GetAddress("mac");
			String sysmacsetting = sysconf.getLinceseMac ();
			if(sysmacsetting.equals(curhardwareMac))
				return true;
			else 
				return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}
