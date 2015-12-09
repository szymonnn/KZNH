package pl.kznh.radio.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import pl.kznh.radio.R;

/**
 * Created by SzymonN on 2015-12-01.
 */public class CurrentDayDecorator implements DayViewDecorator {

    private final CalendarDay date;
    private final Context context;

    public CurrentDayDecorator(CalendarDay date, Context context) {
        this.date = date;
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.current_day_background));
    }
}