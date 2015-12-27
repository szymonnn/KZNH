package pl.kznh.radio;

import android.app.Application;
import android.graphics.Typeface;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseInstallation;
import io.fabric.sdk.android.Fabric;
import pl.kznh.radio.utils.Constants;

/**
 * Created by SzymonN on 2015-12-24.
 */
public class KznhApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Constants.robotoCondensed = Typeface.createFromAsset(getAssets(),Constants.FONT_NAME);
        Parse.initialize(this, "3BAAeXprUZdmQ6NcanLdQ8WXxrtW8c7TyzhyMmpv", "PGOAtJNMrngb99wClTlsnL51rXYDWUDlL8B9nuc5");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
