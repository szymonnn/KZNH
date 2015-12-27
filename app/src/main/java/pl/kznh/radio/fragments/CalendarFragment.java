package pl.kznh.radio.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kznh.radio.R;
import pl.kznh.radio.gson.calendar.Event;
import pl.kznh.radio.gson.calendar.EventsContainer;
import pl.kznh.radio.utils.CalendarDialogAdapter;
import pl.kznh.radio.utils.Constants;
import pl.kznh.radio.utils.CurrentDayDecorator;
import pl.kznh.radio.utils.EventDecorator;
import pl.kznh.radio.utils.TypefaceSpan;
import pl.kznh.radio.utils.VolleySingleton;

/**
 * Created by SzymonN on 2015-11-30.
 */
public class CalendarFragment extends Fragment {

    private ArrayList<Event> mEvents;

    private ImageView mProgressView;

    private TextView mErrorTextView;

    private RelativeLayout mRootLayout;

    private AlertDialog mDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_calendar, container, false);
        mRootLayout = view;
        mProgressView = (ImageView) view.findViewById(R.id.progressView);
        mErrorTextView = (TextView) view.findViewById(R.id.errorTextView);

        mErrorTextView.setTypeface(Constants.robotoCondensed);
        setActionBarTitle(R.string.title_section2);

        Animation rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_animation);
        mProgressView.startAnimation(rotateAnimation);
        getData();
        //Log.i("dfghjhfd", getMinTime() + " " + getMaxTime());
        //new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
        return view;
    }

    private void getData() {

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, getURL(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        EventsContainer eventsContainer = gson.fromJson(response.toString(), EventsContainer.class);
                        mEvents = eventsContainer.getItems();
                        mProgressView.clearAnimation();
                        mProgressView.setVisibility(View.INVISIBLE);
                        createCalendar();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        mProgressView.clearAnimation();
                        mProgressView.setVisibility(View.INVISIBLE);
                        mErrorTextView.setText(R.string.error_unknown);
                        mErrorTextView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });
                    }
                });

// Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    private String getMinTime (){
        Date date = new Date();
        date.setTime(System.currentTimeMillis() - Constants.TIME_RADIUS);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String minTime = sdf.format(date);
        return minTime + "Z";
    }

    private String getMaxTime (){
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + (2 * Constants.TIME_RADIUS));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String maxTime = sdf.format(date);
        return maxTime + "Z";
    }

    private void createCalendar() {
        try {
            MaterialCalendarView calendar = new MaterialCalendarView(getContext());
            calendar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            calendar.setShowOtherDates(MaterialCalendarView.SHOW_ALL);
            calendar.setSelectionColor(ContextCompat.getColor(getContext(), R.color.primary));
            calendar.setArrowColor(ContextCompat.getColor(getContext(), R.color.primary));
            calendar.addDecorator(new CurrentDayDecorator(CalendarDay.from(new Date()), getActivity()));
            calendar.setVisibility(View.VISIBLE);
            calendar.setTitleMonths(R.array.months_array);
            calendar.addDecorator(new EventDecorator(ContextCompat.getColor(getContext(), R.color.primary_dark), getDaysToDecorate()));
            calendar.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, final CalendarDay calendarDay, boolean b) {
                    if (getDaysToDecorate().contains(calendarDay)) {
                        createDialog(calendarDay);
                    }
                }
            });

            mRootLayout.addView(calendar);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void createDialog(final CalendarDay calendarDay) {
        new Thread() {
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                ArrayList<String> eventDescriptions= getEventDescriptionArray(calendarDay);
                final ArrayList<Date> startTimes= getStartTimeArray(calendarDay);
                ArrayList<Date> endTimes= getEndTimeArray(calendarDay);
                View view = getActivity().getLayoutInflater().inflate(R.layout.calendar_dialog, null);
                TextView titleView = (TextView) view.findViewById(R.id.titleView);
                ListView listView = (ListView) view.findViewById(R.id.listView);
                titleView.setText(calendarDay.getDay() + " " + getResources().getStringArray(R.array.months_array)[calendarDay.getMonth()] + " " + calendarDay.getYear());
                listView.setAdapter(new CalendarDialogAdapter(getActivity(),
                        android.R.layout.simple_list_item_1,
                        eventDescriptions,
                        startTimes,
                        endTimes));
                builder.setView(view);
                builder.setNegativeButton(R.string.back, null);

                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mDialog = builder.create();
                        mDialog.show();
                    }
                });
            }
        }.start();
    }

    private ArrayList<Date> getEndTimeArray(CalendarDay calendarDay) {
        ArrayList<Date>endTimes = new ArrayList<>();
        for (Event event : mEvents){
            if (shouldAddToArray(calendarDay.getDate(), event.getStart().getDateTime())){
                endTimes.add(event.getEnd().getDateTime());
            }
        }
        return endTimes;
    }

    private ArrayList<Date> getStartTimeArray(CalendarDay calendarDay) {
        ArrayList<Date> startTimes = new ArrayList<>();
        for (Event event : mEvents){
            if (shouldAddToArray(calendarDay.getDate(), event.getStart().getDateTime())){
                startTimes.add(event.getStart().getDateTime());
            }
        }
        return startTimes;
    }

    private ArrayList<String> getEventDescriptionArray(CalendarDay calendarDay) {
        ArrayList<String> descriptions = new ArrayList<>();
        for (Event event : mEvents){
            if (shouldAddToArray(calendarDay.getDate(), event.getStart().getDateTime())){
                descriptions.add(event.getSummary());
            }
        }
        return descriptions;
    }

    private boolean shouldAddToArray(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date1).equals(sdf.format(date2));
    }

    private List<CalendarDay> getDaysToDecorate () {
        List<CalendarDay> dates = new ArrayList<>();
        for (Event event : mEvents){
            dates.add(CalendarDay.from(event.getStart().getDateTime()));
        }
        return dates;
    }

    private String getURL(){
        String maxTime = "";
        String minTime = "";
        try {
            maxTime = URLEncoder.encode(getMaxTime(), "UTF-8");
            minTime= URLEncoder.encode(getMinTime(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Log.i("CREATING URL", url);
        return "https://www.googleapis.com/calendar/v3/calendars/f4v2gn75ptsmdg2f2kef6rf6ds%40group.calendar.google.com/events" +
                "?maxResults=500" +
                "&timeMax=" + maxTime +
                "&timeMin=" + minTime +
                "&timeZone=UTC-2" +
                "&orderBy=startTime" +
                "&singleEvents=true" +
                "&fields=items(end%2Cstart%2Csummary)" +
                "&key=" + Constants.GOOGLE_API_KEY;
    }

    public void setActionBarTitle (int titleRes) {
        SpannableString s = new SpannableString(getString(titleRes));
        s.setSpan(new TypefaceSpan(getActivity(), Constants.FONT_NAME), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(s);
    }
}
