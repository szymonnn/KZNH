package pl.kznh.radio.gson.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by SzymonN on 2015-12-07.
 */
public class RecordContainer {
    private ArrayList<Record> items;


    public ArrayList<Record> getRecordArray() {
        return items;
    }

    public void setRecordArray(ArrayList<Record> items) {
        this.items = items;
    }

    public ArrayList<String> getSortedArtistsArray() {
        ArrayList<String> list = new ArrayList<>();
        for (Record record : this.items){
            list.add(record.getArtist());
        }
        Set<String> s = new HashSet<>(list);
        s.remove(null);
        s.remove("");
        ArrayList<String> newArray = new ArrayList<>();
        for (String string : s){
            newArray.add(string);
        }
        Collections.sort(newArray);
        return newArray;
    }

    public ArrayList<String> getSortedYearsArray() {
        ArrayList<String> list = new ArrayList<>();
        for (Record record : this.items){
            list.add(record.getYear());
        }
        Set<String> s = new HashSet<>(list);
        s.remove(null);
        s.remove("");
        ArrayList<String> newArray = new ArrayList<>();
        for (String string : s){
            newArray.add(string);
        }
        Collections.sort(newArray);
        return newArray;
    }

    public ArrayList<String> getRecordStrings(){
        ArrayList<String> recordsString = new ArrayList<>();
        for (Record record : this.items){
            String year = record.getYear();
            String artist = record.getArtist();
            String length = record.getLength();
            String title = record.getTitle();
            String webDirectory = record.getWebDirectory();
            recordsString.add("" + artist + " " +  year + " " + title);
        }
        return recordsString;
    }

    public ArrayList<Integer> getIndexesOfMatchingRecords(String text){
        ArrayList<Integer> indexArray = new ArrayList<>();
        String [] keyWords = text.split("\\w+\\s");
        int i = 0;
        for (String recordString : getRecordStrings()){
            if (recordString.toLowerCase(Locale.getDefault()).contains(text)){
                indexArray.add(i);
            }
            i++;
        }
        return indexArray;
    }

    public ArrayList<String> getRecordLengths(){
        ArrayList<String> array = new ArrayList<>();
        for (Record record : items){
            array.add(record.getLength());
        }
        return array;
    }

    public ArrayList<String> getRecordTitles(){
        ArrayList<String> array = new ArrayList<>();
        for (Record record : items){
            array.add(record.getTitle());
        }
        return array;
    }

    public ArrayList<String> getRecordArtists(){
        ArrayList<String> array = new ArrayList<>();
        for (Record record : items){
            array.add(record.getArtist());
        }
        return array;
    }

    public ArrayList<String> getRecordWebDirectories(){
        ArrayList<String> array = new ArrayList<>();
        for (Record record : items){
            array.add(record.getWebDirectory());
        }
        return array;
    }

    public ArrayList<String> getRecordStrings(ArrayList<Integer> indexes) {
        ArrayList<String> array = getRecordStrings();
        ArrayList<String> newArray = new ArrayList<>();
        for (Integer index : indexes){
            newArray.add(array.get(index));
        }
        return newArray;
    }

    public ArrayList<String> getRecordTitles(ArrayList<Integer> indexes) {
        ArrayList<String> array = getRecordTitles();
        ArrayList<String> newArray = new ArrayList<>();
        for (Integer index : indexes){
            newArray.add(array.get(index));
        }
        return newArray;
    }

    public ArrayList<String> getRecordLengths(ArrayList<Integer> indexes) {
        ArrayList<String> array = getRecordLengths();
        ArrayList<String> newArray = new ArrayList<>();
        for (Integer index : indexes){
            newArray.add(array.get(index));
        }
        return newArray;
    }

    public ArrayList<String> getRecordArtists(ArrayList<Integer> indexes) {
        ArrayList<String> array = getRecordArtists();
        ArrayList<String> newArray = new ArrayList<>();
        for (Integer index : indexes){
            newArray.add(array.get(index));
        }
        return newArray;
    }
}
