package com.parse.starter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;

public class AddEntryActivity extends Activity {

	/**
	 * Called when the activity is first created.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_entry);

		// date view
		Spinner spinnerType = (Spinner) findViewById(R.id.date);

		// build a list of dates for this week
		String[] dateList = DateList.getDateList(7);

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
				this,
				android.R.layout.simple_spinner_item,
				dateList
		);

		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerType.setAdapter(arrayAdapter);

		// expense type view
		Spinner spinnerType2 = (Spinner) findViewById(R.id.type);

		ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(
				this,
				android.R.layout.simple_spinner_item,
				TypeList.getTypeList()
		);

		arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerType2.setAdapter(arrayAdapter2);

		ParseAnalytics.trackAppOpenedInBackground(getIntent());
	}


	public void saveData(View v) {

		ParseObject expense;

		Integer month;
		Integer day;
		Integer year;
		String type;
		Double amount;
		String description;

		boolean abortFlag = false;

		// read date
		Spinner dateSpinner = (Spinner) findViewById(R.id.date);
		String dateComponents[] = dateSpinner.getSelectedItem().toString().split("-");
		month = Integer.parseInt(dateComponents[0]);
		day = Integer.parseInt(dateComponents[1]);
		year = Integer.parseInt(dateComponents[2]);

		// read type
		Spinner typeSpinner = (Spinner) findViewById(R.id.type);
		type = typeSpinner.getSelectedItem().toString();

		// read amount
		final EditText amountText = (EditText) findViewById(R.id.amount);
		String tmpString = amountText.getText().toString();
		try {
			amount = Double.parseDouble(tmpString);
		} catch (NumberFormatException numberFormatException) {
			Toast toast = Toast.makeText(AddEntryActivity.this, "bad double " + tmpString, Toast.LENGTH_LONG);
			toast.show();
			abortFlag = true;
			amount = null;
		}

		// read description
		final EditText descText = (EditText) findViewById(R.id.desc);
		description = descText.getText().toString();

		// add new entry to Parse database
		if (!abortFlag) {
			expense = new ParseObject("Expense");
			expense.put("Month", month);
			expense.put("Day", day);
			expense.put("Year", year);
			expense.put("Type", type);
			expense.put("Amount", amount);
			expense.put("Desc", description);
			expense.saveInBackground();

			Toast.makeText(getBaseContext(), getResources().getString(R.string.save_msg), Toast.LENGTH_LONG).show();
		}
	}

	// view expense graph screen
	public void viewGraph(View v) {
		Intent intent = new Intent(AddEntryActivity.this, ViewGraphActivity.class);

		try {
			startActivity(intent);
		} catch (ActivityNotFoundException activityNotFoundException) {
			Toast toast = Toast.makeText(AddEntryActivity.this, activityNotFoundException.toString(), Toast.LENGTH_LONG);
			toast.show();
		}
	}
}

