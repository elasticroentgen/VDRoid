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

import android.util.Log;

public class SVDRP {
	private Socket sock;
	private InputStream net_in;
	private OutputStream net_out;
	private PrintWriter net_write;
	private BufferedReader net_read;
	private SocketAddress sockaddr;
	private String greet;
	private Boolean sock_ready;
	
	public SVDRP (String host, int port)
	{
		greet = "N/A";
		sock_ready = false;
		sockaddr = new InetSocketAddress(host, 2001);
       
	}
	
	private void connectSocket() throws Exception
	{
		try {
			sock = new Socket();
			sock.setSoTimeout(2000);
			sock.connect(sockaddr, 10000);
			net_in = sock.getInputStream();
			InputStreamReader isr = new InputStreamReader(net_in);
			net_read = new BufferedReader(isr,8192);
			net_out = sock.getOutputStream();
			net_write = new PrintWriter(net_out, true);
			greet = net_read.readLine();
			sock_ready = true;
		} catch (IOException e) {
			Log.d("SVDRP","I/O Error or Connection Timeout");
			sock.close();
			sock_ready = false;
			throw new Exception();
		}

	}
	
	public void close()
	{
		try {
			sock.close();
			sock_ready = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getGreeting()
	{
		//Make Connection when nessesary
		if(!sock_ready)
			try {
				connectSocket();
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		return greet;
	}
	
	public String getData(String query)
	{
		String result = "";
		
		//Make Connection when nessesary
		if(!sock_ready)
			try {
				connectSocket();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return result;
			}

		net_write.println(query);
		try {
			result = net_read.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public List<String> getListData(String query)
	{
		List<String> result = new ArrayList<String>();
		//Make Connection when nessesary
		if(!sock_ready)
			try {
				connectSocket();
			} catch (Exception e1) {
				return null;
			}
		
		//Restdaten rauslaufen lassen
		try {
			while(net_read.ready())
				net_read.read();
		} catch (IOException e1) {
			return null;
		}	
			
		try {
			net_write.println(query);
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
				line = net_read.readLine();	
				result.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
	
}
