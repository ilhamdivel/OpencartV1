package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.HomePage;
import pageObjects.RegistrationPage;
import testBase.BaseClass;
import utilities.DataGenerator;

public class TC001_AccountRegistrationTest extends BaseClass {
	

	@Test(description = "Verify user can successfully register a new account", groups={"Regression","Master"})
	public void verifyAccountRegistration() {
		getLogger().info("==========START REGISTRATION==========");

		// 1. Generate data
		String firstName = DataGenerator.getFirstName();
		String lastName = DataGenerator.getLastName();
		String email = DataGenerator.getEmail();
		String password = DataGenerator.getPassword();

		// Log data yang di-generate untuk keperluan debugging jika tes gagal
		getLogger().debug("Generated test data - First Name: {}, Last Name: {}, Email: {}", firstName, lastName, email);

		try {
			// 2. Navigate to registration page
			HomePage hp = new HomePage(driver);
			getLogger().info("Navigating to My Account page.");
			hp.clickMyAccount();
			getLogger().info("Navigating to Registration page.");
			hp.clickRegister();

			// 3. Fill the registration form
			RegistrationPage rp = new RegistrationPage(driver);
			getLogger().info("Filling the registration form.");
			rp.setFirstName(firstName);
			rp.setLastName(lastName);
			rp.setEmail(email);
			rp.setPassword(password);
			rp.clickAgree();
			rp.clickContinue();

			// 4. Verify the result
			String confirmMsg = rp.getConfirmationMsg();
			if(confirmMsg.equals("Your Account Has Been Created!")) 
			{
				Assert.assertTrue(true);
				getLogger().info("Verification PASSED: Account successfully created.");
			}
			else 
			{
				getLogger().info("Email duplikat");
				getLogger().error("Email duplikat");
				getLogger().debug("Email duplikat");
				Assert.assertTrue(false);
			}

		}  catch (Exception e) {
			getLogger().error("An unexpected error occurred during registration.", e);
			throw e;
		}

		getLogger().info("==========END REGISTRATION==========");

	}

}
