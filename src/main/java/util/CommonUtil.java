package util;
import java.io.File;
import config.Base;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.apache.commons.io.FileUtils;

public class CommonUtil extends Base {

	int resCode = 200;
	HttpURLConnection uc = null;
	public static long PAGE_LOAD_TIMEOUT = 180;
	public static long IMPLICIT_WAIT = 180;
	private static Logger log = LogManager.getLogger(CommonUtil.class.getName());

	public void waitclick(WebElement ele) {
		wait.until(ExpectedConditions.elementToBeClickable(ele));
	}

	public void waitvisible(WebElement ele) {
		wait.until(ExpectedConditions.visibilityOf(ele));
	}

	public void implicitWait(WebDriver driver) {
		driver.manage().timeouts().implicitlyWait(CommonUtil.IMPLICIT_WAIT, TimeUnit.SECONDS);
	}

	public void sleep3Seconds(WebDriver driver) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void sleep6Seconds(WebDriver driver) {
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Scroll till the view is not visible
	public void scrollToView(WebDriver driver, WebElement ele) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView();", ele);
	}

	// Scroll to bottom of page
	public void scrollToBottom(WebDriver driver) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,250)", "");
	}

	// Take Screenshot
	public void takeScreenshot(WebDriver driver) {
        Date date = new Date();
		String os = System.getProperty("os.name");
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String currentDir = System.getProperty("user.dir");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		try {
			if(os.equals("Linux") || os.equals("Mac")) {
				FileUtils.copyFile(scrFile, new File(currentDir+"/screenshots/" +dateFormat.format(date)+".png"));
			}else {
				FileUtils.copyFile(scrFile, new File(currentDir+"/screenshots/" +dateFormat.format(date)+".png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// validate link have status code 200 or not
	public boolean validateActiveLinks(ArrayList<String> urls) {
		boolean linkActive = false;
		for(int i = 0; i<urls.size(); i++) {
			if(urls.get(i) != null) {
				String url = urls.get(i);
				try {
					uc = (HttpURLConnection)(new URL(url).openConnection());
					uc.setRequestMethod("HEAD");
					uc.connect();
					int res = uc.getResponseCode();
					if(res == resCode) {
						// log.info("Active Url : "+url);
						linkActive = true;
					}else {
						linkActive = false;
						log.error("Dead Url : "+url);
						continue;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return linkActive;
	}
}