package pl.kznh.radio.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import pl.aprilapps.switcher.Switcher;
import pl.kznh.radio.R;
import pl.kznh.radio.utils.Constants;
import pl.kznh.radio.utils.ReadingPlanAdapter;
import pl.kznh.radio.utils.TypefaceSpan;

/**
 * Created by SzymonN on 2015-11-30.
 */
public class ReadingPlanFragment extends Fragment{

    private ListView mListView;

    private TextView mPercentageTextView;

    private MaterialProgressBar mReadProgressBar;

    private List<String> mDates = new ArrayList<>();

    private List<String> mBookNames = new ArrayList<>();

    private List<String> mAnnotations = new ArrayList<>();

    private List<String> mMySwordAbbeviations = new ArrayList<>();

    private List<Integer> mBookNumbers = new ArrayList<>();

    private List<Integer> mFirstChapters = new ArrayList<>();

    private SharedPreferences mSharedPreferences;

    private Switcher mSwitcher;

    private Context mContext;

    private int mReadProgress;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_reading_plan, container, false);
        setActionBarTitle(R.string.fragments_for_today);
        ((TextView)view.findViewById(R.id.progressTextView)).setTypeface(Constants.robotoCondensed);
        ((TextView)view.findViewById(R.id.errorTextView)).setTypeface(Constants.robotoCondensed);
        ((TextView)view.findViewById(R.id.progressTView)).setTypeface(Constants.robotoCondensed);
        mSwitcher = new Switcher.Builder(getActivity())
                .addContentView(view.findViewById(R.id.rootView))
                .addProgressView(view.findViewById(R.id.progress_view)) //progress view member
                .addErrorView(view.findViewById(R.id.error_view))
                .setErrorLabel((TextView) view.findViewById(R.id.errorTextView))
                .build();
        mSwitcher.showProgressViewImmediately();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mListView = (ListView) view.findViewById(R.id.listView);
        mReadProgressBar = (MaterialProgressBar)view.findViewById(R.id.readMaterialProgressBar);
        mPercentageTextView = (TextView) view.findViewById(R.id.percentageTextView);

        mPercentageTextView.setTypeface(Constants.robotoCondensed);

        SimpleDateFormat queryDateFormat = new SimpleDateFormat("DD-MM");
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.CLASS_READING_PLAN);
        query.whereEqualTo(Constants.KEY_PLAN_DATE, queryDateFormat.format(new Date()));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mSwitcher.showContentView();
                    setProgress();
                    for (ParseObject object : objects) {
                        mBookNames.add(object.getString(Constants.KEY_PLAN_BOOK_NAME));
                        mDates.add(object.getString(Constants.KEY_PLAN_DATE));
                        mAnnotations.add(getAnnotation(
                                object.getInt(Constants.KEY_PLAN_FIRST_CHAPTER),
                                object.getInt(Constants.KEY_PLAN_FIRST_VERSE),
                                object.getInt(Constants.KEY_PLAN_LAST_CHAPTER),
                                object.getInt(Constants.KEY_PLAN_LAST_VERSE)));
                        mMySwordAbbeviations.add(object.getString(Constants.KEY_PLAN_MY_SWORD_ABBREVIATION));
                        mBookNumbers.add(object.getInt(Constants.KEY_PLAN_BOOK_NUMBER));
                        mFirstChapters.add(object.getInt(Constants.KEY_PLAN_FIRST_CHAPTER));
                    }
                    mListView.setAdapter(new ReadingPlanAdapter(mContext, android.R.layout.simple_list_item_1, mBookNames, mBookNumbers, mFirstChapters, mAnnotations, mMySwordAbbeviations));
                } else {
                    mSwitcher.showErrorView(String.format("%s\n%s %s", getString(R.string.error_unknown), getString(R.string.error_code), e.getCode()));
                    e.printStackTrace();
                }
            }
        });
        return view;
    }


    public String getAnnotation (int firstChapter, int firstVerse, int lastChapter, int lastVerse){
        if (firstVerse == 1 && lastChapter == 0 && lastVerse == 0){
            return String.valueOf(firstChapter);
        }

        if (firstVerse == 1 && lastChapter != 0 && lastVerse == 0){
            return firstChapter + "-" + lastChapter;
        }

        if (lastChapter == 0 && lastVerse != 0){
            return firstChapter + ":" + firstVerse + "-" + lastVerse;
        }

        if (lastChapter != 0 && lastVerse != 0){
            return firstChapter + ":" + firstVerse + "-" + lastChapter + ":" + lastVerse;
        }

        return "";
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onPause() {
        super.onPause();
        setProgress();
    }

    private void setProgress() {
        mReadProgress = mSharedPreferences.getStringSet(Constants.PREF_READ_FRAGMENTS, new HashSet<String>()).size();
        mReadProgressBar.setMax(Constants.AMOUNT_OF_FRAGMENTS_TO_READ);
        mReadProgressBar.setProgress(mReadProgress);
        double progressPercent = (double)mReadProgress*100 / Constants.AMOUNT_OF_FRAGMENTS_TO_READ;
        mPercentageTextView.setText(String.format("%.1f %s", progressPercent, "%"));
    }

    public void setActionBarTitle (int titleRes) {
        SpannableString s = new SpannableString(getString(titleRes));
        s.setSpan(new TypefaceSpan(getActivity(), Constants.FONT_NAME), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(s);
    }
}
