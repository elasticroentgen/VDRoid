package kits.vdroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class add_new_server extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.add_server);

	    final Button button = (Button) findViewById(R.id.new_server_add);
	    button.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	            // Perform action on clicks
	        	final EditText ui_host = (EditText) findViewById(R.id.new_server_host_edit);
	        	String host = ui_host.getText().toString();
	        	
	        	final EditText ui_name = (EditText) findViewById(R.id.new_server_name_edit);
	        	String name = ui_name.getText().toString();
	        	
	        	final EditText ui_key = (EditText) findViewById(R.id.new_server_host_key);
	        	String key = ui_key.getText().toString();
	        	
	        	final EditText ui_port = (EditText) findViewById(R.id.new_server_port);
	        	String port = ui_port.getText().toString();
	        	
	        	CheckBox cb = (CheckBox) findViewById(R.id.new_server_isenc);
	        	Boolean encon = false;
	        	if(cb.isChecked())
	        		encon = true;
	        	
	        	VDRDBHelper db = new VDRDBHelper(add_new_server.this);

	        	if(name.length() == 0 && host.length() == 0)
	        		Toast.makeText(add_new_server.this,"Name oder Host fehlt!", Toast.LENGTH_LONG).show();
	        	else if(db.serverExists(name))
	        		Toast.makeText(add_new_server.this,"Eintrag exisitert bereits!", Toast.LENGTH_LONG).show();
	        	else
	        	{
	        		//Save new Server to Database
	        		
	        		db.addServer(name, host,port, encon, key);
	        		Toast.makeText(add_new_server.this,name + " hinzugefügt!", Toast.LENGTH_LONG).show();
	        		finish();
	        	}
	        		
	        }
	    }); 
	    
	}

}
