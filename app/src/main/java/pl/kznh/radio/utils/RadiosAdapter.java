package pl.kznh.radio.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pl.kznh.radio.R;


public class RadiosAdapter extends ArrayAdapter<String> {

    protected Context mContext;
    protected String [] mNames;
    protected String [] mOwners;


    public RadiosAdapter(Context mContext,
                         int resource,
                         String[] names,
                         String[] owners) {
        super(mContext, resource, names);
        this.mContext = mContext;
        this.mOwners = owners;
        this.mNames = names;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.radios_list_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.ownerView = (TextView) convertView.findViewById(R.id.ownerView);
        holder.nameView = (TextView) convertView.findViewById(R.id.nameView);
        holder.lengthView = (TextView)convertView.findViewById(R.id.lengthView);

        holder.ownerView.setTypeface(Constants.robotoCondensed);
        holder.lengthView.setTypeface(Constants.robotoCondensed);
        holder.nameView.setTypeface(Constants.robotoCondensed);


        holder.ownerView.setText(mOwners[position]);
        holder.nameView.setText(mNames[position]);

        return convertView;
    }

    private static class ViewHolder {
        TextView nameView;
        TextView ownerView;
        TextView lengthView;

    }
}






