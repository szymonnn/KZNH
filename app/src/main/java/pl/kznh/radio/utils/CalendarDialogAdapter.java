package pl.kznh.radio.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pl.kznh.radio.R;


public class CalendarDialogAdapter extends ArrayAdapter<String> {

    protected Context mContext;
    protected ArrayList<String> mDescriptions;
    protected ArrayList<Date> mStartTimes;
    protected ArrayList<Date> mEndTimes;


    public CalendarDialogAdapter(Context context, int resource, ArrayList<String> descriptions, ArrayList<Date> startTimes, ArrayList<Date> endTimes) {
        super(context, resource, descriptions);
        this.mContext = context;
        this.mDescriptions = descriptions;
        this.mStartTimes = startTimes;
        this.mEndTimes = endTimes;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.calendar_dialog_list_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.timeView = (TextView) convertView.findViewById(R.id.timeView);
        holder.descriptionView = (TextView) convertView.findViewById(R.id.descriptionView);

        holder.timeView.setTypeface(Constants.robotoCondensed);
        holder.descriptionView.setTypeface(Constants.robotoCondensed);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        holder.timeView.setText(sdf.format(mStartTimes.get(position)) + " - " + sdf.format(mEndTimes.get(position)));
        holder.descriptionView.setText(mDescriptions.get(position));

        return convertView;
    }

    private static class ViewHolder {
        TextView timeView;
        TextView descriptionView;

    }
}






