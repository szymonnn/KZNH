package pl.kznh.radio.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.kznh.radio.R;


public class RecordsAdapter extends ArrayAdapter<String> {

    protected Context mContext;
    protected ArrayList<String> mRecordStrings;
    protected ArrayList<String> mTitles;
    protected ArrayList<String> mLengths;
    protected ArrayList<String> mSpeakers;


    public RecordsAdapter(Context mContext,
                          int resource,
                          ArrayList<String> recordStrings,
                          ArrayList<String> titles,
                          ArrayList<String> lengths,
                          ArrayList<String> speakers) {
        super(mContext, resource, recordStrings);
        this.mContext = mContext;
        this.mLengths = lengths;
        this.mRecordStrings = recordStrings;
        this.mSpeakers = speakers;
        this.mTitles = titles;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.records_list_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.speakerView = (TextView) convertView.findViewById(R.id.speakerView);
        holder.titleView = (TextView) convertView.findViewById(R.id.titleView);
        holder.lengthView = (TextView)convertView.findViewById(R.id.lengthView);

        holder.speakerView.setTypeface(Constants.robotoCondensed);
        holder.titleView.setTypeface(Constants.robotoCondensed);
        holder.lengthView.setTypeface(Constants.robotoCondensed);

        holder.speakerView.setText(mSpeakers.get(position));
        holder.titleView.setText(mTitles.get(position));
        holder.lengthView.setText(mLengths.get(position));

        return convertView;
    }

    private static class ViewHolder {
        TextView titleView;
        TextView speakerView;
        TextView lengthView;

    }
}






