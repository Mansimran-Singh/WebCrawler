import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class WebCrawler {
    private HashSet<String> links;
    private List<Movie> movies;
    final String URL = "https://www.imdb.com/calendar";

    public WebCrawler() {
        links = new HashSet<String>();
        movies = new ArrayList<Movie>();
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

    public void downloadPages(List<Movie> moviesToDownload) throws ExecutionException, InterruptedException, IOException {
        for (int i = 0; i<moviesToDownload.size(); i++){
            try {
//              System.out.println( "HEADER: " + headers.get(finalI).text() + " |   Name: "+li.text() +" |     Link: "+li.attr("abs:href"));
                if (Firestore.checkIfTitleExists(moviesToDownload.get(i).title)){
                    Document htmlPage = Jsoup.connect(moviesToDownload.get(i).url).get();

                    Element a = htmlPage.select("a.ipc-lockup-overlay").first();
                    String s = a.attr("abs:href");
                    Document sPage = Jsoup.connect(s).get();

                    Elements images = sPage.select("img[class*=bnaOri]");

                    int k=1;
                    do{
                        if (images.first().attr("src") != null){
                            moviesToDownload.get(i).posterUrl = images.first().attr("src");
                            break;
                        }
                        k++;
                        if (k==11 && moviesToDownload.get(i).posterUrl == null){
                            moviesToDownload.get(i).posterUrl = "https://www.rooseveltlibrary.org/wp-content/uploads/2016/10/PageNotFound.png";
                        }
                    }while(k<=10);

                    moviesToDownload.get(i).htmlPage = htmlPage;
                    Firestore.addMovie(moviesToDownload.get(i));

                }else {
                    System.out.println("SKIPPED BECAUSE REPLICA FOUND: "+moviesToDownload.get(i).title);
                }
            } catch (IOException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void getMoviesList() {
        Elements table = getTable();
        Elements headers = table.select("h4");
        Elements ulList = table.select("ul");
        for (int i = 0; i<ulList.size(); i++){
            Elements liList = ulList.get(i).select("li > a");
            int finalI = i;

                liList.forEach(li->{
                    Date mDate = null;
                    try {
                        mDate=new SimpleDateFormat("dd MMMM yyyy").parse(headers.get(finalI).text());
//                        System.out.println(mDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Movie m = new Movie(
                            li.text(),mDate,
                            li.attr("abs:href"),null, null);
                    movies.add(m);
//                    System.out.println(m.title);
                });
            }

        int partitionSize = 5;
        List<List<Movie>> moviesToDownloadList = new LinkedList<>();
        for (int i = 0; i < movies.size(); i += partitionSize) {
            moviesToDownloadList.add(movies.subList(i,
                    Math.min(i + partitionSize, movies.size())));
        }

        try {
            Firestore.initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread[] threads = new Thread[moviesToDownloadList.size()];
        int k=0;

        for (List<Movie> moviesToDownload : moviesToDownloadList) {
            Runnable task = () -> {
                System.out.println("Current Thread ID: " + Thread.currentThread().getId());
                try {
                    downloadPages(moviesToDownload);
                } catch (ExecutionException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            };
            threads[k] = new Thread(task);
            k++;
        }

            long st = System.currentTimeMillis();
             for (Thread thread : threads) thread.start();
             for (Thread thread : threads) {
                 try {
                     thread.join();
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }

             long ed = System.currentTimeMillis();
             System.out.println("The computation took "+(ed-st)+" milliseconds");
             System.out.println();
        }

}
