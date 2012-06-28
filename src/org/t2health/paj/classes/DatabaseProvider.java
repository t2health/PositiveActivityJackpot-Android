package org.t2health.paj.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Handles all database operations
 * 
 * @author Steve Ody (stephen.ody@tee2.org)
 */

public class DatabaseProvider 
{

	private static final String DATABASE_NAME = "paarmixare.db";
	private static final int DATABASE_VERSION = 38;

	private Context context;
	private SQLiteDatabase db;

	public DatabaseProvider(Context context) 
	{
		this.context = context;      
	}

	public String scrubInput(String input)
	{
		//TODO: make robust
		String Output = input.replace("'", "''");
		return Output;
	}

	public List<String> checkContacts()
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select Contactname from Contacts order by contactname limit 2";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String row = cursor.getString(0);
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}
	
	public List<String> checkMinActivity()
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select Activityname from Activities where ActivityEnabled=1 order by Activityname limit 10";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String row = cursor.getString(0);
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public boolean checkUnratedActivites() 
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select SavedActivityID from savedactivities where SavedActivityID not in (select SavedActivityID from SAContactRating)";

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
			db.close();
			return true;
		}
		else
		{
			db.close();
			return false;
		}
	}

	public boolean insertSavedActivity(String SelectedAttendance, String SelectedContacts, String SelectedActivityID, String LocalName, String LocalAddress, String LocalPhone)
	{

		//TODO: Convoluted process
		try
		{
			OpenHelper openHelper = new OpenHelper(this.context);
			this.db = openHelper.getWritableDatabase();

			db.execSQL("insert into SavedActivities (SelectedAttendance, SelectedActivityID, SavedDate, LocalName, LocalAddress, LocalPhone) values('" + scrubInput(SelectedAttendance) + "'," + scrubInput(SelectedActivityID) + ",'" + DateFormat.getDateInstance().format(new Date()) + "', '" + scrubInput(LocalName) + "', '" + scrubInput(LocalAddress) + "', '" + scrubInput(LocalPhone) + "')");

			//Save contacts
			String[] splitContacts = SelectedContacts.split("\\|");
			for(int a = 0; a < splitContacts.length; a++)
			{
				String cName = scrubInput(splitContacts[a]);

				//String contactID = "";

				//Cursor cursor = this.db.rawQuery(query, null);
				//if (cursor.moveToFirst()) 
				//{
					//do 
					//{
						//contactID = cursor.getString(0);
						db.execSQL("insert into SAContactRating (ContactName, SavedActivityID) values('" + cName + "', (select savedactivityid from savedactivities order by savedactivityid desc limit 1))" );
					//} 
					//while (cursor.moveToNext());
				//}
				//if (cursor != null && !cursor.isClosed()) 
				//{
				//	cursor.close();
				//}
			}

			db.close();

			return true;

		}
		catch(Exception ex)
		{
			db.close();
			Global.Log.v("insertSavedActivity", ex.toString());
			return false;
		}
	}

	public boolean insertRating(int SavedActivityID, String field, String RatingValue)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			if(field.equals("ContactRating"))
			{
				db.execSQL("update SAContactRating set ContactRating =" + scrubInput(RatingValue) + " where SavedActivityID=" + SavedActivityID);
			}
			else
			{
				Global.Log.v("DBSAVESOCIALRATING", "Updating SA=" + SavedActivityID + " RATING=" + RatingValue);
				db.execSQL("update SavedActivities set " + field + "=" + scrubInput(RatingValue) + " where SavedActivityID=" + SavedActivityID);
			}
			db.close();

			return true;
		}
		catch(Exception ex)
		{
			db.close();
			Global.Log.v("DBSAVERATING", ex.toString());
			return false;
		}
	}

	public void insertContacts(ArrayList<String> inContacts)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		db.execSQL("delete from contacts");

		try
		{
			int conLen = inContacts.size();
			for(int a=0; a< conLen; a++)
			{
				try
				{
					db.execSQL("insert into contacts (contactname) values('" + scrubInput(inContacts.get(a).toString().trim()) + "')");
				}
				catch(Exception ex)
				{

				}
			}
		}
		catch(Exception ex)
		{

		}

		db.close();
	}

	public String selectActivityCategory(String activityID)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select ActivityCategory from Activities where ActivityID = " + activityID;

		String result = "";

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				result = cursor.getString(0);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		db.close();

		return result;
	}

	public String selectActivityName(String activityID)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select ActivityName from Activities where ActivityID = " + activityID;

		String result = "";

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				result = cursor.getString(0);
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return result;
	}

	public String selectActivityAttendance(String activityID)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select ActivityAttendance from Activities where ActivityID = " + activityID;

		String result = "";

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				result = cursor.getString(0);
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return result;
	}

	public boolean selectActivityEnabled(String activityID)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select ActivityEnabled from Activities where ActivityID = " + activityID;

		int result = 1;

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				result = cursor.getInt(0);
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		boolean outResult = true;
		if(result == 1)
			outResult = true;
		else
			outResult = false;

		return outResult;
	}

	public String selectActivityType(int subcategoryID)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select GoogleType from Activities where ActivityID = " + subcategoryID;

		String result = "";

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				result = cursor.getString(0);
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return result;
	}

	public List<String> selectAllActivities(String category, String attendance) 
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select Activityid from activities where activitycategory='" + scrubInput(category) + "' and activityattendance like '%" + scrubInput(attendance) + "%'";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String row = cursor.getString(0);
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public List<String> selectAttendanceWeighted()
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select AttendanceName, (select sum(attendancerating) from savedactivities sa where selectedattendance = att.attendancevalue ) as weight from Attendance att";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String row = cursor.getString(0);
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public List<String> selectCategories() 
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select distinct activitycategory from activities where activityenabled = 1 order by activitycategory COLLATE nocase ASC";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String row = cursor.getString(0);
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		//Add special categories
		try
		{
			if(Global.RoadTripList.size() > 0)
				list.add("Road Trip");
		}
		catch(Exception ex)
		{}
		return list;
	}

	public List<String> selectCategoriesWeighted() 
	{

		//Weighting of the categories happens by summing the total ratings for that category, and putting (sum) copies
		//into the list. The list is then shuffled so that we don't end up with a long visible run of the same category.
		//TODO: reduce the (sum) amount to maybe a percentage of 10.
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select distinct activitycategory, (select sum(categoryrating) from savedactivities sa join activities act2 on act2.activityid = sa.selectedactivityid where act2.activitycategory = act.activitycategory ) as weight from activities act order by activitycategory COLLATE nocase ASC";
		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				if(cursor.getString(1) != null)
				{
					int weight = Integer.parseInt(cursor.getString(1));
					for(int i = 0; i < weight; i++)
					{
						String row = cursor.getString(0);
						list.add(row);
					}
				}
				else //No ratings for this category
				{
					String row = cursor.getString(0);
					list.add(row);
				}
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		//Add special categories
		try
		{
			if(Global.RoadTripList.size() > 0)
				list.add("Road Trip");
		}
		catch(Exception ex)
		{}

		//Thanks java!
		Collections.shuffle(list);

		return list;
	}

	public List<String> selectContacts()
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select Contactname from Contacts order by contactname";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String row = cursor.getString(0);
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public List<String> selectContactsWeighted()
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select Contactname, (select sum(contactrating) from SAContactRating sa where sa.contactname = oc.contactname) as weight from Contacts oc order by contactname";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				if(cursor.getString(1) != null)
				{
					int weight = Integer.parseInt(cursor.getString(1));
					for(int i = 0; i < weight; i++)
					{
						String row = cursor.getString(0);
						list.add(row);
					}
				}
				else //No ratings for this activity
				{
					String row = cursor.getString(0);
					list.add(row);
				}
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		//Thanks java!
		Collections.shuffle(list);

		return list;
	}

	public String[] selectContactEmailPhone(Context ctx, String displayName)
	{
		Cursor people = ctx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		String phoneNumber = "";
		String emailAddress = "";
		String[] results = new String[2];

		while(people.moveToNext()) 
		{
			String contactId = people.getString(people.getColumnIndex(ContactsContract.Contacts._ID)); 
			int nameFieldColumnIndex = people.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String contact = people.getString(nameFieldColumnIndex);

			if(contact.equals(displayName))
			{
				if (Integer.parseInt(people.getString(people.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
				{
					Cursor phones = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{contactId}, null);
					while (phones.moveToNext()) 
					{
						phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.DATA));
						break;
					} 
					phones.close();
				}
				Cursor emails = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null); 
				while (emails.moveToNext()) 
				{ 
					// This would allow you get several email addresses 
					emailAddress = emails.getString( emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)); 
					break;
				} 
				emails.close();
				break;
			}
		}

		people.close();

		results[0] = phoneNumber;
		results[1] = emailAddress;

		return results;
	}

	public List<String> selectEnabledActivities(String category, String attendance) 
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select Activityid from activities where activitycategory='" + scrubInput(category) + "' and activityattendance like '%" + scrubInput(attendance) + "%' and activityenabled=1";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String row = cursor.getString(0);
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public List<String> selectEnabledActivitiesWeighted(String category, String attendance) 
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select Activityid, (select sum(activityrating) from savedactivities sa where selectedactivityid = act.activityid ) as weight from activities act where activitycategory='" + scrubInput(category) + "' and activityattendance like '%" + scrubInput(attendance) + "%' and activityenabled=1";

		List<String> list = new ArrayList<String>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				if(cursor.getString(1) != null)
				{
					int weight = Integer.parseInt(cursor.getString(1));
					for(int i = 0; i < weight; i++)
					{
						String row = cursor.getString(0);
						list.add(row);
					}
				}
				else //No ratings for this activity
				{
					String row = cursor.getString(0);
					list.add(row);
				}
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		//Thanks java!
		Collections.shuffle(list);

		return list;
	}

	public int selectLastActivityID() 
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select savedactivityid from savedactivities order by savedactivityid desc limit 1";
		int lastID = 0;

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			lastID = cursor.getInt(0);
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return lastID;
	}

	public List<String> selectPhoneContacts(Context ctx)
	{
		List<String> list = new ArrayList<String>();
		Cursor people = ctx.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while(people.moveToNext()) 
		{
			int nameFieldColumnIndex = people.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String contact = people.getString(nameFieldColumnIndex);
			list.add(contact);
		}

		people.close();
		return list;
	}

	public List<String[]> selectSavedActivity(int activityID) 
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		//String query = "select sa.SavedActivityID, SelectedAttendance, SelectedContacts, SelectedCategory, SelectedSubcategory, SelectedActivity, SavedDate, LocalOption, r.RatingValue from savedactivities sa left join ratings r on sa.SavedActivityID = r.SavedActivityID where sa.SavedActivityID =" + activityID + " order by sa.SavedActivityID desc";
		String query = "select sa.SavedActivityID, SelectedAttendance, ActivityCategory, ActivityName, SavedDate, localname, AttendanceRating, CategoryRating, ActivityRating, (select contactrating from SAContactRating where SavedActivityID = " + activityID + " limit 1) as ContactRating, localaddress, localphone from savedactivities sa left join Activities act on act.activityID = sa.selectedActivityID left join SAContactRating cr on sa.SavedActivityID = cr.SavedActivityID where sa.SavedActivityID =" + activityID + " order by sa.SavedActivityID desc";

		List<String[]> list = new ArrayList<String[]>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			//			do 
			//			{
			String[] row = new String[] {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11)};
			list.add(row); 
			//			} 
			//			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public List<String[]> selectSavedActivityContacts(int activityID) 
	{

		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		//String query = "select sa.SavedActivityID, SelectedAttendance, SelectedContacts, SelectedCategory, SelectedSubcategory, SelectedActivity, SavedDate, LocalOption, r.RatingValue from savedactivities sa left join ratings r on sa.SavedActivityID = r.SavedActivityID where sa.SavedActivityID =" + activityID + " order by sa.SavedActivityID desc";
		String query = "select contactname from SAContactRating where SavedActivityID = " + activityID ;

		List<String[]> list = new ArrayList<String[]>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String[] row = new String[] {cursor.getString(0)};
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public List<String[]> selectSavedActivities() 
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
		String query = "select distinct sa.SavedActivityID, SelectedAttendance, ActivityCategory, ActivityName, SavedDate, AttendanceRating, CategoryRating, ActivityRating, ContactRating, LocalName from savedactivities sa left join Activities act on act.activityID = sa.selectedActivityID left join SAContactRating cr on sa.SavedActivityID = cr.SavedActivityID order by sa.SavedActivityID desc";

		List<String[]> list = new ArrayList<String[]>();

		Cursor cursor = this.db.rawQuery(query, null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				String[] row = new String[] {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)};
				list.add(row); 
			} 
			while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) 
		{
			cursor.close();
		}

		db.close();

		return list;
	}

	public void updateActivityEnabled(int activityID, int enabled)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			String upd = "update activities set activityenabled = " + enabled + " where activityid = " + activityID;
			db.execSQL(upd);
			db.close();
		}
		catch(Exception ex){

			Global.Log.v("DBPROVIDER", ex.toString());
		}
	}

	public void updateAllActivitiesEnabled()
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			String upd = "update activities set activityenabled = 1";
			db.execSQL(upd);
			db.close();
		}
		catch(Exception ex){

			Global.Log.v("DBPROVIDER", ex.toString());
		}
	}
	
	public void updateAllActivitiesDisabled()
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			String upd = "update activities set activityenabled = 0";
			db.execSQL(upd);
			db.close();
		}
		catch(Exception ex){

			Global.Log.v("DBPROVIDER", ex.toString());
		}
	}
	
	public void updateClearContacts()
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			String upd = "delete from contacts";
			db.execSQL(upd);
			db.close();
		}
		catch(Exception ex){

			Global.Log.v("DBPROVIDER", ex.toString());
		}
	}
	
	public void updateClearSavedActivities()
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			String upd = "delete from SavedActivities"; 
			db.execSQL(upd);
			upd = "delete from SAContactRating";
			db.execSQL(upd);
			db.close();
		}
		catch(Exception ex){

			Global.Log.v("DBPROVIDER", ex.toString());
		}
	}
	
	public void DeleteActivity(int savedactivityID)
	{
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

		try
		{
			String upd = "delete from SavedActivities where savedactivityid = " + savedactivityID; 
			db.execSQL(upd);
			upd = "delete from SAContactRating where savedactivityid = " + savedactivityID;
			db.execSQL(upd);
			db.close();
		}
		catch(Exception ex){

			Global.Log.v("DBPROVIDER", ex.toString());
		}
	}
	
	private static class OpenHelper extends SQLiteOpenHelper 
	{

		OpenHelper(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{

			//Attendance
			String createAttendance = "CREATE TABLE IF NOT EXISTS Attendance (AttendanceID INTEGER PRIMARY KEY, AttendanceName TEXT, AttendanceValue TEXT);";
			db.execSQL(createAttendance);
			db.execSQL("insert into Attendance (AttendanceName, AttendanceValue) values ('Solo','a')");
			db.execSQL("insert into Attendance (AttendanceName, AttendanceValue) values ('Pair','c')");
			db.execSQL("insert into Attendance (AttendanceName, AttendanceValue) values ('Group','g')");

			//Saved Activities
			String createSavedActivities = "CREATE TABLE IF NOT EXISTS SavedActivities (SavedActivityID INTEGER PRIMARY KEY, SelectedAttendance TEXT, SelectedActivityID INTEGER, AttendanceRating FLOAT, CategoryRating FLOAT, ActivityRating FLOAT, SavedDate DATETIME, LocalName TEXT, LocalAddress TEXT, LocalPhone TEXT);";
			db.execSQL(createSavedActivities);

			//Saved Activity Contact Ratings
			String createSavedActivityContacts = "CREATE TABLE IF NOT EXISTS SAContactRating (SAContactRatingID INTEGER PRIMARY KEY, ContactName TEXT, ContactRating FLOAT, SavedActivityID INTEGER);";
			db.execSQL(createSavedActivityContacts);

			//Contacts
			String createContacts = "CREATE TABLE IF NOT EXISTS Contacts (ContactID INTEGER PRIMARY KEY, ContactName TEXT);";
			db.execSQL(createContacts);

			//Activities
			String createActivities = "CREATE TABLE IF NOT EXISTS Activities (ActivityID INTEGER PRIMARY KEY, ActivityEnabled INTEGER, ActivityName TEXT, ActivityCategory TEXT, ActivityAttendance TEXT, GoogleType TEXT);";
			db.execSQL(createActivities);

			//Populate Activities
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do housework or some laundry', 'Indoor', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to the movies', 'Indoor', 'acg', 'movie_theater')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'work on my finances', 'Indoor', 'a', 'finance')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a government meeting or court session', 'Indoor', 'acg', 'courthouse')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go back to school or take a class', 'Indoor', 'a', 'university')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read essays or technical, academic, or professional literature', 'Indoor', 'a', 'book_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'visit people who are sick or in trouble', 'Indoor', 'acg', 'hospital')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'design or draft something', 'Indoor', 'a', 'google:drafting')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a lecture or listen to a speaker', 'Indoor', 'acg', 'book_store|library|university')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read aloud to someone else', 'Indoor', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read a short story, a novel, poems, or a play', 'Indoor', 'a', 'book_store|library')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read a How to Do it book or article on something that interests me', 'Indoor', 'a', 'book_store|library')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do art work like painting, sculpture, or drawing', 'Indoor', 'acg', 'art_gallery')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take a long bath', 'Indoor', 'aci', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'buy or go look at an aquarium', 'Indoor', 'a', 'pet_store|aquarium')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go bowling', 'Indoor', 'acg', 'bowling_alley')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take sauna or steam bath', 'Indoor', 'acg', 'spa')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'buy a book', 'Indoor', 'a', 'book_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'get a massage or backrub', 'Indoor', 'ac', 'spa')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'listen to talk radio', 'Indoor', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a museum', 'Indoor', 'acg', 'museum')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'call a friend', 'Indoor', 'c', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play pool', 'Indoor', 'acg', 'google:pool hall')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do crossword puzzles, word searches or Sudoku', 'Indoor', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play cards', 'Indoor', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to lunch with friends', 'Indoor', 'cg', 'restaurant|cafe')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go dancing', 'Indoor', 'acg', 'google:dance school|dancing instruction')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'write a journal entry', 'Indoor', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'write a letter, card or note', 'Indoor', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'watch TV', 'Indoor', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a play, musical, or concert', 'Indoor', 'acg', 'google:musical theaters')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'watch my children play', 'Indoor', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go have an early morning coffee and read a newspaper', 'Indoor', 'a', 'cafe')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a salon or spa', 'Indoor', 'acg', 'spa|hair_care')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go out to breakfast', 'Indoor', 'acg', 'bakery|cafe')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go out to dinner', 'Indoor', 'acg', 'restaurant')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'sew something', 'Indoor', 'acg', 'google:sewing')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'knit something', 'Indoor', 'acg', 'google:knitting')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'write a book or a short story', 'Indoor', 'a', 'book_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'cook a meal', 'Indoor', 'acg', 'google:kitchen supply')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'watch sports', 'Indoor', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'make a gift for someone', 'Indoor', 'acg', 'google:craft store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a club for people who enjoy gardening', 'Indoor', 'a', 'google:gardening_club')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a club such as Parents without Partners', 'Indoor', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do an arts and crafts project', 'Indoor', 'acg', 'google:craft store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go Skating', 'Indoor', 'acg', 'google:skating_rinks')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'collect old things or go antiquing', 'Indoor', 'acg', 'google:antique')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'collect things like coins, shells or stamps', 'Indoor', 'acg', 'google:collectables')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'develop a hobby like stamp collecting or model building', 'Indoor', 'acg', 'google:hobby_shop')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 're-arrange or redecorate a room', 'Indoor', 'a', 'google:interior_decorating')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'learn a musical instrument', 'Indoor', 'acg', 'google:musical_instrument_rental')");

			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'look at the stars or moon', 'Outdoor', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'beach combing', 'Outdoor', 'acg', 'google:clamming')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go on a field trip or nature walk', 'Outdoor', 'acg', 'campground|park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play in the sand, a stream, or the grass', 'Outdoor', 'acg', 'park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play Frisbee or catch', 'Outdoor', 'cg', 'campground|park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'walk barefoot', 'Outdoor', 'acg', 'campground|park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'gather natural objects like wild foods, fruit, rocks, or driftwood', 'Outdoor', 'acg', 'campground|park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go on an outing to the park, a picnic or barbecue', 'Outdoor', 'cg', 'campground|park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'watch the sky, clouds, or a storm', 'Outdoor', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a fair, a carnival, or the circus', 'Outdoor', 'acg', 'amusement_park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do art work like painting, sculpture, or drawing', 'Outdoor', 'acg', 'google:art_supply')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go out in the country-side', 'Outdoor', 'acg', 'campground|park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go horseback riding', 'Outdoor', 'acg', 'google:horseback_riding')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go kayaking or canoeing', 'Outdoor', 'acg', 'google:kayaking')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go whitewater rafting', 'Outdoor', 'acg', 'google:rafting')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go skiing or snowboarding', 'Outdoor', 'acg', 'google:skiing')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play baseball, football, volleyball, etc', 'Outdoor', 'g', 'google:intramural sports')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'learn to fly a plane ', 'Outdoor', 'acg', 'google:flight_school')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go fishing', 'Outdoor', 'acg', 'google:fishing')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go take pictures outdoors', 'Outdoor', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a spectator sport like auto racing or horse racing', 'Outdoor', 'acg', 'google:racing_tracks')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'walk in the woods or at the waterfront', 'Outdoor', 'acg', 'campground|park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'watch my children play', 'Outdoor', 'acg', 'park|amusement_park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'Watch my grandchildren play', 'Outdoor', 'acg', 'park|amusement_park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'garden ', 'Outdoor', 'ac', 'google:garden_nursery')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to the beach', 'Outdoor', 'acg', 'google:clamming')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go sailing', 'Outdoor', 'acg', 'google:boat_rental')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play golf', 'Outdoor', 'acg', 'google:golf_course')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play soccer', 'Outdoor', 'acg', 'google:soccer')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'fly a kite', 'Outdoor', 'acg', 'google:kite_shop')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'ride a bike or motorcycle', 'Outdoor', 'acg', 'google:motorbike rental')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go hiking', 'Outdoor', 'acg', 'google:hiking_trails')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a park', 'Outdoor', 'acg', 'campground|park|rv_park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take a walking tour', 'Outdoor', 'acg', 'google:walkingtour')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go camping', 'Outdoor', 'acg', 'campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to an outdoor movie', 'Outdoor', 'acg', 'google:drive-in movie_theater')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'watch sports', 'Outdoor', 'acg', 'park|stadium')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go bike riding', 'Outdoor', 'acg', 'google:bicycle_rental')");

			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go water ski, surf, or scuba dive', 'Water Activity', 'acg', 'google:scuba')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go beach combing', 'Water Activity', 'acg', 'google:clamming')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go kayaking or canoeing', 'Water Activity', 'acg', 'google:kayaking')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go whitewater rafting', 'Water Activity', 'acg', 'google:rafting')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'rent a Boat', 'Water Activity', 'acg', 'google:boat_rental')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go fishing', 'Water Activity', 'acg', 'google:fishing')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go swimming', 'Water Activity', 'acg', 'google:swimming_pools')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to the beach', 'Water Activity', 'acg', 'google:clamming')");

			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'just think about buying things', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think - - you know, I have a lot more going for me than most people', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think about an interesting question', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think about other peoples problems', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think about people I like', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think about sex', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think - I have done a full days work', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think about myself or my problems', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think - Im an OK person', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think - Ive done enough today', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think about past trips', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think about retirement', 'Thoughts', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think about something good in the future', 'Thoughts', 'a', 'none')");

			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'and borrow something', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read cartoons or comics', 'Free', 'acg', 'google:comic')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'talk about my job or school', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'make a new friend', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'stay up late', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'beach comb', 'Free', 'acg', 'google:clamming')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'remember a departed friend', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'express my love to someone', 'Free', 'c', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go on a field trip', 'Free', 'acg', 'park|campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'be with my significant other', 'Free', 'c', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play in the sand, a stream, or the grass', 'Free', 'acg', 'park|campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'smile at people', 'Free', 'acg', 'shopping_mall')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'ask for help or advice', 'Free', 'c', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'talk about politics or public affairs', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'work with others as a team', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'confess or apologize', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'finish a project or task', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'be with happy people', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go sell or trade something', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go build or watch a fire', 'Free', 'acg', 'campground|rv_park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go somewhere and people-watch', 'Free', 'acg', 'shopping_mall')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go bird watching', 'Free', 'acg', 'park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to the library', 'Free', 'acg', 'library')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'be with someone I love', 'Free', 'c', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'amuse other people', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'walk barefoot outside', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'talk with people on the job or in class', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do a favor for someone', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'visit friends', 'Free', 'g', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'perform an experiment', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have peace and quiet time', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'get up early in the morning', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'reminisce, and talk about old times', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'use cologne, perfume, or aftershave', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'speak in a foreign language', 'Free', 'acg', 'google:learn foreign language')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'see or smell a flower or plant', 'Free', 'a', 'park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'Coach someone', 'Free', 'c', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'be at a family reunion or get-together', 'Free', 'g', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'cry', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do a project in my own way', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'be alone', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go kick leaves, sand, or pebbles', 'Free', 'acg', 'park|campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play lawn sports', 'Free', 'acg', 'google:lawn games')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'learn to do something new', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'protest something', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'compliment or praise someone', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'loan something', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'make spare time in my schedule', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'write a paper, essay, article', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'talk about my health', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'talk about my children or grandchildren', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'help someone else', 'Free', 'acg', 'google:volunteer')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'wear clean clothes', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'work on my finances', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'gather natural objects', 'Free', 'acg', 'park|campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'watch the sky, clouds, or a storm', 'Free', 'acg', 'park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'introduce people who I think would like one another', 'Free', 'g', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have a lively talk', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'listen to the sounds of nature', 'Free', 'a', 'park|campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'wear informal clothes', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'talk about philosophy or religion', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read essays or technical, academic, or professional literature ', 'Free', 'a', 'library|book_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'cheer a team on', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'visit people who are sick or in trouble', 'Free', 'acg', 'google:volunteer')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'design or draft something', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'put on make-up or fix my hair', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play chess or checkers', 'Free', 'ac', 'google:chess')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'solve a personal problem', 'Free', 'ac', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'comb or brush my hair', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'cook something different using only ingredients that are already in my kitchen', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'say something clearly', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'think up or arrange songs, or music', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 're-arrange or redecorate a room', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read a sacred work like scriptures or text from another religion', 'Free', 'a', 'book_store|library')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'talk about sports', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to the beach', 'Free', 'acg', 'google:clamming')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go swimming', 'Free', 'acg', 'google:swimming_pools')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'listen to talk radio', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'call a friend', 'Free', 'c', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do crossword puzzles or Sudoku ', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'meditate ', 'Free', 'a', 'google:meditation')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go on a picnic', 'Free', 'cg', 'park|campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'clean a room', 'Free', 'ac', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'write a diary entry or a letter', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have alone time, to do something I like', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read something', 'Free', 'a', 'library')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play/be with animals', 'Free', 'acg', 'pet_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'stay on a diet', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go outside and take pictures', 'Free', 'acg', 'park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'complete a chore I have been meaning to do', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'walk in the woods or waterfront', 'Free', 'acg', 'park|campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go bike riding', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'make a to-do list', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'plan to go to school', 'Free', 'a', 'university')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'daydream', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'watch TV', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'watch my children play', 'Free', 'a', 'park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'kiss', 'Free', 'ci', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'garden', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'discuss a book', 'Free', 'cg', 'library')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'sew something', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'write a book, story, novel, or poetry', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'watch sports', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take a drive', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a service, civic, or social club meeting', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'sleep in late or take a nap', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do needlepoint or crewel', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do something spontaneously', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'lose weight', 'Free', 'acg', 'google:healthy_weight_loss')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have a day with nothing to do', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have/plan or go to a class reunion or alumni meeting', 'Free', 'g', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'paint something', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'practice religion and go to church or a group prayer)', 'Free', 'acg', 'place_of_worship')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a party', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have a discussion with friends', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have a frank and open conversation', 'Free', 'c', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have a family get-together', 'Free', 'g', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'sing around the house', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'sing in a group', 'Free', 'acg', 'google:chorus')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take care of plants', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play a musical instrument', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'doodle', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'collect old things or go antiquing', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'collect things like coins, shells or stamps', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'relax', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'look at the stars or moon', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'work on my job', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'listen to music', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'lay in the sun', 'Free', 'acg', 'park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'plan a career change', 'Free', 'a', 'google:career')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play with pets', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to office parties or departmental get-togethers', 'Free', 'g', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'listen to others', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read magazines or newspapers', 'Free', 'a', 'library')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'work on an existing hobby', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'spend an evening with good friends', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'meet new people', 'Free', 'acg', 'google:volunteer')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'learn how to save money', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go home from work early', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'laugh', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'soak in the bathtub', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have a house party', 'Free', 'g', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'invite friends over for a game night', 'Free', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have consensual sex', 'Free', 'ci', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'use my personal strengths', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'repair things around the house', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'work on my car or bicycle', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'remember the words of people who love me', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'have a quiet evening', 'Free', 'ac', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'participate in politics', 'Free', 'acg', 'google:political_organizations')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play cards', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'work on cars, bikes, motorcycles, or tractors', 'Free', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'see or talk to an old friend', 'Free', 'c', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'complete a difficult task', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'shave', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take a shower', 'Free', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'listen to the sounds of nature', 'Free', 'acg', 'park')");

			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play handball, paddleball or squash', 'Physical Activity', 'cg', 'gym')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play soccer', 'Physical Activity', 'cg', 'google:soccer_club')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play cricket, or hockey', 'Physical Activity', 'cg', 'google:hockey')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play rugby', 'Physical Activity', 'cg', 'google:rugby_club')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play lacrosse', 'Physical Activity', 'cg', 'google:lacrosse')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play Frisbee or catch', 'Physical Activity', 'cg', 'park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play ping pong', 'Physical Activity', 'c', 'google:ping pong')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go snowmobiling or dune-buggy riding', 'Physical Activity', 'acg', 'google:snowmobile_rental')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do heavy outdoor work like clearing land or farm work', 'Physical Activity', 'acg', 'google:volunteer')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play lawn sports', 'Physical Activity', 'acg', 'google:sporting goods')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'wrestle or box', 'Physical Activity', 'cg', 'google:boxing_club')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go rock climbing', 'Physical Activity', 'acg', 'google:rock climbing')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go swimming', 'Physical Activity', 'acg', 'google:swimming_pools')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go skiing or snowboarding', 'Physical Activity', 'acg', 'google:ski_rental')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'walk in the woods or on the waterfront', 'Physical Activity', 'acg', 'park|campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take a bike ride', 'Physical Activity', 'acg', 'google:bicycle_rentals')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play tennis', 'Physical Activity', 'acg', 'google:tennis_lessons')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go skating', 'Physical Activity', 'acg', 'google:skating_rinks')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do a home exercise video', 'Physical Activity', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'practice karate or judo', 'Physical Activity', 'acg', 'google:karate_lessons')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go for a run/jog/walk', 'Physical Activity', 'acg', 'park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take a Yoga class', 'Physical Activity', 'acg', 'google:yoga')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to the YMCA or YWCA', 'Physical Activity', 'acg', 'google:ymca ywca')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take a Pilates class', 'Physical Activity', 'acg', 'google:pilates')");

			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'buy furniture', 'Shopping', 'acg', 'home_goods_store|furniture_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'buy a household gadget', 'Shopping', 'acg', 'home_goods_store|furniture_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'buy something just for myself', 'Shopping', 'a', 'shopping_mall|store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'buy or sell stock', 'Shopping', 'a', 'google:stock')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'buy music', 'Shopping', 'a', 'google:music store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'buy clothes', 'Shopping', 'acg', 'shopping_mall|clothing_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'buy gifts', 'Shopping', 'a', 'shopping_mall|store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'buy books', 'Shopping', 'a', 'book_store')");

			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go sit in a coffee shop or café', 'Restaurants/Food', 'acg', 'cafe')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a drive-through window and order food', 'Restaurants/Food', 'acg', 'google:fast_food')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'plan a banquet, luncheon or potluck', 'Restaurants/Food', 'g', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'cook something', 'Restaurants/Food', 'acg', 'google:kitchen supply')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'eat some snacks', 'Restaurants/Food', 'a', 'grocery_or_supermarket')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'eat some gooey, fattening foods', 'Restaurants/Food', 'a', 'restaurant|food')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go out to breakfast', 'Restaurants/Food', 'acg', 'restaurant|food')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go out to lunch', 'Restaurants/Food', 'acg', 'restaurant|food')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go out to dinner', 'Restaurants/Food', 'acg', 'restaurant|food')");

			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'spend time in the city', 'Travel', 'acg', 'locality')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to the mountains', 'Travel', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'travel with a group', 'Travel', 'g', 'travel_agency')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'be in the country', 'Travel', 'acg', 'park|campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a business meeting or convention', 'Travel', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'speak in a foreign language', 'Travel', 'cg', 'google:learn foreign language')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'plan a trip abroad', 'Travel', 'acg', 'travel_agency')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'plan a trip in the United States', 'Travel', 'acg', 'travel_agency')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'sightsee in my city', 'Travel', 'acg', 'locality')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'plan or travel to a state or national park', 'Travel', 'acg', 'google:state national park')");

			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'plan a party', 'Miscellaneous', 'acg', 'google:party_supplies')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go see a movie', 'Miscellaneous', 'acg', 'movie_theater')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'attend a play, concert, opera, or ballet', 'Miscellaneous', 'acg', 'google:musical theaters')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to an auction or garage sale', 'Miscellaneous', 'acg', 'google:auction_house')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a banquet, luncheon, potluck, etc', 'Miscellaneous', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'be with happy people', 'Miscellaneous', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'work with others as a team', 'Miscellaneous', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'confess or apologize', 'Miscellaneous', 'c', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'finish a project or task', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'start a new project', 'Miscellaneous', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'brush my teeth', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'perform an experiment or other scientific work', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'see or smell a flower or plant', 'Miscellaneous', 'a', 'florist')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a drive-through window and order food', 'Miscellaneous', 'acg', 'google:fast_food')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'learn to do something new', 'Miscellaneous', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'protest something', 'Miscellaneous', 'acg', 'google:political_organizations')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'compliment or praise someone', 'Miscellaneous', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'loan something', 'Miscellaneous', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'make spare time in my schedule', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play in a musical group', 'Miscellaneous', 'cg', 'google:musical_instruments_rental')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'improve my health, have my teeth fixed, get new glasses, or change my diet', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'eat a good meal', 'Miscellaneous', 'acg', 'resturant|food')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'listen to jokes', 'Miscellaneous', 'acg', 'google:comedy_club')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'make a major purchase or investment like a car, appliances, house, or stocks', 'Miscellaneous', 'ac', 'car_dealer|real_estate_agency|department_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'work on my finances', 'Miscellaneous', 'a', 'google:financial_planning')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'gather natural objects like wild foods, fruit, rocks, or driftwood', 'Miscellaneous', 'acg', 'park|campground')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read maps', 'Miscellaneous', 'a', 'book_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'give a speech or lecture', 'Miscellaneous', 'acg', 'google:toastmasters_club speech')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a government meeting or court session', 'Miscellaneous', 'acg', 'courthouse')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'give gifts', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'introduce people who I think would like one another', 'Miscellaneous', 'cg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play in a sporting competition', 'Miscellaneous', 'cg', 'gym')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take someone on a date', 'Miscellaneous', 'ci', 'movie_theater|restaurant')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'listen to the sounds of nature', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'plan or organize something', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'go to a fair, carnival, circus, zoo, or amusement park', 'Miscellaneous', 'acg', 'zoo|amusement_park')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'make food or crafts to sell or give away', 'Miscellaneous', 'acg', 'google:craft_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'play chess or checkers', 'Miscellaneous', 'ac', 'google:chess')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do craft work like pottery, jewelry, leather, beads, or weaving', 'Miscellaneous', 'acg', 'google:art_supply_stores')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'prepare a new or special food', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'read essays or technical, academic, or professional literature', 'Miscellaneous', 'a', 'library|book_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'cheer a team on', 'Miscellaneous', 'acg', 'stadium')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'design or draft something', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'put on make-up or fix my hair', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'solve a personal problem', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'can, freeze, or make preserves, etc', 'Miscellaneous', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'comb or brush my hair', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'wear informal clothes', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'make snacks', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'ride in a sporty or expensive car', 'Miscellaneous', 'acg', 'car_rental')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'shave', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'speak in a foreign language', 'Miscellaneous', 'cg', 'google:learn foreign language')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take a shower', 'Miscellaneous', 'a', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'work on cars, bikes, motorcycles, or tractors', 'Miscellaneous', 'acg', 'none')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'participate in politics', 'Miscellaneous', 'acg', 'google:political_organizations')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'restore antiques or refinish furniture', 'Miscellaneous', 'acg', 'google:furniture restoration')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do art work like painting, sculpture, drawing, or movie-making', 'Miscellaneous', 'acg', 'google:art_supply_store')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'make a contribution to a religious, charitable, or other group', 'Miscellaneous', 'a', 'google:charities')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'teach something', 'Miscellaneous', 'acg', 'library|church|place_of_worship')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'volunteer my time', 'Miscellaneous', 'acg', 'google:volunteer')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'take acting lessons', 'Miscellaneous', 'acg', 'google:acting_lessons')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'dance; either watch or take lessons', 'Miscellaneous', 'acg', 'google:dance lessons')");
			db.execSQL("insert into Activities (ActivityEnabled, Activityname, ActivityCategory, ActivityAttendance, GoogleType) values(1, 'do woodworking', 'Miscellaneous', 'acg', 'google:wood supplies')");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			try
			{
				db.execSQL("drop table activities");
				db.execSQL("drop table SAContactRating");
			}
			catch(Exception ex)
			{}
			onCreate(db);
		}
	}
}