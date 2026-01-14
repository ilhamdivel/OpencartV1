package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MyAccountPage extends BasePage {

	public MyAccountPage(WebDriver driver) {
		super(driver);
		
	}
	
	@FindBy(xpath="//h1[normalize-space()='My Account']")
	WebElement lblMyAccount;
	
	@FindBy(xpath="//span[normalize-space()='My Account']")
	WebElement lnkMyAccount;
	
	@FindBy(xpath="//a[@class='dropdown-item'][normalize-space()='Logout']")
	WebElement lnkLogout;
	
	
	
	public void clickMyAccount() {
		lnkMyAccount.click();
	}
	
	public void clickLogout() {
		lnkLogout.click();
	}
	
	
	
	public Boolean isMyAccountPageExist() {
		try {
			return lblMyAccount.isDisplayed();
		}catch(Exception e) {
			return false;
		}
	}
	
	
	
	

}
