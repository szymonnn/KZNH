package pl.kznh.radio.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import pl.kznh.radio.R;
import pl.kznh.radio.activities.MediaPlayerActivity;
import pl.kznh.radio.services.RecordPlayerService;
import pl.kznh.radio.utils.RadiosAdapter;

/**
 * Created by SzymonN on 2015-11-30.
 */
public class RadioFragment extends Fragment implements AdapterView.OnItemClickListener {

    private String [] mRadioNames;

    private String [] mRadioOwners;

    public static String EXTRA_IS_RADIO = "is-radio";

    private ListView mRadiosList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio, container, false);
        setActionBarTitle(R.string.choose_radio);
        mRadiosList = (ListView) view.findViewById(R.id.radiosList);
        mRadioNames = getResources().getStringArray(R.array.radio_names_array);
        mRadioOwners = getResources().getStringArray(R.array.radio_owners_array);
        mRadiosList.setOnItemClickListener(this);
        RadiosAdapter adapter = new RadiosAdapter(getActivity(), android.R.layout.simple_list_item_1, mRadioNames, mRadioOwners);
        mRadiosList.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String [] urlArray = getResources().getStringArray(R.array.radio_url_array);
        Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
        intent.putExtra(RecordsFragment.EXTRA_TITLE, mRadioNames[position]);
        intent.putExtra(RecordsFragment.EXTRA_SPEAKER, mRadioOwners[position]);
        intent.putExtra(RecordsFragment.EXTRA_LENGTH, 0);
        intent.putExtra(RecordsFragment.EXTRA_URL, urlArray[position]);
        intent.putExtra(RadioFragment.EXTRA_IS_RADIO, true);
        if (RecordPlayerService.isServiceRunning){
            Toast.makeText(getActivity(), R.string.close_current_player, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intent);
        }
    }

    public void setActionBarTitle (int titleRes) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(titleRes);
    }
}
