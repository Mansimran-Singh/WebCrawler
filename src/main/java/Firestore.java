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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Mansimran Singh
 * @course CS 560 Secure Software Systems
 * @team Shad, Mariah, Mario, Maciej, Mansimran, Khadija
 * --------- Firestore Class to Handle Db functionality and integration with firestore----------
 */
public class Firestore {

    private static com.google.cloud.firestore.Firestore db = null;

    public static void initialize() throws IOException {
        InputStream serviceAccount = new FileInputStream("utils/serviceAccount.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
    }

    public static void addMovie(Movie movie) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("movies").document(movie.title);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("title", movie.title);
        data.put("releaseDate", movie.releaseDate);
        data.put("url", movie.url);
        data.put("htmlPage", movie.htmlPage.toString());
        data.put("posterUrl", movie.posterUrl.toString());
        ApiFuture<WriteResult> result = docRef.set(data);
        System.out.println("Downloaded ["+ movie.title +"] at time : " + result.get().getUpdateTime());
    }

    public static boolean checkIfTitleExists(String arr) throws ExecutionException, InterruptedException {
        CollectionReference movies = db.collection("movies");
        Query query = movies.whereIn("title", Collections.singletonList(arr));

        final ApiFuture<QuerySnapshot> querySnapshot = query.get();
//        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
//            System.out.println("RESULT: "+document.getId());
//        }
        if(querySnapshot.get().getDocuments().size()>0){
            return false;
        }else {
            return true;
        }
    }

    public static List<QueryDocumentSnapshot> readData() throws ExecutionException, InterruptedException {
        // asynchronously retrieve all movies
        ApiFuture<QuerySnapshot> query = db.collection("movies").get();
        // query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        return documents;
    }

}
