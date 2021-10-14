
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.io.IOException;
import java.util.List;


public class WebScrapeTests extends FnLib{

    @Before
    public void setUp() {
        try {
            closeExe("chrome");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Dialog
//        informTheUserDialog("STARTING THE TEST");
        // Initialize ChromeDriver
        initializeDriver();
    }

    @After
    public void tearDown() {
        // Close ChromeDriver
        closeDriver();
    }

    @Test
    public void scrapeRecentMoviesOnIMDB(){
        // Open Url
        openUrl("www.imdb.com");
        // Click on "Open Navigation Drawer"
        clickElem(driver.findElement(By.xpath("//div[normalize-space()='Menu']")));
        // Click on "MoviesRelease CalendarDVD & Bl"
        clickElem(driver.findElement(By.xpath("//body/div[@id='__next']/nav[@id='imdbHeader']/div[@role='presentation']/aside[@role='presentation']/div[@role='presentation']/div[@role='presentation']/div[@role='presentation']/div[1]/span[1]")));
        // Click on "Release Calendar"
        clickElem(driver.findElement(By.xpath("//a[@href='https://www.imdb.com/calendar/?ref_=nv_mv_cal']")));

        // Get Dates Element List
        List<WebElement> heads = driver.findElements(By.xpath("//div[@id='main']/h4"));
        // Get Movies Element List
        List<WebElement> ul = driver.findElements(By.xpath("//div[@id='main']/ul"));
        // Iterating through list
        for (int i = 1; i < ul.size(); i++ ){
            // Print Dates
            WebElement head = driver.findElement(By.xpath("//div[@id='main']/ul["+i+"]/preceding-sibling::h4"));
            if (head.isDisplayed()){
                System.out.println(heads.get(i).getAttribute("innerHTML"));
            }
            // Print Movies with Links
            for ( WebElement l:ul.get(i).findElements(By.tagName("li")) ) {
                System.out.println(getTextFromElem(l));
                System.out.println(l.findElement(By.tagName("a")).getAttribute("href"));
            }

        }

    }

}