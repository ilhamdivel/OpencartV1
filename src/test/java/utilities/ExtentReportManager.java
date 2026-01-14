package utilities;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import testBase.BaseClass;

/**
 * ExtentReportManager - Unified Extent Report
 * 
 * PENDEKATAN: Menggunakan IExecutionListener + @Listeners annotation
 * 
 * Cara Kerja:
 * - Listener didaftarkan via @Listeners annotation di BaseClass
 * - onExecutionStart(): Dipanggil SEKALI sebelum semua suite dimulai
 * - onExecutionFinish(): Dipanggil SEKALI setelah semua suite selesai
 * 
 * Keuntungan:
 * - Report hanya dibuat sekali untuk seluruh test run
 * - Tidak perlu define listener di setiap XML
 * - Eksplisit dan mudah dipahami (best practice)
 */
public class ExtentReportManager implements ITestListener, IExecutionListener {

    // Static fields - shared across all suites
    private static ExtentSparkReporter sparkReporter;
    private static ExtentReports extent;
    private static String repName;

    // ThreadLocal for thread-safe test reporting in parallel execution
    private static ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    // ============ IExecutionListener - Called ONCE for entire TestNG run
    // ============

    /**
     * Called ONCE before any suite starts.
     * Perfect place to initialize report.
     */
    @Override
    public void onExecutionStart() {
        System.out.println("[ExtentReport] ========== EXECUTION STARTED ==========");
        initializeReport();
    }

    /**
     * Called ONCE after ALL suites complete.
     * Perfect place to finalize and open report.
     */
    @Override
    public void onExecutionFinish() {
        System.out.println("[ExtentReport] ========== EXECUTION FINISHED ==========");
        finalizeReport();
    }

    /**
     * Initialize Extent Report
     */
    private void initializeReport() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        repName = "Test-Report-" + timeStamp + ".html";
        sparkReporter = new ExtentSparkReporter(".\\reports\\" + repName);

        sparkReporter.config().setDocumentTitle("Opencart Automation Report");
        sparkReporter.config().setReportName("Opencart Functional Testing");
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Application", "Opencart");
        extent.setSystemInfo("Module", "Admin");
        extent.setSystemInfo("Sub Module", "Customers");
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", "QA");

        System.out.println("[ExtentReport] Report initialized: " + repName);
    }

    /**
     * Finalize and open report
     */
    private void finalizeReport() {
        if (extent != null) {
            extent.flush();

            String pathOfExtentReport = System.getProperty("user.dir") + "\\reports\\" + repName;
            File extentReport = new File(pathOfExtentReport);

            try {
                Desktop.getDesktop().browse(extentReport.toURI());
                System.out.println("[ExtentReport] Report opened: " + pathOfExtentReport);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ============ ITestListener Methods ============

    @Override
    public void onStart(ITestContext testContext) {
        if (extent != null) {
            String os = testContext.getCurrentXmlTest().getParameter("os");
            String browser = testContext.getCurrentXmlTest().getParameter("browser");

            if (os != null)
                extent.setSystemInfo("Operating System", os);
            if (browser != null)
                extent.setSystemInfo("Browser", browser);

            List<String> includedGroups = testContext.getCurrentXmlTest().getIncludedGroups();
            if (!includedGroups.isEmpty()) {
                extent.setSystemInfo("Groups", includedGroups.toString());
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (extent == null)
            return;

        ExtentTest test = extent.createTest(result.getTestClass().getName() + " :: " + result.getName());
        test.assignCategory(result.getMethod().getGroups());
        test.log(Status.PASS, result.getName() + " executed successfully");
        testThread.set(test);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (extent == null)
            return;

        ExtentTest test = extent.createTest(result.getTestClass().getName() + " :: " + result.getName());
        test.assignCategory(result.getMethod().getGroups());

        test.log(Status.FAIL, result.getName() + " failed");
        test.log(Status.INFO, result.getThrowable().getMessage());

        try {
            String imgPath = new BaseClass().captureScreen(result.getName());
            test.addScreenCaptureFromPath(imgPath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        testThread.set(test);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (extent == null)
            return;

        ExtentTest test = extent.createTest(result.getTestClass().getName() + " :: " + result.getName());
        test.assignCategory(result.getMethod().getGroups());
        test.log(Status.SKIP, result.getName() + " was skipped");
        if (result.getThrowable() != null) {
            test.log(Status.INFO, result.getThrowable().getMessage());
        }
        testThread.set(test);
    }

    @Override
    public void onFinish(ITestContext testContext) {
        // No action needed - report finalization handled by onExecutionFinish
    }
}
