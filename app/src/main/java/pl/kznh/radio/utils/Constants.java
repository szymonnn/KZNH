package pl.kznh.radio.utils;

import android.graphics.Typeface;

/**
 * Created by SzymonN on 2015-12-27.
 */
public class Constants {

    // MediaPlayerActivity notification id
    public static final int NOTIFICATION_ID = 123;

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

    public static final String PREF_SHOW_DIALOG = "should-show-internet-usage-dialog";

    public static final String EXTRA_ACTION_FROM_NOTIFICATION = "action-from-notification";

    public static final int NOT_ACTION = 0;

    public static final int ACTION_FROM_NOTIFICATION_STOP_SERVICE = 11;

    public static final int ACTION_FROM_NOTIFICATION_PLAY = 12;

    public static final int ACTION_FROM_NOTIFICATION_PAUSE = 13;


    public static Typeface robotoCondensed;
}
