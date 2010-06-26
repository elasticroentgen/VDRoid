package kits.vdroid;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class vdr_stream_channel extends Activity {
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri streamUri = getIntent().getData();
        String streamurl = "http://" + streamUri.getHost() + ":3000/extern;DROID" + streamUri.getPath();
        Log.d("VDRSTREAM", "trying to Stream " + streamurl);
        streamurl = "http://osiris/driod-mp4.mp4";
        
        MediaPlayer mp = new MediaPlayer();
        try {
			mp.setDataSource(streamurl);
			mp.prepare();
	        mp.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           
	}

}
