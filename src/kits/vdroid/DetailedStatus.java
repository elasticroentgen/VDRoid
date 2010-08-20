package kits.vdroid;

import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DetailedStatus extends Activity {
	
	String host;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedstatus);
        Uri data = getIntent().getData();
        host = data.getHost();
        
      
        Log.d("VDRINFO", "Getting Status for "+  host);
        
        final SVDRP vdr = new SVDRP(host,this);
        
        //Get Data from VDR
        String server_greeting = vdr.getGreeting();
        String actchan_line = vdr.getData("CHAN");
        String actdisk_line = vdr.getData("STAT DISK");
        
        Boolean femon_inst = false;
        List<String> plugins = vdr.getListData("PLUG");
        ListIterator<String> plug_it = plugins.listIterator();
		while(plug_it.hasNext())
		{
			String line = plug_it.next();
			if(line.contains("femon"))
			{
				femon_inst = true;
				break;
			}
		}
        
		if(femon_inst)
		{
			String femon_dev = vdr.getData("PLUG femon name"); //900 Afatech AF9013 DVB-T on device #0
			String femon_signal = vdr.getData("PLUG femon sgnl"); //900 7001 (43%) on device #0
			femon_dev = femon_dev.split(" ",2)[1];
			femon_signal = femon_signal.split(" ",2)[1];
			((TextView) findViewById(R.id.dstatus_device_v)).setText(femon_dev);
			((TextView) findViewById(R.id.dstatus_signal_v)).setText(femon_signal);
		}
		else
		{
			((TextView) findViewById(R.id.dstatus_device_v)).setText(getResources().getText(R.string.dstatus_nofemon));
			((TextView) findViewById(R.id.dstatus_signal_v)).setText(getResources().getText(R.string.dstatus_nofemon));
		}
        
		vdr.close();
        String version = server_greeting.split(" ")[4];
        String freespace_mb = actdisk_line.split(" ")[2];
        String freespace_per = actdisk_line.split(" ")[3];
        String freespace = freespace_per + "  (" + freespace_mb + ")";
        String[] actchan = actchan_line.split(" ",3);
        
        String chan = actchan[1] + " - " + actchan[2];
        
        
        ((TextView) findViewById(R.id.dstatus_version_v)).setText(version);
        ((TextView) findViewById(R.id.dstatus_disk_v)).setText(freespace);
        ((TextView) findViewById(R.id.dstatus_channel_v)).setText(chan);

        
        
	}
	
	
	
	
	
}
