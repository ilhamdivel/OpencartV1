package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.HomePage;
import pageObjects.LoginPage;
import pageObjects.MyAccountPage;
import testBase.BaseClass;


public class TC002_LoginTest extends BaseClass{
	
	@Test(description="Verify User can successfully login", groups={"Sanity","Master"})
	public void verifyLogin() {
		getLogger().info("==========START LOGIN==========");
		
		try {
			HomePage hp = new HomePage(driver);
			getLogger().info("Navigating to My Account page.");
			hp.clickMyAccount();
			getLogger().info("Navigating to Login page.");
			hp.clickLogin();

			// Fill the login form
			LoginPage lp = new LoginPage(driver);
			getLogger().info("Filling the login form.");
			lp.setEmail(p.getProperty("email"));
			lp.setPassword(p.getProperty("password"));
			
			getLogger().debug("Email : {}", p.getProperty("email"));
			getLogger().debug("Password : {}", p.getProperty("password"));
			
			getLogger().info("Click Login");
			lp.clickLogin();
			
			//Verify the result
			MyAccountPage ap = new MyAccountPage(driver);
			Boolean isMyAccountPageExist = ap.isMyAccountPageExist();
			if(isMyAccountPageExist.equals(true)) 
			{
				Assert.assertTrue(true);
				getLogger().info("Verification PASSED: Account successfully Login.");
			}
			else 
			{
				getLogger().info("Failed Login");
				getLogger().error("Failed Login");
				Assert.assertTrue(false);
			}
			
		}catch(Exception e) {
			getLogger().error("An unexpected error occurred during registration.", e);
			throw e;
		}
		
		getLogger().info("==========END LOGIN==========");
	}
}
