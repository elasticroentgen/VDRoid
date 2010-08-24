//TODO: Crash bei Access Denied vom SVDRP-Service http://www.vdr-portal.de/board/thread.php?postid=922531#post922531

package kits.vdroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;


//import org.bouncycastle.jce.provider.BouncyCastleProvider;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class SVDRP {
	private Socket sock;
	private InputStream net_in;
	private OutputStream net_out;
	private PrintWriter net_write;
	private BufferedReader net_read;
	private SocketAddress sockaddr;
	private String greet;
	private VDRDBHelper db;
	private String server;
	private Context parent;

	
	public SVDRP (String iserver, Context iparent)
	{
		greet = "N/A";
		server = iserver;
		parent = iparent;
		
		db = new VDRDBHelper(parent);
		db.init();
		String hostname = db.getHostByName(server);
		if(hostname.length() == 0)
			hostname = "localhost";
		sockaddr = new InetSocketAddress(hostname, db.getPortByName(server));

		db.close();
		sock = new Socket();
		
	}
		
	private void sendEnc(String input)
	{
		if(!sock.isConnected())
		{
			Log.d("SVDRP", "Not connected when trying to send!");
			return;
			
		}
		net_write.write(input+"\n");
		net_write.flush();
	}
	
	private String readEnc() {
		try {
			int rdy_wait = 0;
			while(!net_read.ready())
			{
				//Only wait 10secs then throw exeption
				if(rdy_wait > 50)
				{
					Toast.makeText(parent, "SVDRP Connection Error. Check connection and try again!", Toast.LENGTH_LONG).show();
					return null;
				}
				Thread.sleep(200);
				rdy_wait++;
				
			}
			String line = net_read.readLine();
			return line;
		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	private void connectSocket() throws Exception
	{
		
		if(sock.isConnected())
			return;

		try {

			sock = new Socket();
			sock.setSoTimeout(2000);
			sock.connect(sockaddr, 10000);
			
			Log.d("SVDRPC", "Starting non-encrypted Communication");

			net_in = sock.getInputStream();
			net_out = sock.getOutputStream();
			
			InputStreamReader isr = new InputStreamReader(net_in);
			net_read = new BufferedReader(isr,8192);
			net_write = new PrintWriter(net_out, true);
						
			//Handshaking Encrypted Connection
			
			greet = readEnc();
			
		} catch (IOException e) {
			Log.d("SVDRP","I/O Error or Connection Timeout");
			sock.close();
			throw new Exception();
		}

	}

	public void close()
	{
		try {
			sock.close();
			Log.d("SVDRP","Closed connection to SVDRP Host.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	public String getGreeting()
	{
		
		try {
			connectSocket();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return greet;
	}
	
	public String getData(String query)
	{
		String result = "";
		
		//Make Connection when nessesary
		try {
			connectSocket();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return result;
		}
		
		sendEnc(query);
		result = readEnc();
		
		return result;
	}
	
	public List<String> getListData(String query)
	{
		List<String> result = new ArrayList<String>();
		//Make Connection when nessesary
		
		try {
			connectSocket();
		} catch (Exception e1) {
			return null;
		}
					
		try {
			sendEnc(query);
			String line;
			//wait for data
			while(!net_read.ready())
			{	
				//Log.d("SVDRP", "Waiting for Data to arrive...");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			while(net_read.ready())
			{
				line = readEnc();	
				result.add(line);
				if(line.charAt(3) == '-' && !net_read.ready())
				{
					while(!net_read.ready())
					{
						Log.d("ARRAYREAD", "Data from VDR not complete, but netbuffers empty. Waiting for next data to arrive");
						Thread.sleep(100);
					}
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
}
