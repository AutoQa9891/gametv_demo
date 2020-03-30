package pages;
import config.Base;
import util.CommonUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage extends Base {

	WebDriver driver;
	CommonUtil cu = new CommonUtil();
	public ArrayList<String> game_names_list = new ArrayList<>();
	public ArrayList<String> game_detail_page_url_list = new ArrayList<>();
	public ArrayList<Integer> tournamentNumberCounts = new ArrayList<>();
	private static Logger log = LogManager.getLogger(HomePage.class.getName());

	@FindBy (xpath = "//a[@target='_blank' and contains(text(),'Invite Tourney')]")
	private WebElement invite_tourney_btn;

	@FindBy (xpath = "//h3[@class='heading' and contains(text(),'Available Games')]")
	private WebElement available_game_title;

	public HomePage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

    public String pageTitle() {
		cu.waitclick(invite_tourney_btn);
		cu.sleep6Seconds(driver);
		return driver.getTitle();	
	}

	public boolean scrollToTestSection(String val){	
		try{
			cu.scrollToView(driver, available_game_title);
			if(available_game_title.getText().trim().equals(val)){
				return true;
			}
		}catch(Exception e){
			log.error("Test section title not visible on page.");
		}
		return false;
	}

	@FindBy (xpath = "//li[@class='games-item']")
	private List<WebElement> gameTiles;

	@FindBy (xpath = "//li[@class='games-item']/a/figure/img")
	private List<WebElement> gameImages;

	@FindBy (xpath = "//li[@class='games-item']/a/figcaption")
	private List<WebElement> gameNames;

	@FindBy (xpath = "//li[@class='games-item']/a")
	private List<WebElement> gameDetailPageUrls;

	private void getNamesAnsUrlsOfGames(String pattern){
		if((gameTiles.size() == gameImages.size()) && (gameNames.size() == gameDetailPageUrls.size())){
			for(int i = 0; i<gameTiles.size(); i++){
				/** scroll till image **/
				// if(!gameImages.get(i).isDisplayed() && gameNames.get(i).isDisplayed()){
				// 	cu.scrollToView(driver, gameImages.get(i));
				// }
				if(gameImages.get(i).getAttribute("src").contains(pattern)){
					String name = gameNames.get(i).getText().trim();
					String urls = gameDetailPageUrls.get(i).getAttribute("href");
					if(name.length() > 0 && urls.length()>0){
						game_names_list.add(name);
						game_detail_page_url_list.add(urls);
					}else{
						return;
					}
				}else{
					log.info("Image on game name "+gameNames.get(i).getText().trim()+" is not published by game tv domain!");
					String name = gameNames.get(i).getText().trim();
					String urls = gameDetailPageUrls.get(i).getAttribute("href");
					if(name.length() > 0 && urls.length()>0){
						game_names_list.add(name);
						game_detail_page_url_list.add(urls);
					}else{
						return;
					}
				}
			}
		}else{
			log.error("Game tiles and details number of count mismatched manual check required!");
		}
	}

	public int getAllTileDetails(String imgUrlPattern){
		getNamesAnsUrlsOfGames(imgUrlPattern);
		if(game_names_list.size() == game_detail_page_url_list.size()){
			return 1;
		}
		return 0;
	}

	/**
	 * Either via navigate or click and navigate back we can perform from both methods.
	 * In this scenerio we have name and URI so directly performing navigate to operation.
	 */

	@FindBy(xpath = "//span[@class='count-tournaments']")
	private WebElement tournamentCounts;

	@FindBy(xpath = "//h1[@class='heading']")
	private WebElement gameNameOnDetailsPage;

	public int getTournamentsCounts(){
		if(game_names_list.size() == game_detail_page_url_list.size()){
			int count = 0;
			for(String name : game_names_list){
				driver.get(game_detail_page_url_list.get(count)); /** Navigate used so that don't wait to load all elements  */
				cu.waitvisible(gameNameOnDetailsPage);
				cu.scrollToView(driver, tournamentCounts);
				String detailsPageGameName = gameNameOnDetailsPage.getText().trim();
				if(detailsPageGameName.equals(name)){
					int tournament_count = Integer.parseInt(tournamentCounts.getText().trim().replace(",", ""));
					// log.info("Name => "+name+ " tournament count => "+tournament_count);
					tournamentNumberCounts.add(tournament_count);
				}else{
					log.error("Name Mismatched while taking tournament count expected game was "+name+ " but found "+detailsPageGameName);
				}
				count++;
			}
			if(count == game_detail_page_url_list.size()){
				return 1;
			}
		}
		return 0;
	}
}