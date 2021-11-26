import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Mansimran Singh
 * @course CS 560 Secure Software Systems
 * @team Shad, Mariah, Mario, Maciej, Mansimran, Khadija
 * --------- Crawl Class defines JSOUP crawler ----------
 * The sole purpose of this class is to crawl hard-coded url imdb calendar and retrieve all the newly added movies.
 * This class creates MULTIPLE THREADS, for retrieving data using JSOUP, and uploading desired to Firestore Database for Web App.
 */
class Crawl {

    // final BASE URL
    final String URL_BASE = "https://www.imdb.com";
    // final Robots.txt URL
    final String URL_ROBOTS = URL_BASE +"/robots.txt";
    // final URL imdb calendar site
    final String URL_CALENDAR = URL_BASE + "/calendar";
    // final URL_404
    final String URL_404 = "https://www.rooseveltlibrary.org/wp-content/uploads/2016/10/PageNotFound.png";
    // Initializing list of Movie for movies
    private final List<Movie> movies = new ArrayList<>();
    // Initializing list of String for disallowed urls from robots.txt
    private List<String> robotsDisallowURLs = new ArrayList<>();


    /**
     * Private Method to retrieve robots.txt for obeying
     * @return List of disallowed URL String
     */
    private List<String> getRobotsDisallowedURLs(){
        // Get the imdb robots.txt
        try {
            // Read URL_ROBOTS
            Document document = Jsoup.connect(URL_ROBOTS).get();
            // Create a list of Disallowed URLS
            List<String> disallowedList = new ArrayList<>(Arrays.asList(document.body().text().split("Disallow: ")));
            // Remove information data
            disallowedList.remove(0);
            // Uncomment below to indent base url to disallowed
            disallowedList.replaceAll(s -> URL_BASE+s);
            //return list
            return disallowedList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Private Method to validate the URL's hit are not present in robots.txt
     * @param url is the url to be validated
     * @return boolean true or false depending upon check respectively.
     */
    boolean validateRobotsText(String url){
        // Get disallowed URLs list of String for Robots.txt Disallow Check
        List<String> robotsDisallowURLs = getRobotsDisallowedURLs();
        // Printing disallowed list
        // System.out.println(robotsDisallowURLs);
        // If URL present in disallowed or robots.txt not found return false else true
        if(robotsDisallowURLs != null)
        return !robotsDisallowURLs.contains(url);
        else return false;
    }

    /**
     * Method can be called from same package.
     * Method is called from Main class to start crawling and retrieve the movie list.
     */
    void getMoviesList() {

        // Get disallowed URL's from robots.txt before doing anything
        robotsDisallowURLs = getRobotsDisallowedURLs();

        // Call to get table method to retrieve html elements of main table
        Elements table = getTable();

        // If table is not empty
        if (table != null){
            // Filter header elements into a var which should provide date of movie
            Elements headers = table.select("h4");
            // Filter un-ordered list into a var which should provide names of movies of same date
            Elements ulList = table.select("ul");

            // Iterate through the un-ordered list of movies on same day
            for (int i = 0; i<ulList.size(); i++){
                // Create a list of links of each movie
                Elements liList = ulList.get(i).select("li > a");
                // Copying i to iTemp var for getting date from header elements
                int iTemp = i;

                // Iterate through each movie link
                liList.forEach(li->{
                    // Initialize a date
                    Date mDate = null;

                    // Try getting the date from headers and parse it to type Date
                    try { mDate=new SimpleDateFormat("dd MMMM yyyy").parse(headers.get(iTemp).text()); }
                    catch (ParseException e) { e.printStackTrace(); }

                    // Add each movie to movies arrayList
                    Movie m = new Movie( li.text(),mDate, li.attr("abs:href"),null, null);
                    movies.add(m);
                });
            }

            // final partition size for linkedList
            final int partitionSize = 5;
            // Create LinkedList of List of Movies
            List<List<Movie>> moviesToDownloadList = new LinkedList<>();
            // Distribute and create sub lists of approx 5 movies or less for execution by separate threads
            for (int i = 0; i < movies.size(); i += partitionSize) {
                moviesToDownloadList.add(movies.subList(i,
                        Math.min(i + partitionSize, movies.size())));
            }

            // Initialize Firestore before any firebase interactions
            try {
                Firestore.initialize();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Create List of threads
            Thread[] threads = new Thread[moviesToDownloadList.size()];

            // counter for adding threads
            int k=0;
            // Iterate through the distributed sublist of movies
            for (List<Movie> moviesToDownload : moviesToDownloadList) {

                // Create a runnable task for threads
                Runnable task = () -> {
                    // Print the id of new thread added
                    System.out.println("Created New Thread | Id: " + Thread.currentThread().getId());
                    // Try to Download desired data from movies sublist provided to thread
                    try {
                        downloadPages(moviesToDownload);
                    } catch ( Exception e) {
                        e.printStackTrace();
                    }
                };

                // Add Thread to thread list
                threads[k] = new Thread(task);
                // Step up counter for added threads
                k++;
            }

            // Get the start time for computation
            long st = System.currentTimeMillis();

            // Start Execution of Each thread in threads list
            for (Thread thread : threads) thread.start();
            // Wait and Join Each thread after execution
            for (Thread thread : threads) {
                try {
                    // join
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Get the end time for computation
            long ed = System.currentTimeMillis();

            // Print time taken for computation in milliseconds
            System.out.println("TOTAL TIME TAKEN FOR COMPUTATION: "+(ed-st)+" milliseconds");
        }else{
            System.out.println("Main Table is empty");
        }
    }


    /**
     * Private Method to parse and get the main table that contains all new releases in imdb release calendar using JSOUP
     * @return a table of type Elements for further parsing
     */
    private Elements getTable() {

        // Validate Calendar URL with Robots.txt
        boolean validatedURL = validateRobotsText(URL_CALENDAR);
        // If URL is not present in disallowed list
        if (validatedURL){
            try {
                // Get the main imdb calendar URL
                Document document = Jsoup.connect(URL_CALENDAR).get();
                // Select the main table from page and return table
                return document.select("#main ");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Private Method is called by Runnable of each thread to retrieve desired data and upload to firestore.
     * @param moviesToDownload of type List<Movie> contains list of 5 or less movies provided by each thread.
     */
    private void downloadPages(List<Movie> moviesToDownload){
        // Iterate through provided movie list
        for (Movie movie : moviesToDownload) {
            // Try getting desired data
            try {
                // System.out.println( "HEADER: " + headers.get(finalI).text() + " |   Name: "+li.text() +" |     Link: "+li.attr("abs:href"));

                // Condition statement to check if the movie is not present in firestore
                if (Firestore.checkIfTitleIsNew(movie.title)) {

                    // Validate movie.url with Robots.txt
                    boolean validatedURL = validateRobotsText(movie.url);
                    // If URL is not present in disallowed list
                    if (validatedURL){
                        // Retrieve HTML Page from movie url
                        Document htmlPage = Jsoup.connect(movie.url).get();
                        movie.htmlPage = htmlPage;
                        // Retrieve images from multiple links
                        Element a = htmlPage.select("a.ipc-lockup-overlay").first();
                        String s = a.attr("abs:href");
                        Document sPage = Jsoup.connect(s).get();
                        Elements images = sPage.select("img[class*=bnaOri]");

                        // Find src image for movie
                        if (images.size()>0){
                            if (images.first().attr("src") != null) {
                                movie.posterUrl = images.first().attr("src");
                            }
                            else{
                                // If not found adding a 404 page
                                movie.posterUrl = URL_404;
                            }
                        }
                        else{
                            // If not found adding a 404 page
                            movie.posterUrl = URL_404;
                        }
                    }

                    // Add movie to firestore database
                    Firestore.addMovie(movie);

                } else {
                    // Print skipped movie message
                    System.out.println("Skipped because replica document exists | Movie Title: " + movie.title);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
