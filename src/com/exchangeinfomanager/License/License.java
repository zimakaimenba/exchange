package com.exchangeinfomanager.License;

import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;


//https://stackoverflow.com/questions/8462498/how-to-determine-internet-network-interface-in-java
public class License 
{
	private SetupSystemConfiguration sysconf;

	public License ()
	{
		sysconf = new SetupSystemConfiguration();
	}
	
	public Boolean isLicenseValide ()
	{
		try {
		
			String curhardwareMac = GetNetworkAddress.GetAddress("mac");
			
			String sysmacsetting = sysconf.getLinceseMac ();
			sysmacsetting = (new EncryptAndDecypt()).getDecryptedPassowrd(sysmacsetting);
			
			if(sysmacsetting.equals(curhardwareMac))
				return true;
			else 
				return false;
		} catch (Exception e) {
//			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean isAdminPwvalide (String testpw)
	{
		if(testpw.equals("987654321") )
			return true;
		else
			return false;
	}
}
