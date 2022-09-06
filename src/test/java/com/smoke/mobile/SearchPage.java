package com.smoke.mobile;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.PageFactory;

import com.automation.controllers.BaseActions;

import io.appium.java_client.AppiumDriver;

public class SearchPage {

	private AppiumDriver driver;
	BaseActions actions =new BaseActions();
	public SearchPage (AppiumDriver driver){
		this.driver=driver;
		PageFactory.initElements(driver, this);
		actions.app_driver = driver;	
	}
	
	@FindBy(id= "searchDropdownBox")
	WebElement select_categories;
	
	@FindBy(id= "twotabsearchtextbox")
	WebElement input_search;
	@FindBy(id= "nav-search-submit-button")
	WebElement btn_search;
	@FindBys( { @FindBy (xpath= "//div[contains(@class,'AdHolder ')][last()]/following-sibling::div//h2/a"),})
	List<WebElement> all_items;
	@FindBy(id= "add-to-cart-button")
	WebElement btn_addToCart;
	@FindBy(xpath= "//h1[contains(text(),'Added to Cart')]")
	WebElement txt_addedToCart;
	
	public boolean searchItem(String search_category, String search_item) {
		
		actions.selectByVisibleText(select_categories, search_category, "select category as"+search_category );
		actions.inputText(input_search, search_item,"Enter " +search_item+ "in search filed");
		actions.click(btn_search, "Click on search button");
		return true;
	}
	
	public boolean addToCart() {
		
		String parent_window = driver.getWindowHandle();
		actions.click(all_items.get(0), "Click on search item");
		Set<String > all_window =driver.getWindowHandles();
		all_window.remove(parent_window);
		for(String window: all_window) {
			
			driver.switchTo().window(window);
			
			actions.click(btn_addToCart, "Click on Add To Cart button");
			actions.isWebElementVisible(txt_addedToCart, " Added item to cart");
			driver.switchTo().defaultContent();
		}
		driver.switchTo().window(parent_window);
		
		return true;
	}
}
