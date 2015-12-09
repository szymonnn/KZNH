package pl.kznh.radio.gson.calendar;

import java.util.ArrayList;

/**
 * Created by SzymonN on 2015-12-01.
 */
public class EventsContainer {
    ArrayList<Event> items;

    public ArrayList<Event> getItems() {
        return items;
    }

    public void setItems(ArrayList<Event> items) {
        this.items = items;
    }
}