package kits.vdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimerInfo extends Activity {
	
	String host;
	String timerid;
	SVDRP vdr;
	
	Button date_btn;
	Button time_end_btn;
	Button time_start_btn;
	CheckBox cb;
	EditText timername;
	
	String year;
	String month;
	String day;
	
	String start_h;
	String start_m;
	
	String end_h;
	String end_m;
	
	int state;
	String cnr;
	String prio;
	String halt;
	
	Boolean active;
	
	static final int DATE_DIALOG_ID = 0;
	static final int TIMES_DIALOG_ID = 1;
	static final int TIMEE_DIALOG_ID = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timerinfo);
        Uri data = getIntent().getData();
        host = data.getHost();
        timerid = data.getQueryParameter("timerid");
        Log.d("VDRINFO", "Getting Info's for Timer "+  timerid);
        vdr = new SVDRP(host,2001);
        
        String line = vdr.getData("LSTT " + timerid);
        
        active = false;
        
        if(line.startsWith("250 "))
		{
			String timerline;
			timerline = line.split(" ", 3)[2];
		
			//Activetimer
			String active_str = timerline.split(":")[0];
			state = Integer.parseInt(active_str);
			if(state == 1 || state == 3 || state == 5 || state == 9)
				active = true;
			else
				active = false;
			
			//Chanalnummer
			cnr = timerline.split(":")[1];
			
			//Datum
			String date_raw = timerline.split(":")[2];
			
			//Zeiten
			String start_time_raw = timerline.split(":")[3];
			String end_time_raw = timerline.split(":")[4];

			//Titel
			String ctitle = timerline.split(":")[7];

			prio = timerline.split(":")[5];
			halt = timerline.split(":")[6];
			
			//Channel
			String cchannel = vdr.getData("LSTC " + cnr).split(" ")[2].split(";")[0];
        
			TextView texthead = ((TextView) findViewById(R.id.timerinfo_head));
			texthead.setText("Timer bearbeiten - " + cchannel);
			
			//Timer aktiv
			cb = ((CheckBox) findViewById(R.id.timerinfo_active));
			cb.setChecked(active);
			
			//Timer Titel
			timername = ((EditText) findViewById(R.id.timerinfo_name));
			timername.setText(ctitle);
			
			//Datum
			year = date_raw.split("-")[0];
			month = date_raw.split("-")[1];
			day = date_raw.split("-")[2];
			date_btn = ((Button) findViewById(R.id.timerinfo_date));
						
			//Startzeit
			start_h = start_time_raw.substring(0, 2);
			start_m = start_time_raw.substring(2);
			time_start_btn = ((Button) findViewById(R.id.timerinfo_time_start));
			
						
			//Endzeit
			end_h = end_time_raw.substring(0, 2);
			end_m = end_time_raw.substring(2);
			time_end_btn = ((Button) findViewById(R.id.timerinfo_time_end));
						
			//Clickzeugs
			date_btn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                showDialog(DATE_DIALOG_ID);
	            }
	        });
			
			time_start_btn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                showDialog(TIMES_DIALOG_ID);
	            }
	        });
			
			time_end_btn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                showDialog(TIMEE_DIALOG_ID);
	            }
	        });
			
			Button ok_btn = ((Button) findViewById(R.id.timerinfo_ok));
			Button cancel_btn = ((Button) findViewById(R.id.timerinfo_cancel));
			Button delete_btn = ((Button) findViewById(R.id.timerinfo_delete));
			
			ok_btn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                updateTimer();
	            }
	        });
			
			cancel_btn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                finish();
	            }
	        });
			delete_btn.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	
	                deleteThisTimer();
	            }
	        });
			
			
			updateDisplay();
			vdr.close();
		}
        else
        {
        	vdr.close();
        	finish();
        }
        
	}
	
	private void deleteThisTimer()
	{
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Timer wirklich löschen?")
		       .setCancelable(false)
		       .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		       		Log.d("TIMER", "DELETING");
		    		vdr.getData("DELT " + String.valueOf(timerid));
		    		vdr.close();
		    		finish();
		           }
		       })
		       .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void updateTimer()
	{
		Log.d("TIMER", "UPDATING");
		String timerline = "";
		
		//timerstate
		if(cb.isChecked())
		{
			if(state == 0 || state == 2 || state == 4 || state == 8)
				state = state + 1;
			timerline = timerline + String.valueOf(state) + ":"; 
		}
		else
		{
			if(state == 1 || state == 3 || state == 5 || state == 9)
				state = state - 1;
			timerline = timerline + String.valueOf(state) + ":"; 
		}	
		
		//Channr
		timerline = timerline + cnr + ":";
		
		//Date
		timerline = timerline + year + "-" + month + "-" + day + ":";
		
		//Starttime
		timerline = timerline + start_h + start_m + ":";
		
		//Starttime
		timerline = timerline + end_h + end_m + ":";
		
		//prio + dauerhaft
		timerline = timerline + prio + ":" + halt + ":";
		
		//titel
		String title = timername.getText().toString();
		timerline = timerline + title;
		
		Log.d("TIMER", timerline);
		
		vdr.getData("MODT " + String.valueOf(timerid) + " " + timerline);
		vdr.close();
		Toast.makeText(TimerInfo.this,"Timer geändert", Toast.LENGTH_LONG).show();
		finish();
		
			
	}
	
	private void updateDisplay() {
	        date_btn.setText(day+"."+month+"."+year);
	        
	        if(Integer.parseInt(start_h+start_m) > Integer.parseInt(end_h+end_m))
	        	Toast.makeText(TimerInfo.this,"Startzeit grösser Endezeit", Toast.LENGTH_LONG).show();
	        else
	        {
		        time_start_btn.setText(start_h+":"+start_m);
		        time_end_btn.setText(end_h+":"+end_m);
	        }
	           
	}
	
	//Datum chooser Callback
	private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int iyear, 
                                  int imonthOfYear, int idayOfMonth) {
                year = String.valueOf(iyear);
                if(year.length() < 2)
                	year = "0" + year;
                
                month = String.valueOf(imonthOfYear+1);
                if(month.length() < 2)
                	month = "0" + month;
                
                day = String.valueOf(idayOfMonth);
                if(day.length() < 2)
                	day = "0" + day;
                updateDisplay();
            }
        };
	
    private TimePickerDialog.OnTimeSetListener mTimeSetListenerStart =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                start_h = String.valueOf(hourOfDay);
                if(start_h.length() < 2)
                	start_h = "0" + start_h;
                
                start_m = String.valueOf(minute);
                if(start_m.length() < 2)
                	start_m = "0" + start_m;
                updateDisplay();
            }
        };
     
    private TimePickerDialog.OnTimeSetListener mTimeSetListenerEnd =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                end_h = String.valueOf(hourOfDay);
                if(end_h.length() < 2)
                	end_h = "0" + end_h;
                
                end_m = String.valueOf(minute);
                if(end_m.length() < 2)
                	end_m = "0" + end_m;
                
                updateDisplay();
            }
        };
        
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,mDateSetListener,Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day));
        case TIMES_DIALOG_ID:
        	return new TimePickerDialog(this,mTimeSetListenerStart, Integer.parseInt(start_h),Integer.parseInt(start_m), true);
        case TIMEE_DIALOG_ID:
        	return new TimePickerDialog(this,mTimeSetListenerEnd, Integer.parseInt(end_h),Integer.parseInt(end_m), true);
        }
        return null;
    }
	
}
