
package info.edutech.smartsurveillance.hardwarestate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Alarm
{

    private Boolean alarm1;
    private Boolean alarm2;

    public Alarm() {
    }

    public Alarm(Boolean alarm1, Boolean alarm2) {
        this.alarm1 = alarm1;
        this.alarm2 = alarm2;
    }

    public Boolean getAlarm1() {
        return alarm1;
    }

    public void setAlarm1(Boolean alarm1) {
        this.alarm1 = alarm1;
    }

    public Boolean getAlarm2() {
        return alarm2;
    }

    public void setAlarm2(Boolean alarm2) {
        this.alarm2 = alarm2;
    }



}
