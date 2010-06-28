package kits.vdroid;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class TimerInfo extends Activity {
	
	String host;
	String timerid;
	SVDRP vdr;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        Uri data = getIntent().getData();
        host = data.getHost();
        timerid = data.getQueryParameter("timerid");
        Log.d("VDRINFO", "Getting Info's for Timer "+  timerid);
        vdr = new SVDRP(host,2001);
        
        String line = vdr.getData("LSTT " + timerid);
        
        Boolean active;
        
        if(line.startsWith("250 "))
		{
			String timerline;
			timerline = line.split(" ", 3)[2];
		
			//Activetimer
			String active_str = timerline.split(":")[0];
			if(active_str.equals("1"))
				active = true;
			else
				active = false;
			
			//Chanalnummer
			String cnr = timerline.split(":")[1];
			
			//Datum
			String date_raw = timerline.split(":")[2];
			
			//Zeiten
			String start_time_raw = timerline.split(":")[3];
			String start_time = start_time_raw.substring(0, 2) + ":" + start_time_raw.substring(2); 
			
			String end_time_raw = timerline.split(":")[4];
			String end_time = end_time_raw.substring(0, 2) + ":" + end_time_raw.substring(2);

			//Titel
			String ctitle = timerline.split(":")[7];

			//Channel
			String cchannel = vdr.getData("LSTC " + cnr).split(" ")[2].split(";")[0];
        
			//Timer aktiv
			CheckBox cb = ((CheckBox) findViewById(R.id.timerinfo_active));
			cb.setChecked(active);
			
			//Timer Titel
			EditText timername = ((EditText) findViewById(R.id.timerinfo_name));
			timername.setText(ctitle);
			
			//Datum
			DatePicker dp = ((DatePicker) findViewById(R.id.timerinfo_date));
			int year = Integer.parseInt(date_raw.split("-")[0]);
			int month = Integer.parseInt(date_raw.split("-")[1]);
			int day = Integer.parseInt(date_raw.split("-")[2]);
			dp.updateDate(year,month,day);
			
			//Startzeit
			int hour_start = Integer.parseInt(start_time_raw.substring(0, 2));
			int minute_start = Integer.parseInt(start_time_raw.substring(2));
			
			TimePicker tp_start = ((TimePicker) findViewById(R.id.timerinfo_time_start));
			tp_start.setIs24HourView(true);
			tp_start.setCurrentHour(hour_start);
			tp_start.setCurrentMinute(minute_start);
			
			//Endzeit
			int hour_end = Integer.parseInt(start_time_raw.substring(0, 2));
			int minute_end = Integer.parseInt(start_time_raw.substring(2));
			
			TimePicker tp_end = ((TimePicker) findViewById(R.id.timerinfo_time_end));
			tp_end.setIs24HourView(true);
			tp_end.setCurrentHour(hour_end);
			tp_end.setCurrentMinute(minute_end);
			vdr.close();
		}
        else
        {
        	vdr.close();
        	finish();
        }
        
	}
}
