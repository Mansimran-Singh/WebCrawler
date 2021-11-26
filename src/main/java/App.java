/**
 * @author Mansimran Singh
 * @course CS 560 Secure Software Systems
 * @team Shad, Mariah, Mario, Maciej, Mansimran, Khadija
 * --------- JSOUP WEB CRAWLER INTEGRATED WITH FIRESTORE ----------
 */
public class App {
    /**
     * Main Method to launch crawler and fetch movies
     */
    public static void main(String[] args) {
        WebCrawler wc = new WebCrawler();
        wc.getMoviesList();
    }
}