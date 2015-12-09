package pl.kznh.radio.gson.record;

import com.google.gson.annotations.SerializedName;

/**
 * Created by SzymonN on 2015-12-07.
 */
public class Record {
    private String title;
    @SerializedName("web-directory")
    private String webDirectory;
    private String year;
    private String lenght;
    private String artist;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebDirectory() {
        return webDirectory;
    }

    public void setWebDirectory(String webDirectory) {
        this.webDirectory = webDirectory;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLength() {
        return lenght;
    }

    public void setLength(String length) {
        this.lenght = length;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
