
package info.edutech.smartsurveillance.hardwarestate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Lock implements Serializable
{

    private Boolean state;

    public Lock() {
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }



}
