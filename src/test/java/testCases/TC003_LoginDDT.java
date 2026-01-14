package testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import pageObjects.HomePage;
import pageObjects.LoginPage;
import pageObjects.MyAccountPage;
import testBase.BaseClass;
import utilities.DataProviders;

/**
 * Data-Driven Login Test
 * 
 * Test Logic:
 * Data is valid + login success → test PASS (logout)
 * Data is valid + login failed → test FAIL
 * Data is invalid + login success → test FAIL (logout)
 * Data is invalid + login failed → test PASS
 */
public class TC003_LoginDDT extends BaseClass {

    @Test(dataProvider = "LoginData", dataProviderClass = DataProviders.class, groups="Datadriven")
    public void verifyLoginDDT(String email, String password, String expected) {
        getLogger().info("========== START LOGIN DDT ==========");
        getLogger().info("Testing with - Email: {}, Expected: {}", email, expected);

        try {
            // 1. Navigate to Login Page
            HomePage hp = new HomePage(driver);
            hp.clickMyAccount();
            hp.clickLogin();

            // 2. Enter credentials and login
            LoginPage lp = new LoginPage(driver);
            lp.setEmail(email);
            lp.setPassword(password);
            lp.clickLogin();

            // 3. Check if login was successful
            MyAccountPage ap = new MyAccountPage(driver);
            boolean isLoggedIn = ap.isMyAccountPageExist();


            if (expected.equalsIgnoreCase("valid")) {
                // Data is VALID - expecting login SUCCESS
                if (isLoggedIn) {
                    // Login success with valid data → PASS
                    getLogger().info("PASS: Valid data, login successful as expected");
                    ap.clickMyAccount();
                    ap.clickLogout();
                    getLogger().info("Logged out successfully");
                    Assert.assertTrue(true);
                } else {
                    // Login failed with valid data → FAIL
                    getLogger().error("FAIL: Valid data but login failed");
                    Assert.fail("Login failed with valid credentials");
                }
            } else {
                // Data is INVALID - expecting login FAILURE
                if (isLoggedIn) {
                    // Login success with invalid data → FAIL (security issue!)
                    getLogger().error("FAIL: Invalid data but login succeeded - Security Issue!");
                    ap.clickMyAccount();
                    ap.clickLogout();
                    getLogger().info("Logged out after unexpected success");
                    Assert.fail("Login succeeded with invalid credentials - Security Issue!");
                } else {
                    // Login failed with invalid data → PASS
                    getLogger().info("PASS: Invalid data, login failed as expected");
                    Assert.assertTrue(true);
                }
            }

        } catch (Exception e) {
            getLogger().error("An unexpected error occurred during login DDT", e);
            throw e;
        }

        getLogger().info("========== END LOGIN DDT ==========");
    }
}
