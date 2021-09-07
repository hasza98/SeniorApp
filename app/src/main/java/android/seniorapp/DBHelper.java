package android.seniorapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.seniorapp.model.Person;
import android.seniorapp.model.studyType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

   private Context context;

   // File names
   public static final String SENIOR_FILE = "Seniors.txt";

   // Inner object name
   public static final String JSON_LIST = "list";

   // Database name
   public static final String DATABASE_NAME = "Senior.db";

   // Table
   public static final String SENIOR_TABLE = "senior";

   // Table columns
   // Chapter
   public static final String SENIOR_ID = "id";
   public static final String SENIOR_NAME = "name";
   public static final String SENIOR_DESC = "description";
   public static final String SENIOR_TYPE = "type";
   public static final String SENIOR_YEAR = "year";
   public static final String SENIOR_IMGSOURCE = "source";

   // Table Create statements
   // Language
   /*
   public static final String CREATE_LANGUAGE_TABLE = "CREATE TABLE " + LANGUAGE_TABLE + "(" +
           LANGUAGE_ID + " INTEGER PRIMARY KEY, " +
           LANGUAGE_NAME + " TEXT " + ")";
*/

   // Chapter
   public static final String CREATE_CHAPTER_TABLE = "CREATE TABLE " + SENIOR_TABLE + "( " +
           SENIOR_ID + " INTEGER PRIMARY KEY, " +
           SENIOR_NAME + " TEXT, " +
           SENIOR_DESC + " TEXT, " +
           SENIOR_TYPE + " TEXT, " +
           SENIOR_YEAR + " INTEGER, " +
           SENIOR_IMGSOURCE + " TEXT " + ")";

   public DBHelper(Context context) {
      super(context, DATABASE_NAME , null, 1);
      this.context = context;
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      // TODO Auto-generated method stub
      //db.execSQL(CREATE_LANGUAGE_TABLE);
      db.execSQL(CREATE_CHAPTER_TABLE);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // TODO Auto-generated method stub
      //db.execSQL("DROP TABLE IF EXISTS "+LANGUAGE_TABLE);
      db.execSQL("DROP TABLE IF EXISTS "+SENIOR_TABLE);
      onCreate(db);
   }

   public void populateDatabase() {
      this.populateSeniors();
   }

   /*
   public void populateLanguages() {
      String json = null;
      try {
         InputStream is = context.getAssets().open(LANGUAGES_FILE);
         int size = is.available();
         byte[] buffer = new byte[size];
         is.read(buffer);
         is.close();
         json = new String(buffer, StandardCharsets.UTF_8);
         JSONObject obj = new JSONObject(json);
         JSONArray list = obj.getJSONArray(JSON_LIST);
         for(int i=0; i<list.length(); i++)
         {
            this.insertLanguage(new Language(
                    list.getJSONObject(i).getInt(LANGUAGE_ID),
                    list.getJSONObject(i).getString(LANGUAGE_NAME)
            ));
         }
      }
      catch (IOException | JSONException ex) {
         ex.printStackTrace();
      }
   }
   */

   public void populateSeniors() {
      String json = null;
      try {
         InputStream is = context.getAssets().open(SENIOR_FILE);
         int size = is.available();
         byte[] buffer = new byte[size];
         is.read(buffer);
         is.close();
         json = new String(buffer, StandardCharsets.UTF_8);
         JSONObject obj = new JSONObject(json);
         JSONArray list = obj.getJSONArray(JSON_LIST);
         for (int i = 0; i < list.length(); i++) {
            this.insertPerson(new Person(
                    list.getJSONObject(i).getInt(SENIOR_ID),
                    list.getJSONObject(i).getString(SENIOR_NAME),
                    list.getJSONObject(i).getString(SENIOR_DESC),
                    studyType.valueOf(list.getJSONObject(i).getString(SENIOR_TYPE)),
                    list.getJSONObject(i).getInt(SENIOR_YEAR),
                    list.getJSONObject(i).getString(SENIOR_IMGSOURCE)));
         }
      } catch (IOException | JSONException ex) {
         ex.printStackTrace();
      }
   }

   /*
   private void insertLanguage(Language language) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(LANGUAGE_ID, language.getId());
      values.put(LANGUAGE_NAME, language.getName());
      db.insert(LANGUAGE_TABLE,null,values);
   }
   */

   public void insertPerson(Person p) {
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues values = new ContentValues();
      values.put(SENIOR_ID, p.getId());
      values.put(SENIOR_NAME, p.getName());
      values.put(SENIOR_DESC, p.getDescription());
      values.put(SENIOR_TYPE, p.getType().toString());
      values.put(SENIOR_YEAR, p.getYear());
      values.put(SENIOR_IMGSOURCE, p.getImgSource());
      db.insert(SENIOR_TABLE,null,values);
   }

   public Person getPerson(int id) {
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "SELECT * FROM " + SENIOR_TABLE + " WHERE "+ SENIOR_ID + "=" + id, null );
      res.getExtras();
      res.moveToFirst();
      Person p = new Person(
              res.getInt(res.getColumnIndex(SENIOR_ID)),
              res.getString(res.getColumnIndex(SENIOR_NAME)),
              res.getString(res.getColumnIndex(SENIOR_DESC)),
              studyType.valueOf(res.getString(res.getColumnIndex(SENIOR_TYPE))),
              res.getInt(res.getColumnIndex(SENIOR_YEAR)),
              res.getString(res.getColumnIndex(SENIOR_IMGSOURCE)));
      res.close();
      return p;
   }

   public ArrayList<Person> getPersons() {
      ArrayList<Person> pList = new ArrayList<Person>();
      SQLiteDatabase db = this.getReadableDatabase();
      Cursor res =  db.rawQuery( "SELECT * FROM " + SENIOR_TABLE , null);
      res.moveToFirst();
      while(!res.isAfterLast()){
         pList.add(new Person(
                 res.getInt(res.getColumnIndex(SENIOR_ID)),
                 res.getString(res.getColumnIndex(SENIOR_NAME)),
                 res.getString(res.getColumnIndex(SENIOR_DESC)),
                 studyType.valueOf(res.getString(res.getColumnIndex(SENIOR_TYPE))),
                 res.getInt(res.getColumnIndex(SENIOR_YEAR)),
                 res.getString(res.getColumnIndex(SENIOR_IMGSOURCE))));
         res.moveToNext();
      }
      res.close();
      return pList;
   }
}