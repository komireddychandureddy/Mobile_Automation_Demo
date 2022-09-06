/**
 * 
 */
package com.automation.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.codehaus.plexus.util.ExceptionUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import com.automation.utils.ConfigReader;
import com.automation.utils.ExtentReportsUtil;
import com.automation.utils.LogUtil;
import com.aventstack.extentreports.Status;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;

/**
 * @Author Chandu
 * @Date 27-08-2022
 */
public class DriverFactory extends ExtentReportsUtil
{
	//Enable for parallel test and enable AfterTest method
	//public ThreadLocal<AppiumDriver> wd = new ThreadLocal<AppiumDriver>();
	
	//Enable for sequence tests and enable code in ITest Pass and Failed listener
	public  static ThreadLocal<AppiumDriver> wd = new ThreadLocal<AppiumDriver>();
	//public AppiumDriver<MobileElement> wd_driver = null;
	
	public String PWD = System.getProperty("user.dir");
		
		@BeforeMethod
		public void beforeMethod(ITestContext context) throws Exception
		{		
			String platform=ConfigReader.getValue("Platform");
			String app=ConfigReader.getValue("Appium_App");
			String bundledId=ConfigReader.getValue("bundledId");
			LogUtil.infoLog(getClass(), "Platform: "+platform+"  Appium_App: "+app);
			
			setDriver(createDriver(platform,app, bundledId,ConfigReader.getValue("UDID")));	
			context.setAttribute("WebDriver", getDriver());
		}

	public void setDriver(AppiumDriver driver) 
	{
		wd.set(driver);
		
	}

	public AppiumDriver getDriver() 
	{
		return wd.get();
	}
	/*
	  public void setDriver(AppiumDriver driver)
	  {   
		  wd_driver=driver; 
	  }
	  
	  public AppiumDriver<MobileElement> getDriver() 
	  { 
		  return wd_driver;
	  }
	 */
	

	//@AfterMethod
	//@AfterMethod
		public void afterMethod(ITestResult iTestResult) 
		{
			String testName = iTestResult.getMethod().getConstructorOrMethod().getName();
			//getTest().setEndedTime(new Date());
			if(iTestResult.isSuccess()) {
			
	    	//Extentreports log operation for passed tests.
				ExtentReportsUtil.getTest().log(Status.PASS, "Test passed : "+iTestResult.getMethod().getMethodName());
	    	 LogUtil.infoLog(getClass(),"Test passed : "+iTestResult.getMethod().getMethodName());
			}
	        else {
	        	
	       	// ExtentReportsUtil.getTest().setEndedTime(new Date());
	       	 String  ErrorMsg=ExceptionUtils.getFullStackTrace(iTestResult.getThrowable());
	       	 
	           //Take base64Screenshot screenshot.
	           String base64Screenshot = "data:image/png;base64,"+((TakesScreenshot)getDriver()). getScreenshotAs(OutputType.BASE64);
	          // ExtentReportsUtil.getTest().addBase64ScreenShot(base64Screenshot);
	           //Extentreports log and screenshot operations for failed tests.
	           ExtentReportsUtil.getTest().log(Status.FAIL,"Test Failed : "+iTestResult.getMethod().getMethodName());
	           ExtentReportsUtil.getTest().addScreenCaptureFromBase64String(base64Screenshot);
	           LogUtil.errorLog(getClass(), ErrorMsg,iTestResult.getThrowable());
	       	
	           
	        }
	        getDriver().close();
        
    }
  

	public AppiumDriver createDriver(String platform, String app, String iOS_bundledId, String udid) throws Exception 
	{
		AppiumDriver driver = null;

		DesiredCapabilities capabilities;
		switch(platform.toLowerCase())
		{
		case "android" :
				capabilities = new DesiredCapabilities();
				capabilities.setCapability(MobileCapabilityType.APP,app);
				capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UIAutomator2");
				capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
				capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "12");
				capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Vivo20Pro");
				capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
				capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 120);
				/*
				capabilities.setCapability("appPackage", "com.android.settings");
				capabilities.setCapability("appActivity", ".MainActivity, .Settings");
				capabilities.setCapability("appWaitPackage", "com.android.settings");				
				capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
				capabilities.setCapability(MobileCapabilityType.AUTO_WEBVIEW, false);
				capabilities.setCapability(MobileCapabilityType.CLEAR_SYSTEM_FILES, true);
				*/

			try {
				driver = new AppiumDriver(new URL("http://127.0.0.1:4723"), capabilities);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
				
			break;
			
		case "ios":
			capabilities = new DesiredCapabilities();
			if(app.isEmpty())
			{
			capabilities.setCapability("bundledId", iOS_bundledId);
			}
			capabilities.setCapability(MobileCapabilityType.APP,app);
			capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUIT");
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.IOS);
			capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "15");
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone");
			capabilities.setCapability(MobileCapabilityType.UDID, udid);
			capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
			capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 120);
			//capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
			//capabilities.setCapability(MobileCapabilityType.AUTO_WEBVIEW, false);
			//capabilities.setCapability(MobileCapabilityType.CLEAR_SYSTEM_FILES, true);
			try {
				driver = new AppiumDriver(new URL("http://127.0.0.1:4723"), capabilities);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			break;


		default:
			throw new Exception("Please Provide a Valid OS name");
		}
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
			LogUtil.infoLog(getClass(), " App: "+app+" application launched succefully: ");
		return driver;		
	}
}
