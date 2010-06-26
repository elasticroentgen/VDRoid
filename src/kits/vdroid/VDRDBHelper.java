package kits.vdroid;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class VDRDBHelper {
private static final String DATABASE_NAME = "vdroid.db";
   private static final int DATABASE_VERSION = 2;
 
   private Context context;
   private SQLiteDatabase db;
   private SQLiteStatement insertServer;


   public VDRDBHelper(Context context) {
      this.context = context;
      OpenHelper openHelper = new OpenHelper(this.context);
      this.db = openHelper.getWritableDatabase();
      this.insertServer = this.db.compileStatement("INSERT INTO SERVERS (NAME,HOST) VALUES (?,?)");
   }

   public void addServer(String name, String host)
   {
	  insertServer.bindString(1, name);
	  insertServer.bindString(2, host);
	  insertServer.executeInsert();
   }
   
   public Cursor getServersCursor()
   {
	   Cursor c = db.rawQuery("SELECT ID as _id,NAME,HOST FROM SERVERS ORDER BY NAME",null);
	   return c;
   }
   
   public String getServernameById(long id)
   {
	   Cursor c = db.rawQuery("SELECT NAME FROM SERVERS WHERE ID = "+ String.valueOf(id),null); 
	   c.moveToFirst();
	   return c.getString(0);
   }
   
   public String getHostByName(String name)
   {
	   Cursor c = db.rawQuery("SELECT HOST FROM SERVERS WHERE NAME = '"+ name + "'",null); 
	   c.moveToFirst();
	   return c.getString(0);
   }
   
   public void deleteServerById(long id)
   {
	   db.execSQL("DELETE FROM SERVERS WHERE ID = " + String.valueOf(id));
   }
   
   public String[] getServerNames()
   {
	   String[] servers = {};
	   List<String> srv_list = new ArrayList<String>();
	   Cursor servernames = this.db.query("SERVERS", new String[] { "NAME" }, null, null, null, null, "name");
	   if(servernames.moveToFirst())
	   {
		   do
		   {
			   srv_list.add(servernames.getString(0));
		   } while(servernames.moveToNext());
		   
	   }
	   servernames.close();
	   if(!srv_list.isEmpty())
		   servers = (String[]) srv_list.toArray(new String[0]);
	   return servers;
   }
   
   public Boolean serverExists(String name)
   {
	   Cursor cursor = db.rawQuery("SELECT NAME FROM SERVERS WHERE NAME='"+ name +"';", null);
	   if(cursor.getCount() == 0)
		   return false;
	   else
		   return true;
   }
   
   private static class OpenHelper extends SQLiteOpenHelper {
     OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE SERVERS (id INTEGER PRIMARY KEY, name TEXT, host TEXT)");
     }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

   
   }
}
