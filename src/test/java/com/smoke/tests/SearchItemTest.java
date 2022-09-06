package com.smoke.tests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.automation.controllers.DriverFactory;
import com.automation.listeners.CustomListener;
import com.automation.utils.ConfigReader;
import com.automation.utils.ExcelTestDataReader;
import com.smoke.mobile.SearchPage;
import com.smoke.mobile.WelcomePage;

@Listeners(CustomListener.class)
public class SearchItemTest extends DriverFactory
{	

	@Test(dataProvider="getExcelTestData",description ="Verify searching an item")
	public void searchItem(HashMap<String, String> data)
	{
		WelcomePage welcome =new WelcomePage(getDriver());
		welcome.welcomePage();
		SearchPage search =new SearchPage(getDriver());
		search.searchItem(data.get("Category"),data.get("Item"));
		search.addToCart();
		
	//	Assert.assertTrue(, "Home page is not visiable");
	}
	
	@DataProvider
	public Iterator<Object[]> getExcelTestData() 
	{
		String sheetname = this.getClass().getSimpleName();
		ExcelTestDataReader excelReader = new ExcelTestDataReader();
		LinkedList<Object[]> dataBeans = excelReader.getRowDataMap(PWD+ConfigReader.getValue("TestData"),sheetname);
		return dataBeans.iterator();
	}
}
