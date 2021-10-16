package build;

import org.jsoup.nodes.Document;

public class Movie {
    String title;
    String releaseDate;
    String url;
    Document htmlPage;

    public Movie(String title, String releaseDate, String url, Document htmlPage) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.url = url;
        this.htmlPage = htmlPage;
    }
}
