
package info.edutech.smartsurveillance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataLock {

    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("auto_lock")
    @Expose
    private Boolean autoLock;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Boolean getAutoLock() {
        return autoLock;
    }

    public void setAutoLock(Boolean autoLock) {
        this.autoLock = autoLock;
    }
}
