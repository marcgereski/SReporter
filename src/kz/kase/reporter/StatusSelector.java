package kz.kase.reporter;


import java.util.ArrayList;
import java.util.List;

public class StatusSelector {
    List<StatusReporter> statusListeners = new ArrayList<StatusReporter>();
    private static StatusSelector instance;

    public static StatusSelector getInstance() {
        if (instance == null) {
            instance = new StatusSelector();
        }
        return instance;
    }

    private StatusSelector() {

    }

    public void setStatus(String status) {
        for (StatusReporter sr : statusListeners) {
            sr.setStatus(status);
        }
    }

    public void addListener(StatusReporter stReporter) {
        statusListeners.add(stReporter);
    }

    public boolean DeleteEventListener(StatusReporter stReporter) {
        if (statusListeners.contains(stReporter)) {
            return statusListeners.remove(stReporter);
        } else {
            return false;
        }
    }
}
