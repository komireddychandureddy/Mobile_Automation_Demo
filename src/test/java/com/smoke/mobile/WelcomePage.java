package com.smoke.mobile;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.automation.controllers.BaseActions;

import io.appium.java_client.AppiumDriver;

public class WelcomePage {

	
	private AppiumDriver driver;
	BaseActions actions =new BaseActions();
	public WelcomePage (AppiumDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
		actions.app_driver = driver;	
	}
	
	@FindBy(id= "nav-link-accountList")
	WebElement link_account;
	@FindBy(id= "createAccountSubmit")
	WebElement btn_createNewAccount;
	
	
	public boolean gotoSignUp() {
		actions.isWebElementVisible(link_account, "Verify Account and list tab");
		actions.click(link_account, "Click on Account");
		actions.click(btn_createNewAccount, "Click on Create Account button");
		return true;
	}
	
	public boolean welcomePage() {
		return	actions.isWebElementVisible(link_account, "Verify Account and list tab in welcome page");	
	}
}
