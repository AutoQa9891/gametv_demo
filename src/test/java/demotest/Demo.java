package demotest;
import config.Base;
import pages.HomePage;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import org.testng.Assert;
import testdata.DemoTestData;
import util.CommonUtil;
import util.CreateExcel;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Demo extends Base {
	HomePage home;
	int allLinksStatus = 200;
	DemoTestData td = new DemoTestData();
	CommonUtil cu = new CommonUtil();
	CreateExcel ce = new CreateExcel();
	private static Logger log = LogManager.getLogger(Demo.class.getName());
	Map<String, Object[]> excelData = new TreeMap<String, Object[]>();

	public Demo() {
		super();
	}

	@Test(priority = 1)
	public void pageTitleTest() {
		home = new HomePage(driver);
		boolean isPageTitleValidated = home.pageTitle().equals(td.exPageTitle);
		if(!isPageTitleValidated){
			cu.takeScreenshot(driver);
		}
		Assert.assertEquals(isPageTitleValidated, true, "Page title validation failed!");
	}

	@Test(priority = 2)
	public void scrollToView() {
		boolean isGameTileVisible = home.scrollToTestSection(td.expectedTestLocationTitle);
		Assert.assertEquals(isGameTileVisible, true, "Game tile title not present on page!");
	}

	@Test(priority = 3)
	public void detailsAvailbleOnGameTiles() {
		int result = home.getAllTileDetails(td.eachGameTileImageUrlPattern);
		if(result != 1){
			cu.takeScreenshot(driver);
		}
		Assert.assertEquals(result, 1, "Either game name Or detail page url not found!");
	}

	@Test(priority = 4)
	public void validateGameDetailUrlsStatusCode() {
		ArrayList<String> urls = home.game_detail_page_url_list;
		boolean isUrlsValidated = cu.validateActiveLinks(urls);
		if(isUrlsValidated){
			allLinksStatus = 200;
		}
		Assert.assertEquals(isUrlsValidated, true, "Urls broken please check logs!");
	}

	@Test(priority = 5)
	public void validateNameOnTournamentDetailsPage() {
		int result = home.getTournamentsCounts();
		Assert.assertEquals(result, 1, "All link details not captured there is issues while getting details!");
	}

	@Test(priority = 6, dependsOnMethods = {"scrollToView","detailsAvailbleOnGameTiles","validateNameOnTournamentDetailsPage"})
	public void printResultSet(){
		ArrayList<String> gameName = home.game_names_list;
		ArrayList<String> pageUrl = home.game_detail_page_url_list;
		ArrayList<Integer> tournamentCount =  home.tournamentNumberCounts;
		Assert.assertEquals(tournamentCount.size(), pageUrl.size(), "Game Name, Url and tournament count details not correct!");
		if((gameName.size() == pageUrl.size()) && (tournamentCount.size() == pageUrl.size())){
			int count = 0;
			excelData.put("1", new Object[] {"ID", "Game Name", "Url", "Link Status", "Total Tournamenet Count"});
			for(String name : gameName){
				int counter = (count+1);
				String game_name = name;
				String url = pageUrl.get(count-1);
				int status = allLinksStatus;
				int tournament_count = tournamentCount.get(count-1);
				excelData.put(String.valueOf(counter), new Object[] {counter, game_name, url, status, tournament_count});
				log.info(counter +" | "+game_name+" | "+url+" | "+status+" | "+tournament_count);
			}
		}
		String fileName = "DemoWebData";
		ce.createWorkBook("DemoWebData", excelData);
		log.info("All expected data saved in logs folder with file name : "+fileName+" in Excel format.");
	}
}
