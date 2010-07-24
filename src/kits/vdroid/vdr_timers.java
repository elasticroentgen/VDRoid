//TODO: Crash beim einlesen der Timers VDR-Portal http://www.vdr-portal.de/board/thread.php?postid=922576#post922576
//TODO: Crash bei deaktivierten Timern: http://www.vdr-portal.de/board/thread.php?postid=922531#post922531


package kits.vdroid;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


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
              
        time_list.setAdapter(timeadp);
        
        //Klick auf Timer zeigt Infos
        time_list.setOnItemClickListener(new OnItemClickListener()
		{
		    public void onItemClick(AdapterView<?> parent, android.view.View view,int position, long id)
		    {
		    	//stopFetchThread();
		    	//Timerinfo activity
		    	Intent startStreaming = new Intent(Intent.ACTION_VIEW);
		        Uri timerUri = Uri.parse("vdr://" + host + "/timer?timerid=" + timeadp.getTimerID(position));
		        startStreaming.setData(timerUri);
		        startStreaming.setClass(vdr_timers.this,kits.vdroid.TimerInfo.class);
		        startActivity(startStreaming);

		    }
		});
        
        
	}
	
	@Override
	public void onResume() {
        super.onResume();
        Log.d("VDRTIMERS", "Resumed");
        ldThread = new LoadThread(loadHandler);
        timeadp.clear();
        ldThread.start();
	}
	
	private class TimerInfo
	{
		String title;
		String status;
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

			public void clear()
			{
				timerdata.clear();
				this.notifyDataSetInvalidated();
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
			
			public View getView(final int position, View convertView, ViewGroup parent) {
						
				if (convertView == null)
					convertView = mInflater.inflate(R.layout.timers_li, parent, false);
				
				String chanline = timerdata.get(position).date + " - " + timerdata.get(position).channel;
				
				((TextView) convertView.findViewById(R.id.timer_li_name)).setText(timerdata.get(position).title);
				((TextView) convertView.findViewById(R.id.timer_li_timeline)).setText(timerdata.get(position).timeline);
				((TextView) convertView.findViewById(R.id.timer_li_channel)).setText(chanline);
				
				((TextView) convertView.findViewById(R.id.timer_li_status)).setText(timerdata.get(position).status);
								
				if(timerdata.get(position).status == (String) getResources().getText(R.string.timers_inactive))
					((TextView) convertView.findViewById(R.id.timer_li_status)).setTextColor(Color.YELLOW);
				else if(timerdata.get(position).status == (String) getResources().getText(R.string.timers_recording))
					((TextView) convertView.findViewById(R.id.timer_li_status)).setTextColor(Color.RED);
				else
					((TextView) convertView.findViewById(R.id.timer_li_status)).setTextColor(Color.GREEN);
				
				return convertView;
				
			}
	    }
	
	 
	 final Handler loadHandler = new Handler() {
	        public void handleMessage(Message msg) {
	        	TimerInfo ti = new TimerInfo();
	        	
	        	if(msg.getData().getBoolean("error"))
	        	{
	        		Toast.makeText(vdr_timers.this,getResources().getText(R.string.toast_blocked), Toast.LENGTH_LONG).show();
	        		finish();
	        	}
	        	
	        	if(msg.getData().getBoolean("hastimer"))
	        	{
		        	ti.title =	msg.getData().getString("title");
		        	ti.channel = msg.getData().getString("channel");
		        	ti.timeline = msg.getData().getString("timeline");
		        	ti.date = msg.getData().getString("date");
		        	ti.status = msg.getData().getString("status");
		        	ti.timerid = msg.getData().getInt("timerid");
		        	timeadp.addRecording(ti);
	        	}
	        	else
	        	{
	        		Toast.makeText(vdr_timers.this,getResources().getText(R.string.timers_notimer), Toast.LENGTH_LONG).show();
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
	            SVDRP vdr = new SVDRP(host,vdr_timers.this);
	            
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
	    			String status = null;
	    			
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
	    				int state = Integer.parseInt(timerline.split(":")[0]);
	    				if(state == 0 || state == 2 || state == 4 || state == 8)
	    					status = (String) getResources().getText(R.string.timers_inactive);
	    				else
	    					status = (String) getResources().getText(R.string.timers_active);
	    				
	    				if(state == 9)
	    					status = (String) getResources().getText(R.string.timers_recording);
	    			
	    				
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
		   		     	b.putString("status", status);
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
