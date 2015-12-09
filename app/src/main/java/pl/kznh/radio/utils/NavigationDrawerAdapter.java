package pl.kznh.radio.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import pl.kznh.radio.R;


public class NavigationDrawerAdapter extends ArrayAdapter<String> {

    protected Context mContext;
    protected String [] mTitles;
    protected int [] mIconsRes;


    public NavigationDrawerAdapter(Context mContext, int resource, String[] titles, int [] iconsRes) {
        super(mContext, resource, titles);
        this.mContext = mContext;
        this.mTitles = titles;
        this.mIconsRes = iconsRes;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.navigation_menu_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.iconView = (ImageView) convertView.findViewById(R.id.iconView);
        holder.titleView = (TextView) convertView.findViewById(R.id.titleView);

        holder.iconView.setImageResource(mIconsRes[position]);
        holder.titleView.setText(mTitles[position]);

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconView;
        TextView titleView;

    }
}






