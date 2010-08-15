package kits.vdroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class vdr_remote extends Activity {
	
	String host;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote);
        host = getIntent().getData().getHost();
        
        
        Button btn_0 = ((Button) findViewById(R.id.remo_0));
        btn_0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("0");
            }
        });
        
        Button btn_1 = ((Button) findViewById(R.id.remo_1));
        btn_1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("1");
            }
        });
        
        Button btn_2 = ((Button) findViewById(R.id.remo_2));
        btn_2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("2");
            }
        });
        
        Button btn_3 = ((Button) findViewById(R.id.remo_3));
        btn_3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("3");
            }
        });
        
        Button btn_4 = ((Button) findViewById(R.id.remo_4));
        btn_4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("4");
            }
        });
        
        Button btn_5 = ((Button) findViewById(R.id.remo_5));
        btn_5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("5");
            }
        });
        
        Button btn_6 = ((Button) findViewById(R.id.remo_6));
        btn_6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("6");
            }
        });
        
        Button btn_7 = ((Button) findViewById(R.id.remo_7));
        btn_7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("7");
            }
        });
        
        Button btn_8 = ((Button) findViewById(R.id.remo_8));
        btn_8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("8");
            }
        });
        
        Button btn_9 = ((Button) findViewById(R.id.remo_9));
        btn_9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("9");
            }
        });
        
        // COLORS
        
        Button btn_blue = ((Button) findViewById(R.id.remo_blue));
        btn_blue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Blue");
            }
        });
        
        Button btn_red = ((Button) findViewById(R.id.remo_red));
        btn_red.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Red");
            }
        });
        
        Button btn_green = ((Button) findViewById(R.id.remo_green));
        btn_green.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Green");
            }
        });
        Button btn_yellow = ((Button) findViewById(R.id.remo_yellow));
        btn_yellow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Yellow");
            }
        });
        
        
        //NAVIGATION
        
        Button btn_up = ((Button) findViewById(R.id.remo_up));
        btn_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Up");
            }
        });
        
        Button btn_down = ((Button) findViewById(R.id.remo_down));
        btn_down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Down");
            }
        });
        
        Button btn_left = ((Button) findViewById(R.id.remo_left));
        btn_left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Left");
            }
        });
        
        Button btn_right = ((Button) findViewById(R.id.remo_right));
        btn_right.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Right");
            }
        });
        
        Button btn_menu = ((Button) findViewById(R.id.remo_menu));
        btn_menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Menu");
            }
        });
        
        Button btn_ok = ((Button) findViewById(R.id.remo_ok));
        btn_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Ok");
            }
        });
        
        Button btn_back = ((Button) findViewById(R.id.remo_exit));
        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Back");
            }
        });
        
        Button btn_epg = ((Button) findViewById(R.id.remo_epg));
        btn_epg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Schedule");
            }
        });
        
        Button btn_volu = ((Button) findViewById(R.id.remo_volup));
        btn_volu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Volume+");
            }
        });
        
        Button btn_vold = ((Button) findViewById(R.id.remo_voldown));
        btn_vold.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Volume-");
            }
        });

        Button btn_info = ((Button) findViewById(R.id.remo_info));
        btn_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendKey("Info");
            }
        });

	}
	

	private void sendKey(String key)
	{
		Log.d("VDREMOTE","Sending Key:" + key);
		SVDRP vdr = new SVDRP(host,vdr_remote.this);
		vdr.getData("HITK " + key);
		vdr.close();
	}
	
	
	
}
