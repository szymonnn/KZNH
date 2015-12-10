package pl.kznh.radio.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import pl.kznh.radio.activities.MediaPlayerActivity;
import pl.kznh.radio.R;
import pl.kznh.radio.gson.record.Record;
import pl.kznh.radio.gson.record.RecordContainer;
import pl.kznh.radio.utils.RecordsAdapter;

/**
 * Created by Szymon Nitecki on 2015-11-30.
 */
public class RecordsFragment extends Fragment {

    public static final String EXTRA_TITLE = "title";

    public static final String EXTRA_SPEAKER = "speaker";

    public static final String EXTRA_LENGTH = "length";

    public static final String EXTRA_URL = "url";

    ImageButton mSpeakerButton;

    ImageButton mYearButton;

    ListView mRecordsList;

    EditText mSearchField;

    private ImageView mProgressView;

    private TextView mErrorTextView;

    private LinearLayout mFormLayout;

    private RecordContainer mRecordContainer;

    private ArrayList<Record> mRecords;

    private ArrayList<Integer> mIndexesOfFilteredRecords;

    private String mChoosenYear = "";

    private String mChoosenSpeaker = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);

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
    }

    private void setProgressBarVisible(boolean b) {
        if (b) {
            Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_animation);
            mProgressView.startAnimation(rotateAnimation);
        } else {
            mProgressView.setVisibility(View.INVISIBLE);
        }
    }

    // method responsible for getting data from API after all showView() is called
    private void getData() {
        AsyncTask task = new AsyncTask(){
            @Override
            protected String doInBackground(Object[] params) {
                try {
                    URL url = new URL("http://kznh.pl/nagrania/kazania/lib.json");
                    URLConnection urlConnection = url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
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
                        mRecords = mRecordContainer.getRecordArray();
                        Log.i("hjln", mRecords.get(0).getArtist() + " "
                                + mRecords.get(0).getLength() + " "
                                + mRecords.get(0).getWebDirectory() + " "
                                + mRecords.get(0).getTitle() + " "
                                + mRecords.get(0).getYear() + " ");

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
                Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
                intent.putExtra(EXTRA_TITLE, mRecordContainer.getRecordTitles(mIndexesOfFilteredRecords).get(position));
                intent.putExtra(EXTRA_SPEAKER, mRecordContainer.getRecordArtists(mIndexesOfFilteredRecords).get(position));
                intent.putExtra(EXTRA_LENGTH, mRecordContainer.getRecordLengths(mIndexesOfFilteredRecords).get(position));
                intent.putExtra(EXTRA_URL, mRecordContainer.getRecordWebDirectories(mIndexesOfFilteredRecords).get(position));
                startActivity(intent);
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
                View view = getActivity().getLayoutInflater().inflate(R.layout.records_alert_dialog_title, null);
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(titleRes);
    }
}
