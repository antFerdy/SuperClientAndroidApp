package zhur.myapplication;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhur on 12.09.2016.
 */
@IgnoreExtraProperties
public class Review implements Serializable {
    @Exclude
    private String key;
    private String spotId;
    private String firstRating;
    private String secondRating;
    private String photoUrl;
    private String videoUrl;
    private String telNumber;
    private String reviewDate;
    private String comment;

    public Review() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        setReviewDate(timeStamp);
    }



    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }



    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getFirstRating() {
        return firstRating;
    }

    public void setFirstRating(String firstRating) {
        this.firstRating = firstRating;
    }

    public String getSecondRating() {
        return secondRating;
    }

    public void setSecondRating(String secondRating) {
        this.secondRating = secondRating;
    }
}
