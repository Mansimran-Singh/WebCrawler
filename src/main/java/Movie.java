import org.jsoup.nodes.Document;

import java.util.Date;

public class Movie {
    String title;
    Date releaseDate;
    String url;
    String posterUrl;
    Document htmlPage;

    public Movie(String title, Date releaseDate, String url, String posterUrl ,Document htmlPage) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.url = url;
        this.htmlPage = htmlPage;
        this.posterUrl = posterUrl;
    }
}
