package kits.vdroid;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class vdr_timers extends Activity {

	private TimerAdapter timeadp;
	private ListView time_list;
	private LoadThread ldThread;
	private String host;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.timers);
        timeadp = new TimerAdapter();
        
        time_list = (ListView) findViewById(R.id.vdr_timers_list);
        host = getIntent().getData().getHost();
        
        ldThread = new LoadThread(loadHandler);
        ldThread.start();
        
        time_list.setAdapter(timeadp);
        
        
	}
	
	private class TimerInfo
	{
		String title;
		Boolean active;
		String timeline;
		String date;
		String channel;
		int timerid;
		
	}
	
	
	//Adapter
	 private class TimerAdapter extends BaseAdapter
	    {
	    	private LayoutInflater mInflater;
	    	private List<TimerInfo> timerdata;
	    	 
	        public TimerAdapter() {
	            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            timerdata = new ArrayList<TimerInfo>();
	        }
	    	
			public int getCount() {
				return timerdata.size();
			}

			public Object getItem(int position) {
				timerdata.get(position);
				return null;
			}

			public long getItemId(int position) {
				return position;
			}

			public void addRecording(TimerInfo input)
			{
				timerdata.add(input);
				this.notifyDataSetChanged();
			}
			
			public int getTimerID(int position)
			{
				return timerdata.get(position).timerid;
			}
			
			public View getView(int position, View convertView, ViewGroup parent) {
						
				if (convertView == null)
					convertView = mInflater.inflate(R.layout.timers_li, parent, false);
				
				String chanline = timerdata.get(position).date + " - " + timerdata.get(position).channel;
				
				((TextView) convertView.findViewById(R.id.timer_li_name)).setText(timerdata.get(position).title);
				((TextView) convertView.findViewById(R.id.timer_li_timeline)).setText(timerdata.get(position).timeline);
				((TextView) convertView.findViewById(R.id.timer_li_channel)).setText(chanline);
				CheckBox active_cbox = ((CheckBox) convertView.findViewById(R.id.timer_li_check));
				active_cbox.setChecked(timerdata.get(position).active);
				active_cbox.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
					 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				    {
					 	View parent = (View) buttonView.getParent();
		        		int pos = time_list.getPositionForView(parent);
		        		String timerid = String.valueOf(timeadp.getTimerID(pos));
		        		SVDRP vdr = new SVDRP(host,2001);
		        		
				        if ( isChecked )
				        	vdr.getData("MODT " + timerid + " on");
				        else
				          	vdr.getData("MODT " + timerid + " off");

				        vdr.close();
				    }
	
				});
				return convertView;
				
			}
	    }
	
	 
	 final Handler loadHandler = new Handler() {
	        public void handleMessage(Message msg) {
	        	TimerInfo ti = new TimerInfo();
	        	
	        	if(msg.getData().getBoolean("error"))
	        	{
	        		Toast.makeText(vdr_timers.this,"VDR-Interface blockiert!", Toast.LENGTH_LONG).show();
	        		finish();
	        	}
	        	
	        	if(msg.getData().getBoolean("hastimer"))
	        	{
		        	ti.title =	msg.getData().getString("title");
		        	ti.channel = msg.getData().getString("channel");
		        	ti.timeline = msg.getData().getString("timeline");
		        	ti.date = msg.getData().getString("date");
		        	ti.active = msg.getData().getBoolean("active");
		        	ti.timerid = msg.getData().getInt("timerid");
		        	timeadp.addRecording(ti);
	        	}
	        	else
	        	{
	        		Toast.makeText(vdr_timers.this,"Keine Timer vorhanden!", Toast.LENGTH_LONG).show();
	        		finish();
	        	}
	        	
	        }
	    };
	    
	    private class LoadThread extends Thread
		{
			Handler mHandler;
			LoadThread(Handler h) {
	            mHandler = h;
	        }
	                
	        public void run() {
	    		Log.d("TIMERTHREAD", "Requesting Recordings");
	    		
	            //Read Channels
	            SVDRP vdr = new SVDRP(host,2001);
	            
	            List<String> timers = vdr.getListData("LSTT");
	            
	            if(timers == null)
	            {
	            	Message msg = mHandler.obtainMessage();
	    		    Bundle b = new Bundle();
	            	b.putBoolean("error", true);
	   		     	
	   		     	msg.setData(b);
	   		     	mHandler.sendMessage(msg);	
	   		     	vdr.close();
	   		     	return;
	            }
	            
	            ListIterator<String> time_it = timers.listIterator();

	            while(time_it.hasNext())
	    		{
	    			String line = time_it.next();
	    			String cnr = null;
	    			String ctitle = null;
	    			String ctimeline = null;
	    			String cdate = null;
	    			String cchannel = null;
	    			Boolean active = false;
	    			int timerid;
	    			
	    			Message msg = mHandler.obtainMessage();
	    		    Bundle b = new Bundle();
	    				 
	    			if(line.startsWith("250"))
	    			{
	    				int ctimerid; 
	    				String timerline;
	    				if(line.startsWith("250 "))
	    				{
	    					timerline = line.split(" ", 3)[2];
	    					ctimerid = Integer.parseInt(line.split(" ")[1]);
	    				}
	    				else
	    				{
	    					timerline = line.split(" ", 2)[1];
	    					ctimerid = Integer.parseInt(line.split(" ")[0].split("-")[1]);
	    				}
	    				
	    				//Activetimer
	    				String active_str = timerline.split(":")[0];
	    				if(active_str.equals("1"))
	    					active = true;
	    				else
	    					active = false;
	    				
	    				//Chanalnummer
	    				cnr = timerline.split(":")[1];
	    				
	    				//Datum
	    				String date_raw = timerline.split(":")[2];
	    				cdate = date_raw.split("-")[2] + "." + date_raw.split("-")[1] + "." + date_raw.split("-")[0]; 
	    				
	    				//Zeiten
	    				String start_time_raw = timerline.split(":")[3];
	    				String start_time = start_time_raw.substring(0, 2) + ":" + start_time_raw.substring(2); 
	    				
	    				String end_time_raw = timerline.split(":")[4];
	    				String end_time = end_time_raw.substring(0, 2) + ":" + end_time_raw.substring(2);
	    				
		    			ctimeline = start_time + " - " + end_time;
	    				Log.d("TIMER", ctimeline);
		    			//Titel
		    			ctitle = timerline.split(":")[7];

		    			//Channel
		    			cchannel = vdr.getData("LSTC " + cnr).split(" ")[2].split(";")[0];
		    			
		    			b.putBoolean("hastimer", true);
		    			b.putString("title", ctitle);
		   		     	b.putString("cnum", cnr);
		   		     	b.putString("timeline", ctimeline);
		   		     	b.putString("date", cdate);
		   		     	b.putString("channel", cchannel);
		   		     	b.putBoolean("active", active);
		   		     	b.putInt("timerid", ctimerid);
	    			}
	    			else if(line.startsWith("550"))
	    			{
	    				Log.d("TIMERTHREAD", "No Recordings");
	    				b.putBoolean("hastimer", false);
	    			}
	   		     	msg.setData(b);
	   		     	mHandler.sendMessage(msg);	
	   		    
	    		}
	                        
	    		vdr.close();
	        }
		}
	 
	 
}
