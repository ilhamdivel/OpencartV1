package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;

import utilities.ExtentReportManager;

/**
 * BaseClass - Parent class untuk semua test class
 * 
 * @Listeners annotation memastikan ExtentReportManager diregister untuk semua
 *            test class yang extend BaseClass. Ini adalah BEST PRACTICE karena:
 *            1. Eksplisit dan mudah dipahami 2. Tidak perlu define listener di
 *            setiap XML 3. Otomatis berlaku untuk semua test class
 */
@Listeners(ExtentReportManager.class)
public class BaseClass {

	protected static WebDriver driver;
	protected Properties p;

	@BeforeClass(groups = { "Sanity", "Master", "Regression" })
	@Parameters({ "os", "browser" })
	public void setup(String os, String br) throws IOException {

		// Suppress Selenium CDP version warning
		java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);

		// Load config.properties
		FileReader file = new FileReader("./src//test//resources//config.properties");
		p = new Properties();
		p.load(file);

		if (p.getProperty("execution_env").equalsIgnoreCase("remote")) {
			String huburl = "http://localhost:4444/wd/hub";

			DesiredCapabilities cap = new DesiredCapabilities();

			// OS

			switch (os.toLowerCase()) {
			case ("windows"):
				cap.setPlatform(Platform.WIN11);
				break;
			case ("linux"):
				cap.setPlatform(Platform.LINUX);
				break;
			case ("mac"):
				cap.setPlatform(Platform.MAC);
				break;
			default:
				System.out.println("Invalid OS");
				return;
			}

			// Browser

			switch (br.toLowerCase()) {
			case ("edge"):
				cap.setBrowserName("msedge");
				break;
			case ("chrome"):
				cap.setBrowserName("chrome");
				break;
			case ("firefox"):
				cap.setBrowserName("firefox");
				break;
			default:
				System.out.println("Invalid Browser");
				return;
			}

			driver = new RemoteWebDriver(new URL(huburl), cap);
		}
		
		if (p.getProperty("execution_env").equalsIgnoreCase("local")) {
			
			switch (br.toLowerCase()) {
			case ("edge"):
				driver = new EdgeDriver();
				break;
			case ("chrome"):
				driver = new ChromeDriver();
				break;
			case ("firefox"):
				driver = new FirefoxDriver();
				break;
			default:
				System.out.println("Invalid Browser");
				return;
			}
		}


		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		driver.get(p.getProperty("appUrl"));
		driver.manage().window().maximize();
	}

	protected Logger getLogger() {
		return LogManager.getLogger(this.getClass());
	}

	@AfterClass(groups = { "Sanity", "Master", "Regression" })
	public void tearDown() {
		driver.quit();
	}

	public String captureScreen(String tname) throws IOException {

		String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

		TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
		File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

		String targetFilePath = System.getProperty("user.dir") + "\\screenshots\\" + tname + "_" + timeStamp + ".png";
		File targetFile = new File(targetFilePath);

		sourceFile.renameTo(targetFile);

		return targetFilePath;

	}
}
