package pl.kznh.radio.gson.plan;

/**
 * Created by SzymonN on 2016-02-13.
 */
public class ToRead {
    private String date;
    private int dayOfMounth;
    private String s;
    private String bookName;
    private int bookNumber;
    private String mySwordAbbreviation;
    private String bookShortName;
    private int firstChapter;
    private int lastChapter;
    private int firstVerse;
    private int lastVerse;
    private String bibleOnlineName;

    public void setBibleOnlineName(String bibleOnlineName) {
        this.bibleOnlineName = bibleOnlineName;
    }

    public ToRead (){}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDayOfMounth() {
        return dayOfMounth;
    }

    public void setDayOfMounth(int dayOfMounth) {
        this.dayOfMounth = dayOfMounth;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getMySwordAbbreviation() {
        return mySwordAbbreviation;
    }

    public void setMySwordAbbreviation(String mySwordAbbreviation) {
        this.mySwordAbbreviation = mySwordAbbreviation;
    }

    public String getBookShortName() {
        return bookShortName;
    }

    public void setBookShortName(String bookShortName) {
        this.bookShortName = bookShortName;
    }

    public int getFirstChapter() {
        return firstChapter;
    }

    public void setFirstChapter(int firstChapter) {
        this.firstChapter = firstChapter;
    }

    public int getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(int lastChapter) {
        this.lastChapter = lastChapter;
    }

    public int getFirstVerse() {
        return firstVerse;
    }

    public void setFirstVerse(int firstVerse) {
        this.firstVerse = firstVerse;
    }

    public int getLastVerse() {
        return lastVerse;
    }

    public void setLastVerse(int lastVerse) {
        this.lastVerse = lastVerse;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getBibleOnlineName() {
        return bibleOnlineName;
    }
}
