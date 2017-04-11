
package info.edutech.smartsurveillance.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataSMS {

    @SerializedName("send_when_fire")
    @Expose
    private Boolean sendWhenFire;
    @SerializedName("send_when_motion")
    @Expose
    private Boolean sendWhenMotion;
    @SerializedName("sms_fire")
    @Expose
    private String smsFire;
    @SerializedName("sms_motion")
    @Expose
    private String smsMotion;

    public Boolean getSendWhenFire() {
        return sendWhenFire;
    }

    public void setSendWhenFire(Boolean sendWhenFire) {
        this.sendWhenFire = sendWhenFire;
    }

    public Boolean getSendWhenMotion() {
        return sendWhenMotion;
    }

    public void setSendWhenMotion(Boolean sendWhenMotion) {
        this.sendWhenMotion = sendWhenMotion;
    }

    public String getSmsFire() {
        return smsFire;
    }

    public void setSmsFire(String smsFire) {
        this.smsFire = smsFire;
    }

    public String getSmsMotion() {
        return smsMotion;
    }

    public void setSmsMotion(String smsMotion) {
        this.smsMotion = smsMotion;
    }

}
