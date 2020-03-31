package config;
import util.CommonUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.testng.Reporter;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Base {

	public WebDriver driver;
	CommonUtil cu;
	public static WebDriverWait wait = null;
	public static Properties prop;
	public static String BASEURI = null;
	private static Logger log = LogManager.getLogger(Base.class.getName());
	
	public Base() {
		FileInputStream ip;
		String fileName = "";
		String env = System.getProperty("env");

		if(env != null && env.length() > 0) {
			if(env.contains("stable") || env.contains("production")){
				fileName = "app.properties";
			}else if (env.contains("testing") || env.contains("sandbox")){
				fileName = "local-app.properties";
			}else{
				System.out.println("Unexpected Environment ..!");
				driver.quit();
			}
		}else{
			fileName = "local-app.properties"; // if env not provided then this file will be properties file
		}

		try {
			prop = new Properties();
			ip = new FileInputStream("./src/main/resources/"+fileName);
			prop.load(ip);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();		
		}
	}
	
	public WebDriver startBrowser() {
		if(driver == null) {
			String browserName = prop.getProperty("browser");
			String os = System.getProperty("os.name");
			if(os.equals("Linux") || os.contains("Mac OS X")){
				if(browserName.equals("chrome")) {
					DesiredCapabilities capabilities = DesiredCapabilities.chrome();
					capabilities.setBrowserName("chrome");
					capabilities.setPlatform(Platform.LINUX);
					System.setProperty("webdriver.chrome.driver", "./browserDrivers/chromedriver");
					ChromeOptions option = new ChromeOptions();
					option.setHeadless(false);
					option.addArguments("--disable-infobars");
					option.addArguments("--disable-extensions");
					option.addArguments("--disable-gpu");
					option.addArguments("--disable-features=VizDisplayCompositor");
					driver = new ChromeDriver(option);
				}else if(browserName.equals("firefox")){
					DesiredCapabilities capabilities = DesiredCapabilities.firefox();
					capabilities.setBrowserName("firefox");
					capabilities.setPlatform(Platform.LINUX);
					System.setProperty("webdriver.gecko.driver", "./browserDrivers/geckodriver");
					FirefoxOptions option = new FirefoxOptions();
					option.setHeadless(true);
					option.addArguments("disable-infobars");
					driver = new FirefoxDriver(option);
				}
			}else{
				/** write windows code here */
				log.info("Windows configuration code not written, please write here to invoke windows driver.");
			}
			
			driver.manage().timeouts().pageLoadTimeout(CommonUtil.PAGE_LOAD_TIMEOUT, TimeUnit.SECONDS);
			driver.manage().timeouts().implicitlyWait(CommonUtil.IMPLICIT_WAIT,TimeUnit.SECONDS);
			driver.manage().deleteAllCookies();
			driver.manage().window().maximize();
			wait = new WebDriverWait(driver, 120);
		}
		return driver;
	}
	
	@BeforeClass(alwaysRun = true)
	public void preTest() throws Throwable {
		Reporter.log("========Browser Session Started========", true);
		startBrowser();
		cu = new CommonUtil();
		cu.sleep3Seconds(driver);
		BASEURI = prop.getProperty("baseurl");
		try{
			driver.get(BASEURI);
		}catch(TimeoutException e){
			log.error("Website getting time out, please check manually page load time!");
		}
	}
	
	@AfterClass(alwaysRun = true)
	public void postTest() throws Throwable {
		driver.quit();
		cu.sleep3Seconds(driver);
		Reporter.log("========Browser Session End========", true);
	}
}