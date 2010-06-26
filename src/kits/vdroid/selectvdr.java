package kits.vdroid;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class selectvdr extends ListActivity {
    /** Called when the activity is first created. */
	final Intent new_server_intent = new Intent(Intent.ACTION_VIEW);
	final Intent del_server_intent = new Intent(Intent.ACTION_VIEW);
	final Intent show_vdr_intent = new Intent(Intent.ACTION_VIEW);
	
	//Dialogs
	static final int DIALOG_CONNECTING_ID = 0;
	VDRDBHelper db;
    @Override
    public void onStart() {
    	super.onStart();
    	
    	//Intents
        new_server_intent.setClass(this, kits.vdroid.add_new_server.class);
        del_server_intent.setClass(this, kits.vdroid.del_servers.class);
        show_vdr_intent.setClass(this, kits.vdroid.vdr_show.class);
        
    	//Check if we have a stored VDR-Connection
      	db = new VDRDBHelper(selectvdr.this);
		String[] VDR_SERVERS = db.getServerNames();
    	
		if(VDR_SERVERS.length > 0)
		{
			//Serverliste anlegen
			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, VDR_SERVERS));
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("Neue VDR-Verbindung anlegen?")
	    	       .setCancelable(false)
	    	       .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                startActivity(new_server_intent);
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
		
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
               
        ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener()
		{
		    public void onItemClick(AdapterView<?> parent, android.view.View view,int position, long id)
		    {
		      // When clicked, show a toast with the TextView text
		      String vdrname = (String) ((TextView) view).getText();
		      String vdrhost = db.getHostByName(vdrname);
		      Uri data = Uri.parse("vdr://" + vdrhost + "/?" + vdrname);
		      show_vdr_intent.setData(data);    
		      startActivity(show_vdr_intent);
		      
		    }
		});
		
    }
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    switch(id) {
	    case DIALOG_CONNECTING_ID:
	        // do the work to define the pause Dialog
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	//Menu
	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, 1, 0, "Neue VDR-Verbindung");
	    menu.add(0, 2, 0, "VDR-Verbindung entfernen");
	    return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case 1:
	    	startActivity(new_server_intent);
	        return true;
	    case 2:
	        startActivity(del_server_intent);
	        return true;
	    }
	    return false;
	}
	
}