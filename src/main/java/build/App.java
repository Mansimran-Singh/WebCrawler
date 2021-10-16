package build;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class App {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
// --------- JSOUP WEB CRAWLER INTEGRATED WITH FIRESTORE TO STORE PAGES WITH INDEX ---------- NEEDS MULTI-THREADING //
        WebCrawler wc = new WebCrawler();
        wc.getMoviesList();
    }
}
