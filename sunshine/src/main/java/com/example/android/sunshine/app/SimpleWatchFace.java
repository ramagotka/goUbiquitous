package com.example.android.sunshine.app;

/**
 * Created by hania on 14.07.16.
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SimpleWatchFace {

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static SimpleDateFormat DAY_OF_WEEK_FORMAT;
    private static java.text.DateFormat DATE_FORMAT;
    private static Calendar mCalendar;
//    private static final String DATE_FORMAT = "%02d:%02d:%d";
//    private static final int DATE_AND_TIME_DEFAULT_COLOUR = Color.WHITE;
//    private static final int BACKGROUND_DEFAULT_COLOUR = Color.BLACK;

    private final Paint timePaint;
    private final Paint datePaint;
    private final Paint backgroundPaint;
    private final Time time;
    private final Paint linePaint;
    private final Paint maxPaint;
    private final Paint minPaint;

    private static Context mContext;

    private float mXOffset;
    private float mYOffset;
    private float mDateXOffset;
    private float mDateYOffset;
    private static int DATE_AND_TIME_AMBIENT_MODE_COLOUR;
    private static int BACKGROUND_AMBIENT_MODE_COLOUR;
    private Bitmap mBitmap;

    //private boolean shouldDrawColons;

    public String mMax;
    public String mMin;
    public String mDes;

    private boolean shouldShowSeconds = true;
    private static int backgroundColour;
    private static int dateAndTimeColour;

    public static SimpleWatchFace newInstance(Context context) {
        mContext = context;

        Resources resources = mContext.getResources();

        Paint timePaint = new Paint();
        timePaint.setColor(resources.getColor(R.color.white));
        timePaint.setTextSize(context.getResources().getDimension(R.dimen.digital_text_size));
        timePaint.setAntiAlias(true);
        timePaint.setTextAlign(Paint.Align.CENTER);

        Paint datePaint = new Paint();
        datePaint.setColor(resources.getColor(R.color.white));
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.digital_date_text_size));
        datePaint.setAntiAlias(true);
        datePaint.setTextAlign(Paint.Align.CENTER);

        Paint linePaint = new Paint();
        linePaint.setColor(resources.getColor(R.color.white));

        Paint maxPaint = new Paint();
        maxPaint.setTextSize(context.getResources().getDimension(R.dimen.digital_max_text_size));
        maxPaint.setColor(resources.getColor(R.color.white));
        maxPaint.setAntiAlias(true);
        maxPaint.setTypeface(Typeface.create("Arial",Typeface.BOLD));
        maxPaint.setTextAlign(Paint.Align.CENTER);

        Paint minPaint = new Paint();
        minPaint.setTextSize(context.getResources().getDimension(R.dimen.digital_min_text_size));
        minPaint.setAntiAlias(true);
        minPaint.setColor(resources.getColor(R.color.white));
        minPaint.setTextAlign(Paint.Align.CENTER);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(resources.getColor(R.color.background));

        backgroundColour = resources.getColor(R.color.background);
        dateAndTimeColour = resources.getColor(R.color.white);

        DATE_AND_TIME_AMBIENT_MODE_COLOUR = resources.getColor(R.color.white);
        BACKGROUND_AMBIENT_MODE_COLOUR = resources.getColor(R.color.black);

        mCalendar = Calendar.getInstance();

        DAY_OF_WEEK_FORMAT = new SimpleDateFormat("EEE", Locale.getDefault());
        DAY_OF_WEEK_FORMAT.setCalendar(mCalendar);
        DATE_FORMAT = DateFormat.getDateFormat(context);
        DATE_FORMAT.setCalendar(mCalendar);

        return new SimpleWatchFace(timePaint, datePaint, backgroundPaint, linePaint, maxPaint, minPaint, new Time());
    }

    SimpleWatchFace(Paint timePaint, Paint datePaint, Paint backgroundPaint, Paint linePaint, Paint maxPaint, Paint minPaint, Time time) {

        Resources resources = mContext.getResources();

        this.timePaint = timePaint;
        this.datePaint = datePaint;
        this.backgroundPaint = backgroundPaint;
        this.linePaint = linePaint;
        this.time = time;
        this.mMax = "0";
        this.maxPaint = maxPaint;
        this.minPaint = minPaint;
        mYOffset = resources.getDimension(R.dimen.digital_y_offset);
        mXOffset = resources.getDimension(R.dimen.digital_x_offset);
        mDateYOffset = resources.getDimension(R.dimen.digital_date_y_offset);
        mDateXOffset = resources.getDimension(R.dimen.digital_date_x_offset);
        mDes = "300";
    }

    public void draw(Canvas canvas, Rect bounds) {
        time.setToNow();
        Date date = new Date();

        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

        String timeText = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, time.hour, time.minute, time.second);
        canvas.drawText(timeText, canvas.getWidth()/2, mYOffset, timePaint);

        String dateText = DAY_OF_WEEK_FORMAT.format(date) + ", " + DATE_FORMAT.format(date);
        canvas.drawText(dateText, canvas.getWidth()/2, mYOffset + mDateYOffset, datePaint);

        //line
        float lineLength = 25;
        canvas.drawLine(canvas.getWidth()/2 - lineLength, mYOffset + mDateYOffset + lineLength,
                canvas.getWidth()/2 + lineLength, mYOffset + mDateYOffset + lineLength, linePaint);

        //weather
        float l = -(maxPaint.measureText(mMax + "\u00b0") /*+ minPaint.measureText(" " + mMin + "\u00b0")*/)/2;
        if (shouldShowSeconds){
            Log.d("onDraw ", mDes);
            mBitmap =  BitmapFactory.decodeResource(mContext.getResources(), getIconResourceForWeatherCondition(Integer.valueOf(mDes)));
            float w =mBitmap.getWidth();
            canvas.drawBitmap(mBitmap, canvas.getWidth()/2 - w*2,  mYOffset + mDateYOffset + lineLength + 15, maxPaint);
            l = 0;
        }

        canvas.drawText(mMax + "\u00b0", canvas.getWidth()/2 + l , mYOffset + mDateYOffset + mDateYOffset + lineLength +lineLength,
                maxPaint);
        l += maxPaint.measureText(mMax + "\u00b0");
        canvas.drawText(" " + mMin + "\u00b0",canvas.getWidth()/2 +  l, mYOffset + mDateYOffset + mDateYOffset + lineLength + lineLength,
                minPaint);
    }

    public void setAntiAlias(boolean antiAlias) {
        timePaint.setAntiAlias(antiAlias);
        datePaint.setAntiAlias(antiAlias);
    }

    public void updateTimeZoneWith(String timeZone) {
        time.clear(timeZone);
        time.setToNow();
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
    }

    public void restoreBackgroundColour() {
        backgroundPaint.setColor(backgroundColour);
    }

    public void updateBackgroundColourToAmbientMode() {
        backgroundPaint.setColor(BACKGROUND_AMBIENT_MODE_COLOUR);
    }

    public void updateDateAndTimeColourToAmbientMode() {
        timePaint.setColor(DATE_AND_TIME_AMBIENT_MODE_COLOUR);
        datePaint.setColor(DATE_AND_TIME_AMBIENT_MODE_COLOUR);
    }

    public void restoreDateAndTimeColour() {
        timePaint.setColor(dateAndTimeColour);
        datePaint.setColor(dateAndTimeColour);
    }

    private int getIconResourceForWeatherCondition(int weatherId) {
        Log.d("get image ID", " " + weatherId);
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }
}

