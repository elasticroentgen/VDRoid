package kits.vdroid;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

public class DetailedStatus extends Activity {
	
	String host;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedstatus);
        Uri data = getIntent().getData();
        host = data.getHost();
        
      
        
	}
	
	
	
	
	
}
