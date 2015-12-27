package pl.kznh.radio.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pl.kznh.radio.R;
import pl.kznh.radio.activities.MediaPlayerActivity;
import pl.kznh.radio.services.RecordPlayerService;
import pl.kznh.radio.utils.Constants;
import pl.kznh.radio.utils.RadiosAdapter;
import pl.kznh.radio.utils.TypefaceSpan;

/**
 * Created by SzymonN on 2015-11-30.
 */
public class RadioFragment extends Fragment implements AdapterView.OnItemClickListener {

    private String [] mRadioNames;

    private String [] mRadioOwners;

    private SharedPreferences mSharedPreferences;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio, container, false);
        setActionBarTitle(R.string.choose_radio);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ListView radiosList = (ListView) view.findViewById(R.id.radiosList);
        mRadioNames = getResources().getStringArray(R.array.radio_names_array);
        mRadioOwners = getResources().getStringArray(R.array.radio_owners_array);
        radiosList.setOnItemClickListener(this);
        RadiosAdapter adapter = new RadiosAdapter(getActivity(), android.R.layout.simple_list_item_1, mRadioNames, mRadioOwners);
        radiosList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String [] urlArray = getResources().getStringArray(R.array.radio_url_array);
        boolean shouldShowDialog = mSharedPreferences.getBoolean(Constants.PREF_SHOW_DIALOG, true);
        Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
        intent.putExtra(Constants.EXTRA_TITLE, mRadioNames[position]);
        intent.putExtra(Constants.EXTRA_SPEAKER, mRadioOwners[position]);
        intent.putExtra(Constants.EXTRA_LENGTH, 0);
        intent.putExtra(Constants.EXTRA_URL, urlArray[position]);
        intent.putExtra(Constants.EXTRA_IS_RADIO, true);
        if (RecordPlayerService.isServiceRunning){
            Toast.makeText(getActivity(), R.string.close_current_player, Toast.LENGTH_SHORT).show();
        } else if (!isNetworkAvailable()){
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        } else {
            if (!isWifiConnected() && shouldShowDialog){
                showInternetUsageDialog(intent);
            } else {
                startActivity(intent);
            }
        }
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
