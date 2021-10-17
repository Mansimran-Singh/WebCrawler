package build;

import java.util.Date;
import org.jsoup.nodes.Document;

import java.time.temporal.TemporalAccessor;

public class Movie {
    String title;
    Date releaseDate;
    String url;
    Document htmlPage;

    public Movie(String title, Date releaseDate, String url, Document htmlPage) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.url = url;
        this.htmlPage = htmlPage;
    }
}
