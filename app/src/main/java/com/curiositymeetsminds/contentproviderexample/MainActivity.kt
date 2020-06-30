package com.curiositymeetsminds.contentproviderexample

import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "MainActivity"
private const val READ_CONTACTS_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {

//    private var readGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        we are using synthetic import to directly write READ_CONTACTS instead of android.Manifest.permission.READ_CONTACTS
//        however it is not always a good practice as in case there are many constants then the readability of the code decreases
//        this is because the programmer is unable to track to which script/file the given constant belongs
        val hasReadContactPermission = ContextCompat.checkSelfPermission(this, READ_CONTACTS)

        Log.d(TAG, "onCreate: checkSelfPermission returned $hasReadContactPermission")

        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onCreate: Permission granted")
        } else {
            Log.d(TAG, "onCreate: Requesting permission")
            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), READ_CONTACTS_REQUEST_CODE)
        }

        fab.setOnClickListener { view ->
            Log.d(TAG, "fab onClick: starts")
//            if (readGranted) {
            if (ContextCompat.checkSelfPermission(this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //creating an array to store strings of column names we need to access, only 1 in this case
                val projection = arrayOf (ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
                //making a query to the ContentResolver to access the contacts database, returns a cursor with the requested data
                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
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
            } else {
                Snackbar.make(view, "Please grant permission to access your contacts.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant Access") {
                        Log.d(TAG, "Snackbar onClick: starts")
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_CONTACTS)) {
                            Log.d(TAG, "Snackbar onClick: calling requestPermissions")
                            ActivityCompat.requestPermissions(this, arrayOf(READ_CONTACTS), READ_CONTACTS_REQUEST_CODE)
                        } else {
//                            user has permanently denied permission to access contacts
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", this.packageName, null)
                            Log.d(TAG, "Uri create: $uri")
                            intent.data = uri
                            Log.d(TAG, "Snackbar onClick: launching settings")
                            this.startActivity(intent)
                        }
                        Log.d(TAG, "Snackbar onClick: ends")
                    }.show()
            }

            Log.d(TAG, "fab onClick: ends")
        }

        Log.d(TAG, "onCreate: ends")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: starts")
        when (requestCode) {
            READ_CONTACTS_REQUEST_CODE -> {
//                readGranted = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Permission granted")
//                    true
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Permission refused")
//                    false
                }
//                disable the floating action button if permission is not granted so
//                so the app does not crash when the user tries to click the fab
//                fab.isEnabled = readGranted
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: ends")
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
