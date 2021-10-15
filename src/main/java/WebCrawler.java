import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WebCrawler {
    private HashSet<String> links;
    private List<List<String>> articles;
    private List<Movie> movies;
    final String URL = "https://www.imdb.com/calendar";

    public WebCrawler() {
        links = new HashSet<>();
        articles = new ArrayList<>();
        movies = new ArrayList<>();
    }

    public Elements getTable() {
        if (!links.contains(URL)) {
            try {
                Document document = Jsoup.connect(URL).get();
                Elements table = document.select("#main ");
                return table;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    public void getMoviesList() throws ExecutionException, InterruptedException, IOException {
        Elements table = getTable();
        Elements headers = table.select("h4");
        Elements ulList = table.select("ul");
        FirestoreDB.initialize();
        for (int i = 0; i<ulList.size(); i++){
            Elements liList = ulList.get(i).select("li > a");
            int finalI = i;
            List<QueryDocumentSnapshot> documents = FirestoreDB.readData();
            if (documents.size() != liList.size()){
                liList.forEach(li->{
                    try {
//                        System.out.println( "HEADER: " + headers.get(finalI).text() + " |   Name: "+li.text() +" |     Link: "+li.attr("abs:href"));
                        if (FirestoreDB.checkIfTitleExists(li.text())){
                            Movie m = new Movie(
                                li.text(), headers.get(finalI).text(),
                                li.attr("abs:href"),Jsoup.connect(li.attr("abs:href")).get());
                            FirestoreDB.addMovie(m);
                        }else {
                            System.out.println("REPLICA FOUND: "+li.text());
                        }
                    } catch (IOException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }

        }

    }


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        WebCrawler wc = new WebCrawler();
        wc.getMoviesList();
    }
}
