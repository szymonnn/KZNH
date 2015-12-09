package pl.kznh.radio.gson.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SzymonN on 2015-12-02.
 */
public class Start {
        String dateTime;

    public Date getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date dt = null;
        try {
            dt = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
