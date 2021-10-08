import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        //Create driver object for Chrome
        WebDriver driver = new ChromeDriver();
        //Navigate to a URL
        driver.get("http://www.ccsu.edu");
        //Sleep
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Quit the Driver
        driver.quit();
    }
}
