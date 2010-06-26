package kits.vdroid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class vdr_show_channels extends Activity {
	
	private ListView chan_list;
	private String host;
	private ProgressDialog chanload_prog;
	private LoadThread ldThread;
	private ChannelsAdapter chanadp;
		
	private class ChannelInfo {
		String name;
		String now;
		String timeline;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chanload_prog = ProgressDialog.show( vdr_show_channels.this, "" , "Lade Kanalliste von VDR... ", false);
        
        setContentView(R.layout.channels);
        
        chanadp = new ChannelsAdapter();
        
        chan_list = (ListView) findViewById(R.id.vdr_channel_list);
        host = getIntent().getData().getHost();
        
        
        //Klick auf Kanal wechselt zum Kanal
        chan_list.setOnItemClickListener(new OnItemClickListener()
		{
		    public void onItemClick(AdapterView<?> parent, android.view.View view,int position, long id)
		    {
		    	Toast.makeText(vdr_show_channels.this,"Wechsle Kanal...", Toast.LENGTH_LONG).show();
		    	stopFetchThread();
		    	SVDRP vdr = new SVDRP(host,2001);
		    	int channum = position + 1;
		    	vdr.getData("CHAN "+ channum);
		    	vdr.close();
		    	finish();
		    }
		});
        
        chan_list.setAdapter(chanadp);
        registerForContextMenu(chan_list);
	}

	@Override
	public void onResume() {
        super.onResume();
        Log.d("VDRCHANLIST", "Resumed");
        ldThread = new LoadThread(loadHandler);
        chanadp.clear();
        ldThread.start();
	}
	
	@Override
	public void onStop() {
        super.onStop();
        Log.d("VDRCHANLIST", "Stopped");
        ldThread.stopFetching();
	}
	
	//Contextmenu
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//menu.add(0, 1, 0, "Str./aeamen");
		menu.add(0, 2, 0, "Infos");
		menu.add(0, 3, 0, "Programm");
	}
	
	private void stopFetchThread()
	{
		ldThread.stopFetching();
    	try {
			ldThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch(item.getItemId()) {
	    case 1:
	    	stopFetchThread();
	        Intent startStreaming = new Intent(Intent.ACTION_VIEW);
	        Uri streamUri = Uri.parse("vdrstream://" + host + "/" + (info.id + 1));
	        startStreaming.setData(streamUri);
	        startStreaming.setClass(this,kits.vdroid.vdr_stream_channel.class);
	        startActivity(startStreaming);
	        return true;
	    case 2:
	    	stopFetchThread();
	    	Intent showInfos = new Intent(Intent.ACTION_VIEW);
	        Uri infoUri = Uri.parse("vdr://" + host + "/info?time=now&chan=" + (info.id + 1));
	        showInfos.setData(infoUri);
	        showInfos.setClass(this,kits.vdroid.vdr_info.class);
	        startActivity(showInfos);
	        return true;
	    case 3:
	    	stopFetchThread();
	    	Intent showProg = new Intent(Intent.ACTION_VIEW);
	        Uri progUri = Uri.parse("vdr://" + host + "/prog?chan=" + (info.id + 1));
	        showProg.setData(progUri);
	        showProg.setClass(this,kits.vdroid.vdr_programm.class);
	        startActivity(showProg);
	        return true;
	    }
	    return super.onContextItemSelected(item);
	}
	
	//Hanlder nimmt geladene Kanäle vom Thread auf und läd in ListView
	final Handler loadHandler = new Handler() {
        public void handleMessage(Message msg) {
        	ChannelInfo ci = new ChannelInfo();
        	
        	if(msg.getData().getBoolean("error"))
        	{
        		chanload_prog.dismiss();
        		Toast.makeText(vdr_show_channels.this,"VDR-Interface blockiert!", Toast.LENGTH_LONG).show();
        		finish();
        	}
        	
        	ci.name =	msg.getData().getString("name");
        	ci.now = msg.getData().getString("now");
        	long starttime = msg.getData().getInt("time");
        	long dur = msg.getData().getInt("dur");
        	long endtime = starttime + dur;
        	Date start = new Date(starttime*1000);
        	Date end = new Date(endtime*1000);
        	
        	String start_h = String.valueOf(start.getHours());
        	String start_m = String.valueOf(start.getMinutes());
        	String end_h = String.valueOf(end.getHours());
        	String end_m = String.valueOf(end.getMinutes());
        	
        	if(start_h.length() == 1)
        		start_h = "0" + start_h;
        	if(start_m.length() == 1)
        		start_m = "0" + start_m;
        	if(end_h.length() == 1)
        		end_h = "0" + end_h;
        	if(end_m.length() == 1)
        		end_m = "0" + end_m;
        	        	
        	String start_str = start_h + ":" + start_m;
        	String end_str = end_h + ":" + end_m;
        	
        	ci.timeline = start_str + " - " + end_str;
        	        	        	
        	chanadp.addChannel(ci);
        	chanload_prog.dismiss();
            
        }
    };
    
    //ListView Adapter
    private class ChannelsAdapter extends BaseAdapter
    {
    	private LayoutInflater mInflater;
    	private List<ChannelInfo> chandata;
    	 
        public ChannelsAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            chandata = new ArrayList<ChannelInfo>();
        }
    	
		public int getCount() {
			return chandata.size();
		}

		public Object getItem(int position) {
			chandata.get(position);
			return null;
		}

		public void clear()
		{
			chandata.clear();
			this.notifyDataSetInvalidated();
		}
		
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
					
			if (convertView == null)
				convertView = mInflater.inflate(R.layout.channels_li, parent, false);
			
			((TextView) convertView.findViewById(R.id.channel_li_name)).setText(chandata.get(position).name);
			((TextView) convertView.findViewById(R.id.channel_li_now)).setText(chandata.get(position).now);
			((TextView) convertView.findViewById(R.id.channel_li_time)).setText(chandata.get(position).timeline);

			return convertView;
				
		}
		
		public void addChannel(ChannelInfo input)
		{
			chandata.add(input);
			this.notifyDataSetChanged();
		}
    }
    
	private class LoadThread extends Thread
	{
		Handler mHandler;
        int state;
		
        LoadThread(Handler h) {
            mHandler = h;
        }
          
        public void stopFetching()
        {
        	state = 1;
        }
        
        public void run() {
        	state = 0;
        	Log.d("CHANDTHREAD", "Requesting Channellist");
            //Read Channels
            SVDRP vdr = new SVDRP(host,2001);
            
            List<String> channels = vdr.getListData("LSTC");
                		
            if(channels == null)
            {
            	Message msg = mHandler.obtainMessage();
   		     	Bundle b = new Bundle();
   		     	b.putBoolean("error", true);
   		     	
   		     	msg.setData(b);
   		     	mHandler.sendMessage(msg);	
   		     	vdr.close();
   		     	return;
            }
            
            ListIterator<String> chan_it = channels.listIterator();
            while(chan_it.hasNext() && state == 0)
    		{
            	
    			String line = chan_it.next();
    			Log.d("VDRCHAN",line);
    			if(line.startsWith("250 "))
    				break;
    			String cnr = line.split(" ",2)[0].split("-",2)[1];
    			String cname = line.split(" ",2)[1].split(";",2)[0];
    			String cnow = "Keine EPG Daten";
    			int ctime = 0;
    			int cdur = 0;
    			
    			List<String> epg = vdr.getListData("LSTE "+ cnr + " now");
				ListIterator<String> epg_it = epg.listIterator();
				while(epg_it.hasNext())
				{
					String epg_line = epg_it.next();
					if(epg_line.startsWith("215-T"))
						cnow = epg_line.split(" ", 2)[1];
					if(epg_line.startsWith("215-E"))
					{
						ctime = Integer.parseInt(epg_line.split(" ")[2]);
						cdur = Integer.parseInt(epg_line.split(" ")[3]);
					}
				}
				
    			Message msg = mHandler.obtainMessage();
   		     	Bundle b = new Bundle();
   		     	b.putString("name", cname);
   		     	b.putString("num", cnr);
   		     	b.putString("now", cnow);
   		     	b.putInt("time", ctime);
   		     	b.putInt("dur", cdur);
   		     	
   		     	msg.setData(b);
   		     	mHandler.sendMessage(msg);	
    		}
    		vdr.close();
        }
	}
}
