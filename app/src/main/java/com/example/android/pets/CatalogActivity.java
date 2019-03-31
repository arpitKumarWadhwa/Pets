package com.example.android.pets;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetsContract.PetsEntry;
import com.example.android.pets.data.PetsContract;
import com.example.android.pets.data.PetsDbHelper;
import com.example.android.pets.data.PetsProvider;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private PetsDbHelper mDbHelper;
    PetCursorAdapter petsAdapter;
    public static final int PET_LOADER = 0;
    ListView displayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetsDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
        Cursor cursor = getContentResolver().query(
                PetsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        petsAdapter = new PetCursorAdapter(this, cursor);
        displayView = (ListView) findViewById(R.id.pet_list);
        displayView.setAdapter(petsAdapter);

        View emptyView = findViewById(R.id.empty_view);
        displayView.setEmptyView(emptyView);

        getSupportLoaderManager().initLoader(PET_LOADER, null, this);

        //Starting Editor Activity
        final Intent editorIntent = new Intent(this, EditorActivity.class);
        displayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri uri = PetsEntry.CONTENT_URI;
                String clickedPetUri = uri.toString();
                clickedPetUri += "/" + id;
                editorIntent.putExtra("editorUri", clickedPetUri);
                startActivity(editorIntent);
            }
        });
    }

    public void insertPet() {
        ContentValues cv = new ContentValues();
        cv.put(PetsEntry.COLUMN_PET_NAME, "Toto");
        cv.put(PetsEntry.COLUMN_PET_BREED, "Terrier");
        cv.put(PetsEntry.COLUMN_PET_GENDER, PetsEntry.GENDER_MALE);
        cv.put(PetsEntry.COLUMN_PET_WEIGHT, 7);

        Uri uri = getContentResolver().insert(PetsEntry.CONTENT_URI, cv);

        if (uri != null)
            Toast.makeText(this, R.string.pet_saved, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, R.string.pet_not_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                getContentResolver().delete(PetsEntry.CONTENT_URI, null, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri petProviderUri = PetsEntry.CONTENT_URI;

        String projection[] = {
                PetsEntry._ID,
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED
        };

        CursorLoader petsLoader = new CursorLoader(this);
        petsLoader.setUri(petProviderUri);
        petsLoader.setProjection(projection);
        petsLoader.setSelection(null);
        petsLoader.setSelectionArgs(null);
        petsLoader.setSortOrder(null);

        return petsLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

       // if(data == null || data.getCount() <1)
            //return;

        petsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        petsAdapter.swapCursor(null);
    }
}