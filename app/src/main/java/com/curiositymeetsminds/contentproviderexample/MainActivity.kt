package com.curiositymeetsminds.contentproviderexample

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Log.d(TAG, "fab onClick: starts")

            //creating an array to store strings of column names we need to access, only 1 in this case
            val projection = arrayOf (ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            //making a query to the ContentResolver to access the contacts database, returns a cursor with the requested data
            val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                                                projection,
                                                null,
                                                null,
                                                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

            //getting all contacts into an array list
            val contacts = ArrayList<String>()
            cursor?.use {
                while (it.moveToNext()) {
                    contacts.add(it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)))
                }
            }

            //using ArrayAdapter to display contacts in the ListView
            //id is optional,just the layout name is enough
            val adapter = ArrayAdapter<String>(this, R.layout.contact_detail, R.id.name, contacts)
            contactNames.adapter = adapter

            Log.d(TAG, "fab onClick: ends")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
