import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main extends FnLib{
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
// --------- JSOUP WEB CRAWLER INTEGRATED WITH FIRESTORE TO STORE PAGES WITH INDEX ---------- NEEDS MULTI-THREADING //
        WebCrawler wc = new WebCrawler();
        wc.getMoviesList();
//------------SELENIUM WEB SCRAPPER-------------//
        // Print stack
//        BasicConfigurator.configure();
        // Set up jUnitCore
//        JUnitCore junit = new JUnitCore();
        // Listener to print returned outputs
//        junit.addListener(new TextListener(System.out));
        // Run Scraping Tests
//        junit.run(WebScraper.class);
    }
}
