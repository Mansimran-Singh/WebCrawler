import org.jsoup.nodes.Document;
import java.util.Date;

/**
 * @author Mansimran Singh
 * @course CS 560 Secure Software Systems
 * @team Shad, Mariah, Mario, Maciej, Mansimran, Khadija
 * --------- Data Class Movie to model the customized data ----------
 */
class Movie {
    String title;
    Date releaseDate;
    String url;
    String posterUrl;
    Document htmlPage;

    /**
     * Construct Method
     * @param title of type String
     * @param releaseDate of type Date
     * @param url of type String
     * @param posterUrl of type String
     * @param htmlPage of type Document
     */
     Movie(String title, Date releaseDate, String url, String posterUrl ,Document htmlPage) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.url = url;
        this.htmlPage = htmlPage;
        this.posterUrl = posterUrl;
    }
}
