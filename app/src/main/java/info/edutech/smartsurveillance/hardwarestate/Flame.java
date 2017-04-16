
package info.edutech.smartsurveillance.hardwarestate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Flame
{

    private Boolean state;

    public Flame() {
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

}
