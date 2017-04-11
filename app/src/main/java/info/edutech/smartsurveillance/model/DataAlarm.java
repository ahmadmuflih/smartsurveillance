
package info.edutech.smartsurveillance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataAlarm {

    @SerializedName("alarm_enable")
    @Expose
    private Boolean alarmEnable;
    @SerializedName("beep_motion")
    @Expose
    private Boolean beepMotion;
    @SerializedName("beep_fire")
    @Expose
    private Boolean beepFire;

    public Boolean getAlarmEnable() {
        return alarmEnable;
    }

    public void setAlarmEnable(Boolean alarmEnable) {
        this.alarmEnable = alarmEnable;
    }

    public Boolean getBeepMotion() {
        return beepMotion;
    }

    public void setBeepMotion(Boolean beepMotion) {
        this.beepMotion = beepMotion;
    }

    public Boolean getBeepFire() {
        return beepFire;
    }

    public void setBeepFire(Boolean beepFire) {
        this.beepFire = beepFire;
    }
}
