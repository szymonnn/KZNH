package pl.kznh.radio.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import pl.kznh.radio.R;
import pl.kznh.radio.activities.MediaPlayerActivity;
import pl.kznh.radio.gson.record.RecordContainer;
import pl.kznh.radio.services.RecordPlayerService;
import pl.kznh.radio.utils.Constants;
import pl.kznh.radio.utils.RecordsAdapter;
import pl.kznh.radio.utils.TypefaceSpan;

/**
 * Created by Szymon Nitecki on 2015-11-30.
 */
public class RecordsFragment extends Fragment {

    ImageButton mSpeakerButton;

    ImageButton mYearButton;

    ListView mRecordsList;

    EditText mSearchField;

    private ImageView mProgressView;

    private TextView mErrorTextView;

    private LinearLayout mFormLayout;

    private RecordContainer mRecordContainer;

    private ArrayList<Integer> mIndexesOfFilteredRecords;

    private String mChoosenYear = "";

    private String mChoosenSpeaker = "";

    private SharedPreferences mSharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setActionBarTitle(R.string.title_section4);

        bindViews(view);

        setProgressBarVisible(true);

        getData();
        return view;
    }

    private void bindViews(View view) {
        mRecordsList = (ListView) view.findViewById(R.id.recordsList);
        mSpeakerButton = (ImageButton) view.findViewById(R.id.speakerButton);
        mYearButton = (ImageButton) view.findViewById(R.id.yearButton);
        mProgressView = (ImageView) view.findViewById(R.id.progressView);
        mErrorTextView = (TextView) view.findViewById(R.id.errorTextView);
        mSearchField = (EditText) view.findViewById(R.id.searchField);
        mFormLayout = (LinearLayout) view.findViewById(R.id.formLayout);

        mErrorTextView.setTypeface(Constants.robotoCondensed);
        mSearchField.setTypeface(Constants.robotoCondensed);
    }

    private void setProgressBarVisible(boolean b) {
        if (b) {
            Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_animation);
            mProgressView.startAnimation(rotateAnimation);
        } else {
            mProgressView.setVisibility(View.INVISIBLE);
        }
    }

    // method responsible for getting data from API, after all showView() is called
    private void getData() {
        AsyncTask task = new AsyncTask(){
            @Override
            protected String doInBackground(Object[] params) {
                try {
                    URL url = new URL("http://kznh.pl/nagrania/kazania/lib.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    InputStream inputStream = connection.getInputStream();
                    StringBuilder buffer = new StringBuilder();

                    Reader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = ((BufferedReader) reader).readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line).append("\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    return buffer.toString();

                } catch (Exception ex) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Object json) {
                super.onPostExecute(json);
                if (json.equals("")){
                    mProgressView.clearAnimation();
                    mProgressView.setVisibility(View.INVISIBLE);
                    mErrorTextView.setText(R.string.error_unknown);
                } else {
                    try {
                        JSONObject responseJsonObject = new JSONObject((String)json);
                        Iterator iterator = responseJsonObject.keys();
                        JSONArray jsonArray = new JSONArray();

                        while (iterator.hasNext()){
                            String key = (String) iterator.next();
                            jsonArray.put(responseJsonObject.get(key));
                        }

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("items", jsonArray);
                        Gson gson = new Gson();
                        mRecordContainer = gson.fromJson(jsonObject.toString(), RecordContainer.class);
//                        Log.i("hjln", mRecords.get(0).getArtist() + " "
//                                + mRecords.get(0).getLength() + " "
//                                + mRecords.get(0).getWebDirectory() + " "
//                                + mRecords.get(0).getTitle() + " "
//                                + mRecords.get(0).getYear() + " ");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    showView();

                }
            }
        };
        task.execute();
    }


    // method resposible for configuring actions
    private void showView() {
        mProgressView.clearAnimation();
        setProgressBarVisible(false);
        mRecordsList.setVisibility(View.VISIBLE);
        mYearButton.setVisibility(View.VISIBLE);
        mYearButton.setVisibility(View.VISIBLE);
        mSearchField.setVisibility(View.VISIBLE);
        mFormLayout.setVisibility(View.VISIBLE);


        configureFilterButtons();

        refreshAdapter();
        mRecordsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean shouldShowDialog = mSharedPreferences.getBoolean(Constants.PREF_SHOW_DIALOG, true);
                Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
                intent.putExtra(Constants.EXTRA_TITLE, mRecordContainer.getRecordTitles(mIndexesOfFilteredRecords).get(position));
                intent.putExtra(Constants.EXTRA_SPEAKER, mRecordContainer.getRecordArtists(mIndexesOfFilteredRecords).get(position));
                intent.putExtra(Constants.EXTRA_LENGTH, mRecordContainer.getRecordLengths(mIndexesOfFilteredRecords).get(position));
                intent.putExtra(Constants.EXTRA_URL, Constants.BASE_URL + mRecordContainer.getRecordWebDirectories(mIndexesOfFilteredRecords).get(position));
                if (RecordPlayerService.isServiceRunning) {
                    Toast.makeText(getActivity(), R.string.close_current_player, Toast.LENGTH_SHORT).show();
                } else if (!isNetworkAvailable()) {
                    Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                } else {
                    if (!isWifiConnected() && shouldShowDialog) {
                        showInternetUsageDialog(intent);
                    } else {
                        startActivity(intent);
                    }
                }
            }
        });

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshAdapter();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void configureFilterButtons() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] items = null;
                int title = 0;
                final int id = v.getId();
                switch (id){
                    case R.id.yearButton:
                        ArrayList<String> yearArray = mRecordContainer.getSortedYearsArray();
                        yearArray.add(0, getString(R.string.all_years));
                        items = new String[yearArray.size()];
                        yearArray.toArray(items);
                        title = R.string.choose_year;
                        break;
                    case R.id.speakerButton:
                        ArrayList<String> speakerArray = mRecordContainer.getSortedArtistsArray();
                        speakerArray.add(0, getString(R.string.all_speakers));
                        items = new String[speakerArray.size()];
                        speakerArray.toArray(items);
                        title = R.string.choose_speaker;
                        break;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final String[] finalItems = items;
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (id){
                            case R.id.yearButton:
                                if (which != 0) {
                                    mChoosenYear = finalItems[which];
                                } else {
                                    mChoosenYear = "";
                                }
                                break;
                            case R.id.speakerButton:
                                if (which != 0) {
                                    mChoosenSpeaker = finalItems[which];
                                } else {
                                    mChoosenSpeaker = "";
                                }
                                break;
                        }
                        refreshAdapter();
                        dialog.dismiss();
                    }
                });
                View view = getActivity().getLayoutInflater().inflate(R.layout.default_alert_dialog_title, null);
                TextView titleView = (TextView) view.findViewById(R.id.titleView);
                titleView.setText(title);
                builder.setCustomTitle(view);
                builder.setNegativeButton(R.string.back, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };

        mYearButton.setOnClickListener(listener);
        mSpeakerButton.setOnClickListener(listener);
    }

    // creating new adapter and setting to list view
    private void refreshAdapter() {
        String text = (mSearchField.getText().toString() + " " + mChoosenSpeaker + " " + mChoosenYear).toLowerCase(Locale.getDefault());
        mIndexesOfFilteredRecords = mRecordContainer.getIndexesOfMatchingRecords(text);

        RecordsAdapter recordsAdapter = new RecordsAdapter(getActivity(), android.R.layout.simple_list_item_1,
                mRecordContainer.getRecordStrings(mIndexesOfFilteredRecords),
                mRecordContainer.getRecordTitles(mIndexesOfFilteredRecords),
                mRecordContainer.getRecordLengths(mIndexesOfFilteredRecords),
                mRecordContainer.getRecordArtists(mIndexesOfFilteredRecords));
        mRecordsList.setAdapter(recordsAdapter);
    }

    public void setActionBarTitle (int titleRes) {
        SpannableString s = new SpannableString(getString(titleRes));
        s.setSpan(new TypefaceSpan(getActivity(), Constants.FONT_NAME), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(s);
    }

    private boolean isWifiConnected () {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        return activeNetwork != null && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
    }

    private boolean isNetworkAvailable (){
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        return activeNetwork != null;
    }

    private void showInternetUsageDialog(final Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.internet_usage_dialog, null);
        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox);
        TextView warningTextView = (TextView)view.findViewById(R.id.textView2);
        checkBox.setTypeface(Constants.robotoCondensed);
        warningTextView.setTypeface(Constants.robotoCondensed);
        builder.setView(view);
        builder.setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkBox.isChecked()) {
                    mSharedPreferences.edit().putBoolean(Constants.PREF_SHOW_DIALOG, false).apply();
                    dialog.dismiss();
                }
                startActivity(intent);
            }
        });
        View titleView = inflater.inflate(R.layout.default_alert_dialog_title, null);
        TextView titleTextView = (TextView) titleView.findViewById(R.id.titleView);
        titleTextView.setTypeface(Constants.robotoCondensed);
        titleTextView.setText(R.string.warning);
        builder.setCustomTitle(titleView);
        builder.setNegativeButton(R.string.go_to_preferences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkBox.isChecked()) {
                    mSharedPreferences.edit().putBoolean(Constants.PREF_SHOW_DIALOG, false).apply();
                }
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
