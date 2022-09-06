package com.smoke.mobile;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.automation.controllers.BaseActions;

import io.appium.java_client.AppiumDriver;

public class RegistrationPage {

	private AppiumDriver driver;
	BaseActions actions =new BaseActions();
	public RegistrationPage (AppiumDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
		actions.app_driver = driver;	
	}
	
	@FindBy(id= "ap_customer_name")
	WebElement input_name;
	@FindBy(id= "ap_email")
	WebElement input_email;
	@FindBy(id= "ap_password")
	WebElement input_password;
	@FindBy(id= "ap_password_check")
	WebElement input_confirmPassword;
	
	
	@FindBy(id= "continue")
	WebElement btn_continue;
	
	
	@FindBy(id= "home_children_button")
	WebElement btn_solvePuzzle;
	
	
	public boolean registerNewUser(String name, String email, String password, String confirmPassword) {
		actions.inputText(input_name, name,"Enter name: "+name);
		actions.inputText(input_email, email,"Enter email: "+email);
		actions.inputText(input_password, password,"Enter password: "+password);
		actions.inputText(input_confirmPassword, confirmPassword,"Enter confirm password: "+confirmPassword);
		actions.click(btn_continue, "Click on Continue button");		
		return true;
	}
		
	public boolean verifyPuzzle() {
		
		return actions.isWebElementVisible(btn_solvePuzzle, "Verify solve Puzzle button is displayed");
	}
}
