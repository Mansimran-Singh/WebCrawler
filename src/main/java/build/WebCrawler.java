package build;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
            int finalI = i;
                    try {
//                        System.out.println( "HEADER: " + headers.get(finalI).text() + " |   Name: "+li.text() +" |     Link: "+li.attr("abs:href"));
                        if (Firestore.checkIfTitleExists(moviesToDownload.get(i).title)){
                            moviesToDownload.get(i).htmlPage = Jsoup.connect(moviesToDownload.get(i).url).get();
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

                    Movie m = new Movie(
                            li.text(), headers.get(finalI).text(),
                            li.attr("abs:href"),null);
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
