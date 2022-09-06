package com.automation.listeners;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;

import org.codehaus.plexus.util.ExceptionUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.automation.controllers.BaseActions;
import com.automation.controllers.DriverFactory;
import com.automation.utils.ConfigReader;
import com.automation.utils.ExtentReportsUtil;
import com.automation.utils.LogUtil;
import com.automation.utils.SendMail;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;



/**
 * @Author Chandu
 * @Date 27-Aug-2022
 */

public class CustomListener extends DriverFactory implements ITestListener
{

	private Process process;
	private String getTestMethodName(ITestResult iTestResult) {
        return iTestResult.getMethod().getConstructorOrMethod().getName();
    }
	BaseActions actions = new BaseActions();
    @Override
    public void onStart(ITestContext iTestContext) {
    	ExtentReportsUtil.getReporter();
    	ExtentReportsUtil.getLogger();
    	String wadServerPath = "C:\\Program Files (x86)\\Windows Application Driver\\WinAppDriver.exe";
		ProcessBuilder builder = new ProcessBuilder(wadServerPath).inheritIO();
		try {
			 process = builder.start();
			 LogUtil.infoLog(this.getClass(), "Started WinAppDriver process" );
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	LogUtil.infoLog(getClass(), "I am in onStart method " + iTestContext.getName());
        //System.out.println("I am in onStart method " + iTestContext.getName());
        //iTestContext.setAttribute("WindowsDriver", getWinDriver());
       iTestContext.setAttribute("WebDriver", getDriver());
        ConfigReader.environmentSetup();
        LogUtil.infoLog(this.getClass(), "Updated environment details at /src/main/resources/environment.properties" );
   		
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        LogUtil.infoLog(getClass(), "I am in onFinish method " + iTestContext.getName());
        //Do tier down operations for extentreports reporting!
        //ExtentReportsUtil.endTest();
        ExtentReportsUtil.flush();
        LogUtil.infoLog(getClass(), "Report closed ");
        
        if(!(getDriver()==null)){
        	getDriver().quit();
        }
      
        
        //String htmlReportFile = PWD+  ConfigReader.getValue("HtmlReportFullPath");
        String htmlReportFile = PWD+       "/target/Spark/Spark.html";
   
		File f = new File(htmlReportFile);
		if (f.exists()) {

			try {
				Runtime.getRuntime()
						.exec("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe \"" + htmlReportFile
								+ "\"");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		process.destroy();
		LogUtil.infoLog(this.getClass(), "Destroyed AppDriver process" );
		 if (ConfigReader.getValue("sendMail").equalsIgnoreCase("Y"))
				try {
					SendMail.sendEmailToClient();
					LogUtil.infoLog(getClass(), "Report mail sent to participants");
				} catch (IOException e1) {
					actions.logStepFail(e1.getMessage());
					e1.printStackTrace();
				} catch (MessagingException e1) {
					actions.logStepFail(e1.getMessage());
					e1.printStackTrace();
				}
			
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {
    	LogUtil.infoLog(getClass(), "Testcase started: "+getTestMethodName(iTestResult) );

        ExtentReportsUtil.startTest(iTestResult.getMethod().getMethodName(),iTestResult.getMethod().getDescription());
       // String description=iTestResult.getMethod().getDescription();
        actions.logStep("Test : "+iTestResult.getMethod().getMethodName());
    
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
    	
    	String testName = getTestMethodName(iTestResult);
    	//Extentreports log operation for passed tests.
    	//logStepPass("Test passed : "+iTestResult.getMethod().getMethodName());
        ExtentReportsUtil.getTest().log(Status.PASS, "Test passed : "+iTestResult.getMethod().getMethodName());
      //  ExtentReportsUtil.getTest().setEndedTime(new Date());
        getDriver().close();
        
    }

    @Override
    public void onTestFailure(ITestResult iTestResult)  {
    	
    	String testName = getTestMethodName(iTestResult);
    	// ExtentReportsUtil.getTest().setEndedTime(new Date());
    	 String  ErrorMsg=ExceptionUtils.getFullStackTrace(iTestResult.getThrowable());
    	 actions.logStepFail(ErrorMsg);
        //Take base64Screenshot screenshot.
        String base64Screenshot = "data:image/png;base64,"+((TakesScreenshot)getDriver()). getScreenshotAs(OutputType.BASE64);

        //Extentreports log and screenshot operations for failed tests.
        
        String path =actions.takeScreenshot(getDriver(), testName);
        
        ExtentReportsUtil.getTest().log(Status.FAIL,"Test Failed : "+iTestResult.getMethod().getMethodName());
        //ExtentReportsUtil.getTest().fail(Status.FAIL, addScreenCaptureFromPath(path));
        ExtentReportsUtil.getTest().fail(MediaEntityBuilder.createScreenCaptureFromPath(path).build());

     // base64
        ExtentReportsUtil.getTest().fail(MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
       // ExtentReportsUtil.getTest().addScreenCapture(actions.screenshot_path));
        
        //actions.logStepFail("Test failed : "+iTestResult.getMethod().getMethodName());
        
        
	 getDriver().close();
		
  
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
    	LogUtil.infoLog(getClass(), "Test Skipped"+getTestMethodName(iTestResult));
       
       ExtentReportsUtil.getTest().log(Status.SKIP, "Test Skipped : "+getTestMethodName(iTestResult));
       
    }

   
}
