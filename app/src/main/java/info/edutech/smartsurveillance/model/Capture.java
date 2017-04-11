package info.edutech.smartsurveillance.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Baso on 2/21/2017.
 */
public class Capture extends RealmObject {
    @PrimaryKey
    private int id;
    private String url;
    private String imageName;
    private Date date;

    public Capture() {
    }

    public Capture(int id, String url, String imageName, Date date) {
        this.id = id;
        this.url = url;
        this.imageName = imageName;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
