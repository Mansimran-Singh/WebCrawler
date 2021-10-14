import org.apache.log4j.BasicConfigurator;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

public class Main extends FnLib{
    public static void main(String[] args) {
        // Print stack
//        BasicConfigurator.configure();
        // Set up jUnitCore
        JUnitCore junit = new JUnitCore();
        // Listener to print returned outputs
        junit.addListener(new TextListener(System.out));
        // Run Scraping Tests
        junit.run(WebScrapeTests.class);
    }
}
