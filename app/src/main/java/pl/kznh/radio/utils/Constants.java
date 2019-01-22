package pl.kznh.radio.utils;

import android.graphics.Typeface;

/**
 * Created by SzymonN on 2015-12-27.
 */
public class Constants {

    // MediaPlayerActivity notification id
    public static final int NOTIFICATION_ID = 624;

    // API key used for gogle calendar
    public static final String GOOGLE_API_KEY = "AIzaSyDImE-l31E2GFbELJhgHyYLP8KWAaJLyCI";

    // time radius used in calendar API call
    public static final long TIME_RADIUS = 15778476 * 1000L;//half year

    // indicates if MediaPlayerActivity should be opened as radio
    public static final String EXTRA_IS_RADIO = "is-radio";

    // base URL used for records
    public static final String BASE_URL = "http://kznh.pl/";

    // intent extras
    public static final String EXTRA_TITLE = "title";

    public static final String EXTRA_SPEAKER = "speaker";

    public static final String EXTRA_LENGTH = "length";

    public static final String EXTRA_URL = "url";

    // used for broadcast receiver
    public static final String NOTIFICATION = "pl.kznh.radio.services";

    public static final String MEDIA_PLAYER_RESULT_WHAT = "mp-what";

    public static final String MEDIA_PLAYER_RESULT_EXTRA = "mp-extra";

    public static final String MEDIA_PLAYER_ACTION = "action";

    // states of mediaplayer
    public static final int ACTION_ON_ERROR = 0;

    public static final int ACTION_ON_BUFFERING_UPDATE = 1;

    public static final String MEDIA_PLAYER_BUFFERING_PERCENTAGE = "buffering-percentage";

    public static final String FONT_NAME = "fonts/Roboto-Light.ttf";

    public static final String PREF_SHOW_INTERNET_USAGE_DIALOG = "should-show-internet-usage-dialog";

    public static final String PREF_SHOW_MYSWORD_DIALOG = "should-show-mysword-dialog";

    public static final String EXTRA_ACTION_FROM_NOTIFICATION = "action-from-notification";

    public static final int NOT_ACTION = 0;

    public static final int ACTION_FROM_NOTIFICATION_STOP_SERVICE = 11;

    public static final int ACTION_FROM_NOTIFICATION_PLAY = 12;

    public static final int ACTION_FROM_NOTIFICATION_PAUSE = 13;

    public static final int AMOUNT_OF_FRAGMENTS_TO_READ = 1095;

    public static Typeface robotoCondensed;

    public static String EXTRA_IS_NEW_INTENT = "is-new-intent";

    public static final String KEY_PLAN_BOOK_NAME = "BookName";

    public static final String KEY_PLAN_BOOK_NUMBER = "BookNumber";

    public static final String KEY_PLAN_DATE = "date";

    public static final String KEY_PLAN_FIRST_CHAPTER = "FirstChapter";

    public static final String KEY_PLAN_FIRST_VERSE = "FirstVerse";

    public static final String KEY_PLAN_LAST_CHAPTER = "LastChapter";

    public static final String KEY_PLAN_LAST_VERSE = "LastVerse";

    public static final String KEY_PLAN_MY_SWORD_ABBREVIATION = "MySwordAbbreviation";

    public static final String CLASS_READING_PLAN = "ReadingPlan";

    public static final String PREF_READ_FRAGMENTS = "read_positions";
}
