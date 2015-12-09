package pl.kznh.radio.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.kznh.radio.R;

/**
 * Created by SzymonN on 2015-12-06.
 */
public class ArrayAdapterWithHint extends ArrayAdapter<String> {

    Context mContext;

    public ArrayAdapterWithHint(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = super.getView(position, convertView, parent);
        if (position == getCount()) {
            ((TextView)v.findViewById(android.R.id.text1)).setText("");
            ((TextView)v.findViewById(android.R.id.text1)).setHintTextColor(ContextCompat.getColor(mContext, R.color.primary));
            ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
        }

        return v;
    }

    @Override
    public int getCount() {
        return super.getCount()-1; // you dont display last item. It is used as hint.
    }
}
