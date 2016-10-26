package pl.kznh.radio.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pl.kznh.radio.R;
import pl.kznh.radio.utils.Constants;
import pl.kznh.radio.utils.TypefaceSpan;

/**
 * Created by SzymonN on 2015-11-30.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setActionBarTitle(R.string.title_section1);

        Button facebookButton = (Button) view.findViewById(R.id.facebookButton);
        Button kznhplButton = (Button) view.findViewById(R.id.kznhplButton);
        TextView titleView = (TextView) view.findViewById(R.id.titleView);

        facebookButton.setTypeface(Constants.robotoCondensed);
        kznhplButton.setTypeface(Constants.robotoCondensed);
        titleView.setTypeface(Constants.robotoCondensed);

        facebookButton.setOnClickListener(this);
        kznhplButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.facebookButton:
                startActivity(getFacebookIntent());
                break;
            case R.id.kznhplButton:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kznh.pl")));
                break;
        }
    }

    private Intent getFacebookIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/KZNowaHuta"));
    }

    public void setActionBarTitle (int titleRes) {
        SpannableString s = new SpannableString(getString(titleRes));
        s.setSpan(new TypefaceSpan(getActivity(), Constants.FONT_NAME), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(s);
    }
}
