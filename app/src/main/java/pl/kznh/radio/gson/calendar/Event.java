package pl.kznh.radio.gson.calendar;

/**
 * Created by SzymonN on 2015-12-02.
 */
public class Event {
        String summary;
        Start start;
        End end;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Start getStart() {
        return start;
    }

    public void setStart(Start start) {
        this.start = start;
    }

    public End getEnd() {
        return end;
    }

    public void setEnd(End end) {
        this.end = end;
    }
}
