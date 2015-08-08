package com.parse.starter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class ViewGraphActivity extends Activity {

    int month;
    int day;
    int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_graph);

        // listener for date view
        AdapterView.OnItemSelectedListener cbxListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getId() == (R.id.date)) {
                    String dateComponents[] = parent.getItemAtPosition(position).toString().split("-");
                    month = Integer.parseInt(dateComponents[0]);
                    day = Integer.parseInt(dateComponents[1]);
                    year = Integer.parseInt(dateComponents[2]);
                }

                generateChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        // date view
        Spinner spinnerType = (Spinner) findViewById(R.id.date);

        // build a list of dates for this week
        String[] dateList = DateList.getDateList(31);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dateList
        );

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(arrayAdapter);
        spinnerType.setOnItemSelectedListener(cbxListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  This method calculates the data for the expense chart
     */
    private void generateChart() {

        final TypeList myTypeList = new TypeList();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Expense");
        query.whereEqualTo("Month", month);
        query.whereEqualTo("Day", day);
        query.whereEqualTo("Year", year);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    // object will be your expense

                    for (ParseObject a : results) {
                        String tmpType = a.getString("Type");

                        Integer loc = Arrays.asList(TypeList.getTypeList()).indexOf(tmpType);

                        if (loc > -1 && loc < TypeList.getTypeCnt()) {
                            myTypeList.addAmt(loc, (float) a.getDouble("Amount"));
                        }
                    }

                    buildChart(TypeList.getTypeList(), myTypeList.getTypeAmts(), TypeList.getTypeCnt());
                } else {
                    // something went wrong
                }
            }
        });
    }

    /**
     * This method displays a series of views in a LinearLayout to form a bar graph showing the amount of money spent for each type
     *
     * @param typeNames - an array of names of each type
     * @param typeAmounts - an array of numbers for how much money we spent for each type)
     * @param typeCnt - the number of types to display
     */
    private void buildChart(String[] typeNames, float[] typeAmounts, int typeCnt) {

        // create TextViews to form a bar graph and put it into a LinearLayout
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.barGraph);

        // erase previous bar graph if it exists
        if ( linearLayout.getChildCount() > 0) {
            linearLayout.removeAllViews();
        }

        // calculate max height of all views
        float maxHeight = 0;
        for (int i = 0; i < typeCnt; ++i) {
            if (typeAmounts[i] > maxHeight) {
                maxHeight = typeAmounts[i];
            }
        }

        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels;
        float screenFactor = dpHeight / maxHeight;

        // build views for each bar of the bar graph
        for (int i = 0; i < typeCnt; ++i) {

            float height = typeAmounts[i] * screenFactor * 0.6f; // to account for action bar
            int newHeight = Math.round(height);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, // width
                    newHeight  //height
            );

            float f = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, displayMetrics);
            int px = Math.round(f);
            layoutParams.setMargins(px, 0, px, 0);   //left, top, right, bottom
            TextView textView = new TextView(ViewGraphActivity.this);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(px, px, px, px);   //left, top, right, bottom

            // set background color for each View
            textView.setBackgroundColor(getBackgroundColor(i));
            // set font color for each View
            textView.setTextColor(getFontColor(i));

            DecimalFormat df = new DecimalFormat("#,###.00");

            String barCaption = typeNames[i] + "\n" + df.format(typeAmounts[i]);

            textView.setText(barCaption);
            linearLayout.addView(textView);
        }
    }

    /**
     * Get the textcolor to use for each bar in the bar graph
     *
     * @param typeCnt - the number of the current type
     * @return - a hex font color code
     */
    private int getFontColor(int typeCnt) {
        Resources resources = getResources();
        int color;
        int counter = typeCnt % 10;
        if (counter < 4) {
            color = resources.getColor(R.color.black);
        } else {
            color = resources.getColor(R.color.white);
        }
        return color;
    }

    /**
     * Get the background color to use for each bar in the bar graph
     *
     * @param typeCnt - the number of the current type
     * @return - a hex background color code
     */
    private int getBackgroundColor(int typeCnt) {
        Resources resources = getResources();
        int colorList[] = {
                resources.getColor(R.color.red),
                resources.getColor(R.color.orange),
                resources.getColor(R.color.yellow),
                resources.getColor(R.color.green),
                resources.getColor(R.color.blue),
                resources.getColor(R.color.purple),
                resources.getColor(R.color.indigo),
                resources.getColor(R.color.lightgrey),
                resources.getColor(R.color.darkgrey),
                resources.getColor(R.color.black),
        };
        int counter = typeCnt % 10;
        return colorList[counter];
    }

    // erase all expenses for this date
    public void eraseDaysExpenses(View v) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Expense");
        query.whereEqualTo("Month", month);
        query.whereEqualTo("Day", day);
        query.whereEqualTo("Year", year);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    // object will be your expense
                    for (ParseObject a : results) {
                        a.deleteInBackground();
                    }

                    generateChart();
                } else {
                    // something went wrong
                }
            }
        });
    }

    // view add new expense screen
    public void viewAdd(View v) {
        Intent intent = new Intent(ViewGraphActivity.this, AddEntryActivity.class);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException activityNotFoundException) {
            Toast toast = Toast.makeText(ViewGraphActivity.this, activityNotFoundException.toString(), Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
