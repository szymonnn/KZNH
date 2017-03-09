package pl.kznh.radio.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.kznh.radio.R;


public class ReadingPlanAdapter extends ArrayAdapter<String> {

    protected Context mContext;
    protected List<String> mBookNames;
    protected List<String> mAnnotations;
    protected List<String> mMySwordAbbeviations;
    protected List<Integer> mBookNumbers;
    protected List<Integer> mFirstChapters;
    protected List<String> mCurrentFragmentsToRead;
    protected Set<String> mReadFragments;
    protected SharedPreferences mSharedPreferences;
    protected List<String> mBibleOnlineNames;


    public ReadingPlanAdapter(Context mContext,
                              int resource,
                              List<String> bookNames,
                              List<Integer> bookNumbers,
                              List<Integer> firstChapters,
                              List<String> annotations,
                              List<String> mySwordAbreviations,
                              List<String> bibleOnlineNames) {
        super(mContext, resource, bookNames);
        this.mContext = mContext;
        this.mBookNames = bookNames;
        this.mBookNumbers = bookNumbers;
        this.mFirstChapters = firstChapters;
        this.mAnnotations = annotations;
        this.mMySwordAbbeviations = mySwordAbreviations;
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.mCurrentFragmentsToRead = new ArrayList<>();
        for (int i = 0; i < mAnnotations.size(); i++){
            mCurrentFragmentsToRead.add(mBookNames.get(i) + mAnnotations.get(i));
        }
        this.mReadFragments = mSharedPreferences.getStringSet(Constants.PREF_READ_FRAGMENTS, new HashSet<String>());
        this.mBibleOnlineNames = bibleOnlineNames;

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.reading_plan_list_item, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.bookView = (TextView) convertView.findViewById(R.id.bookView);
        holder.readButton = (Button) convertView.findViewById(R.id.readButton);
        holder.bibleIcon = (ImageView) convertView.findViewById(R.id.imageView);

        holder.bookView.setTypeface(Constants.robotoCondensed);
        holder.readButton.setTypeface(Constants.robotoCondensed);

        holder.bookView.setText(String.format("%s %s", mBookNames.get(position), mAnnotations.get(position)));
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        if (mReadFragments.size() >= Constants.AMOUNT_OF_FRAGMENTS_TO_READ){
                            mReadFragments = new HashSet<>();
                        }
                        mReadFragments.add(mCurrentFragmentsToRead.get(position));
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.clear();
                        editor.putStringSet(Constants.PREF_READ_FRAGMENTS, mReadFragments).apply();
                        holder.readButton.setBackgroundResource(R.drawable.grey_button);
                        holder.bookView.setTextColor(ContextCompat.getColor(mContext, R.color.inactive));
                        holder.bibleIcon.setImageResource(R.drawable.icon_check_inactive);
                        holder.bibleIcon.setBackgroundResource(R.drawable.menu_item_icon_background_inactive);

//                        try {
//                            Intent intent = new Intent();
//                            intent.setComponent(ComponentName.unflattenFromString(
//                                    "com.riversoft.android.mysword/com.riversoft.android.mysword.MySwordLink"));
//                            intent.setData(Uri.parse("http://mysword.info/b?r=" + mMySwordAbbeviations.get(position) + "_" + mAnnotations.get(position)));
//                            mContext.startActivity(intent);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            showMySwordDialog(position);
//                        }
                showMySwordDialog(position);
            }
        };
        holder.readButton.setOnClickListener(listener);

        if (mReadFragments.contains(mCurrentFragmentsToRead.get(position))){
            holder.readButton.setBackgroundResource(R.drawable.grey_button);
            holder.bookView.setTextColor(ContextCompat.getColor(mContext, R.color.inactive));
            holder.bibleIcon.setImageResource(R.drawable.icon_check_inactive);
            holder.bibleIcon.setBackgroundResource(R.drawable.menu_item_icon_background_inactive);
        } else {
            holder.readButton.setBackgroundResource(R.drawable.social_button);
            holder.bookView.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
            holder.bibleIcon.setImageResource(R.drawable.icon_bible);
            holder.bibleIcon.setBackgroundResource(R.drawable.menu_item_icon_background);
        }
        return convertView;
    }

    private void showMySwordDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = ((AppCompatActivity)mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.internet_usage_dialog, null);
        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox);
        TextView warningTextView = (TextView)view.findViewById(R.id.textView2);
        checkBox.setTypeface(Constants.robotoCondensed);
        warningTextView.setTypeface(Constants.robotoCondensed);
        warningTextView.setText(R.string.install_mysword);
        builder.setView(view);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkBox.isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(Constants.PREF_SHOW_MYSWORD_DIALOG, false).apply();
                    dialog.dismiss();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.riversoft.android.mysword"));
                mContext.startActivity(intent);
            }
        });
        View titleView = inflater.inflate(R.layout.default_alert_dialog_title, null);
        TextView titleTextView = (TextView) titleView.findViewById(R.id.titleView);
        titleTextView.setTypeface(Constants.robotoCondensed);
        titleTextView.setText(R.string.warning);
        builder.setCustomTitle(titleView);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkBox.isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putBoolean(Constants.PREF_SHOW_MYSWORD_DIALOG, false).apply();
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = "http://biblia-online.pl/Biblia/Warszawska/" + mBibleOnlineNames.get(position) + "/" + mFirstChapters.get(position) + "/" + mFirstChapters.get(position);
                intent.setData(Uri.parse(url));
                Toast.makeText(mContext, mContext.getString(R.string.read).toLowerCase() + ": " + mBookNames.get(position) + " " + mAnnotations.get(position), Toast.LENGTH_LONG).show();
                mContext.startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private static class ViewHolder {
        TextView bookView;
        Button readButton;
        ImageView bibleIcon;
    }
}






