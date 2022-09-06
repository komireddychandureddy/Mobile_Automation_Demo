package com.automation.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ViewName;


public class ExtentReportsUtil {
    static Map extentTestMap = new HashMap();
    //static ExtentReports extent = ExtentManager.getReporter();
    static ExtentReports extent ;
    static Logger logger ;
	public static Logger getLogger() {
	
	 logger=Logger.getLogger("Report");

    // configure log4j properties file
     PropertyConfigurator.configure("Log4j.properties");
    // logger.info("Logger started");
     return logger;
	}
	

	    public static ExtentReports getReporter() {
	        extent = new ExtentReports();
	        ExtentSparkReporter spark = new ExtentSparkReporter("target/Spark/Spark.html")
	        		.viewConfigurer()
	        .viewOrder()
	        .as(new ViewName[] { 
	    	   ViewName.DASHBOARD, 
	    	   ViewName.TEST, 
	    	   //ViewName.TAG, 
	    	   ViewName.AUTHOR, 
	    	   ViewName.DEVICE, 
	    	   ViewName.EXCEPTION, 
	    	   ViewName.LOG 
	    	})
	      .apply();
	        extent.attachReporter(spark);
	        
	        extent.setSystemInfo("os", "Windows Chandu");
	       
	       // extent.a
	        return extent;
	    }
	    
	    public static void flush() {
	    	extent.flush();
	    }
	
	 
	    public static synchronized ExtentTest getTest() {
	        return (ExtentTest)extentTestMap.get((int) (long) (Thread.currentThread().getId()));
	    }
	 
		/*
		 * public static synchronized void endTest() {
		 * extent.endTest((ExtentTest)extentTestMap.get((int) (long)
		 * (Thread.currentThread().getId())));
		 * 
		 * }
		 */
	 
	    public static synchronized ExtentTest startTest(String testName, String desc) {
	        ExtentTest test = extent.createTest(testName, desc);
	        extentTestMap.put((int) (long) (Thread.currentThread().getId()), test);
	        return test;
	    }
	    public static synchronized void stepInfo(String stepName) {
	    	 getTest().log(Status.INFO, "Test Info : "+stepName);
	    }
	    public static void stepSkip(String stepName) {
	  	  getTest().log(Status.SKIP, "Test Skipped : "+stepName);
	  }
	    public static void stepPass(String stepName) {
	    	getTest().log(Status.PASS, "Test Pass : "+stepName);
	  }
	    public static void stepFail(String stepName) {
	    	getTest().log(Status.FAIL, "Test Fail : "+stepName);
	  }

	  public static void stepWarning(String stepName) {
		  getTest().log(Status.WARNING, "Test Warning : "+stepName);
	}
}
