package pl.kznh.radio.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.aprilapps.switcher.Switcher;
import pl.kznh.radio.R;
import pl.kznh.radio.gson.plan.ToRead;
import pl.kznh.radio.utils.Constants;
import pl.kznh.radio.utils.ReadingPlanAdapter;
import pl.kznh.radio.utils.TypefaceSpan;

/**
 * Created by SzymonN on 2015-11-30.
 */
public class ReadingPlanFragment extends Fragment{

    private ListView mListView;

    private List<String> mDates = new ArrayList<>();

    private List<String> mBookNames = new ArrayList<>();

    private List<String> mAnnotations = new ArrayList<>();

    private List<String> mMySwordAbbeviations = new ArrayList<>();

    private List<String> mBibleOnlineNames = new ArrayList<>();

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
        mSwitcher = new Switcher.Builder(getActivity())
                .addContentView(view.findViewById(R.id.rootView))
                .addProgressView(view.findViewById(R.id.progress_view)) //progress view member
                .addErrorView(view.findViewById(R.id.error_view))
                .setErrorLabel((TextView) view.findViewById(R.id.errorTextView))
                .build();
        mSwitcher.showProgressViewImmediately();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mListView = (ListView) view.findViewById(R.id.listView);

        SimpleDateFormat queryDateFormat = new SimpleDateFormat("dd-MM");
        Firebase ref = new Firebase("https://resplendent-torch-429.firebaseio.com/" + Constants.CLASS_READING_PLAN);
        if (isNetworkAvailable()){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.YEAR, 2015);
            Date date = calendar.getTime();
            Log.i("QUERY_DATE", queryDateFormat.format(date));
        Query queryRef = ref.orderByChild(Constants.KEY_PLAN_DATE).equalTo(queryDateFormat.format(date));
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists() || snapshot == null) {
                    mSwitcher.showErrorView(String.format("%s", getString(R.string.error_unknown)));
                } else {


                    System.out.println(snapshot.getValue());
                    mSwitcher.showContentView();
                    for (DataSnapshot toReadSnapshot : snapshot.getChildren()) {
                        ToRead toRead = toReadSnapshot.getValue(ToRead.class);
                        mBookNames.add(toRead.getBookName());
                        mDates.add(toRead.getDate());
                        mAnnotations.add(getAnnotation(
                                toRead.getFirstChapter(),
                                toRead.getFirstVerse(),
                                toRead.getLastChapter(),
                                toRead.getLastVerse()));
                        mMySwordAbbeviations.add(toRead.getMySwordAbbreviation());
                        mBookNumbers.add(toRead.getBookNumber());
                        mFirstChapters.add(toRead.getFirstChapter());
                        mBibleOnlineNames.add(toRead.getBibleOnlineName());
                        mListView.setAdapter(new ReadingPlanAdapter(mContext, android.R.layout.simple_list_item_1, mBookNames, mBookNumbers, mFirstChapters, mAnnotations, mMySwordAbbeviations, mBibleOnlineNames));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mSwitcher.showErrorView(String.format("%s\n%s %s", getString(R.string.error_unknown), getString(R.string.error_code), firebaseError.getCode()));
            }
        });
        } else {
            mSwitcher.showErrorView(String.format("%s", getString(R.string.error_unknown)));
        }
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
    }

    public void setActionBarTitle (int titleRes) {
        SpannableString s = new SpannableString(getString(titleRes));
        s.setSpan(new TypefaceSpan(getActivity(), Constants.FONT_NAME), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(s);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
