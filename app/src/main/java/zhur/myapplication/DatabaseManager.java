package zhur.myapplication;

import android.util.Log;

import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhur on 11.09.2016.
 */
public class DatabaseManager {
    private static DatabaseManager instance = null;

    FirebaseDatabase database;
    private DatabaseReference reviewRef;
    private Review review;
    private String key;

    private DatabaseManager() {
        database = FirebaseDatabase.getInstance();
        reviewRef = database.getReference("reviews");
//        Review review = new Review("test1",1L);
//        reviewRef.child("testReview").setValue(review);
//
////        myRef.setValue("fuck yeahhh! " + value);
//        Log.d("DATABASE", "Database uploaded");
    }

    public static DatabaseManager getInstance() {
        if(instance == null)
            instance = new DatabaseManager();

        return instance;
    }

    public void setReview(Review newReview) {
        this.review = newReview;
        key = reviewRef.push().getKey();
        review.setKey(key);
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        String spotId = mAuth.getCurrentUser().getUid().toString();
//        review.setSpotId(spotId);
    }

    public Review getReview() {
        return review;
    }

    /**
     * Сохранение отзыва
     **/
    public void save() {
        if(key != null) {
            reviewRef.child(key).setValue(review);
        } else {
            reviewRef.child(review.getKey()).setValue(review);
        }
    }
}
