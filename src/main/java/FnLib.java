import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FnLib {

    protected static WebDriver driver;

    /**
     * initializeDriver() is final method to initialize ChromeDriver
     */
    protected final void initializeDriver() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    /**
     * Close any open exe
     * @param exe name of the executable running without extension like "excel"
     * @throws IOException throws exception if the no exe is found running
     */
    protected final void closeExe(String exe) throws IOException {
        Runtime.getRuntime().exec("taskkill /F /IM "+exe+".exe");

    }

    /**
     * closeDriver to close the current driver instance and quit
     */
    protected final void closeDriver(){
        driver.close();
        driver.quit();
    }

    /**
     * Open URL from existing driver instance (driver.get(...))
     * @param url The url string without https://
     */
    protected final void openUrl(String url){
        // Get
        driver.get("https://"+url);
        // Maximize current window
        driver.manage().window().maximize();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * informTheUserDialog to load a dialog with message
     * @param message The message string
     */
    protected final void informTheUserDialog(final String message){
        // inform the user
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * clickElem to click on the element
     * @param element webElement
     * @return returns boolean for assertions
     */
    protected final boolean clickElem(WebElement element) {
        try {
            if(element.isDisplayed()) {
                highLightElem(element);
                element.click();
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }

    }

    /**
     * typeToElem to click on the element
     * @param element webElement
     * @return returns boolean for assertions
     */
    protected final boolean typeToElem(WebElement element, String typedString) {
        try {
            if(element.isDisplayed()) {
                highLightElem(element);
                element.sendKeys(typedString);
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }

    }

    /**
     * typeToElem to click on the element
     * @param element webElement
     * @return returns boolean for assertions
     */
    protected final String getTextFromElem(WebElement element) {
        try {
            if(element.isDisplayed()) {
//                highLightElem(element);
                return element.getText();
            } else {
                return "NO TEXT RETURNED";
            }
        }catch (Exception e){
            return "NO TEXT RETURNED";
        }

    }

    /**
     * addWait Thread sleep
     * @param sec integer seconds
     */
    protected final void addWait(int sec) {
        sec = (sec*1000);
        try {
            Thread.sleep(sec);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * implicitWait
     * @param sec integer seconds
     */
    protected final void implicitWait(int sec){
        driver.manage().timeouts().implicitlyWait(sec, TimeUnit.SECONDS);
    }


    /**
     * explicitWait on an element
     * @param objType takes object type like xpath, id ...
     * @param objProp takes object property
     * @param sec integer seconds
     */
    protected final void explicitWait(String objType, String objProp,int sec){
        WebDriverWait wait = new WebDriverWait(driver, sec);
        switch(objType.toLowerCase()) {
            case "xpath": {wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(objProp))); break;}
            case "name": {wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(objProp))); break;}
            case "id": {wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(objProp))); break;}
        }
    }


    /**
     * highlightElem to highlight the element on WebView only
     * @param element The Web element to highlight
     */
    protected final void highLightElem(WebElement element){
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("arguments[0].setAttribute('style','background: yellow; border: 2px solid red;');", element);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        js.executeScript("arguments[0].setAttribute('style','background: none; border: none;');", element);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        js.executeScript("arguments[0].setAttribute('style','background: yellow; border: 2px solid red;');", element);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        js.executeScript("arguments[0].setAttribute('style','background: none; border: none;');", element);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
