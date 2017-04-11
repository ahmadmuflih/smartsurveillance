package info.edutech.smartsurveillance.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ValidationAlarm {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("data")
    @Expose
    private DataAlarm data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public DataAlarm getData() {
        return data;
    }

    public void setData(DataAlarm data) {
        this.data = data;
    }

}
