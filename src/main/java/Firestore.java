import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Mansimran Singh
 * @course CS 560 Secure Software Systems
 * @team Shad, Mariah, Mario, Maciej, Mansimran, Khadija
 * --------- Firestore Class defines integration with firestore, and handles db functionality ----------
 */
class Firestore {
    // Initializing database instance
    private static com.google.cloud.firestore.Firestore db = null;

    /**
     * Method to implement firebase connection using service account json file.
     * This method must be called before any firestore interaction.
     * @throws IOException if connection failed.
     */
    static void initialize() throws IOException {
        // Read service account file
        InputStream serviceAccount = new FileInputStream("utils/serviceAccount.json");
        // Get google credentials from file
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        // Connect to firebase
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);
        // Set database instance for project
        db = FirestoreClient.getFirestore();
    }

    /**
     * Method is used to create a new document and add it to firestore database.
     * @param movie of type Movie to be added in the firestore database.
     * @throws ExecutionException if failed to get 'uploaded at' timestamp.
     * @throws InterruptedException if failed to get 'uploaded at' timestamp.
     */
    static void addMovie(Movie movie) throws ExecutionException, InterruptedException {
        // Create document reference with title
        DocumentReference docRef = db.collection("movies").document(movie.title);
        // Create a map for document
        Map<String, Object> data = new HashMap<>();
        // Add data to map
        data.put("title", movie.title);
        data.put("releaseDate", movie.releaseDate);
        data.put("url", movie.url);
        data.put("htmlPage", movie.htmlPage.toString());
        data.put("posterUrl", movie.posterUrl);
        // Write data to the newly created document reference
        ApiFuture<WriteResult> result = docRef.set(data);
        // Print the upload time
        System.out.println("Uploaded New movie at time : " + result.get().getUpdateTime() +" | Movie Title: "+ movie.title);
    }

    /**
     * Method to check if the document exists already in the database to save time, and computation.
     * @param title of type string to pass the movie name which should be same as document name.
     * @return boolean true or false if movie has a document already present in firestore db respectively.
     * @throws ExecutionException if failed to get firebase documents'.
     * @throws InterruptedException if failed to get firebase documents'.
     */
    static boolean checkIfTitleIsNew(String title) throws ExecutionException, InterruptedException {
        // Get collection movies from db instance
        CollectionReference movies = db.collection("movies");
        // Initialize query with title as document name in collection
        Query query = movies.whereIn("title", Collections.singletonList(title));
        // Execute query and get results
        final ApiFuture<QuerySnapshot> querySnapshot = query.get();
        // return boolean of query results determining if document is new or not
        return querySnapshot.get().getDocuments().size() <= 0;
    }

}
