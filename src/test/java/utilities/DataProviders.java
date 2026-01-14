package utilities;

import java.io.IOException;

import org.testng.annotations.DataProvider;

public class DataProviders {

	//DataProvider 1
	
	@DataProvider(name="LoginData")
	public Object[][] getData() throws IOException
	{
		 ExcelUtility xlutil = new ExcelUtility(".\\testData\\Opencart_LoginData.xlsx");
		    return xlutil.getSheetData("Sheet1");  // 1 line!
				
	}
	
	//DataProvider 2
	
	//DataProvider 3
	
	//DataProvider 4
}
