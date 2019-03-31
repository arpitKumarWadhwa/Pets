/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetsContract;
import com.example.android.pets.data.PetsDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private PetsDbHelper mDbHelper;

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mBreedEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = PetsContract.PetsEntry.GENDER_UNKNOWN;

    //Uri received from CatalogActivity
    Uri clickedPetUri;

    private boolean mPetHasChanged;

    String name;
    String breed;
    int weight;
    long gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        String editorUri = intent.getStringExtra("editorUri");

        ActionBar ab = getSupportActionBar();

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();

        if (editorUri != null) {
            ab.setTitle("Edit Pet");
            clickedPetUri = Uri.parse(editorUri);

            invalidateOptionsMenu();

            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsContract.PetsEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsContract.PetsEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetsContract.PetsEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetsContract.PetsEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    public void savePet() {

        if (clickedPetUri == null) {
            name = mNameEditText.getText().toString().trim();
            breed = mBreedEditText.getText().toString().trim();
            weight = Integer.parseInt(mWeightEditText.getText().toString().trim());
            gender = mGender;

            if (isValid()) {

                ContentValues cv = new ContentValues();
                cv.put(PetsContract.PetsEntry.COLUMN_PET_NAME, name);
                cv.put(PetsContract.PetsEntry.COLUMN_PET_BREED, breed);
                cv.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT, weight);
                cv.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, gender);


                mDbHelper = new PetsDbHelper(this);

                Uri uri = getContentResolver().insert(PetsContract.PetsEntry.CONTENT_URI, cv);

                if (uri != null)
                    Toast.makeText(this, R.string.pet_saved, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, R.string.pet_not_saved, Toast.LENGTH_SHORT).show();
            }
        } else {
            name = mNameEditText.getText().toString().trim();
            breed = mBreedEditText.getText().toString().trim();
            weight = Integer.parseInt(mWeightEditText.getText().toString().trim());
            gender = mGender;

            if (isValid()) {

                ContentValues cv = new ContentValues();
                cv.put(PetsContract.PetsEntry.COLUMN_PET_NAME, name);
                cv.put(PetsContract.PetsEntry.COLUMN_PET_BREED, breed);
                cv.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT, weight);
                cv.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, gender);

                mDbHelper = new PetsDbHelper(this);

                int rowsUpdated = getContentResolver().update(clickedPetUri, cv, null, null);

                if (rowsUpdated != 0)
                    Toast.makeText(this, R.string.pet_updated, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, R.string.pet_not_updated, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (clickedPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                savePet();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isValid() {
        name = mNameEditText.getText().toString().trim();
        breed = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();
        gender = mGenderSpinner.getSelectedItemId();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(breed) && TextUtils.isEmpty(weightString) && gender == 0)
            return false;
        else if (TextUtils.isEmpty(weightString)) {
            weight = 0;
            return true;
        }

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader editPetsLoader = new CursorLoader(this);

        editPetsLoader.setUri(clickedPetUri);
        editPetsLoader.setSelection(null);
        editPetsLoader.setSelectionArgs(null);
        editPetsLoader.setProjection(null);
        editPetsLoader.setSortOrder(null);

        return editPetsLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data == null || data.getCount()<1)
            return;

        data.moveToFirst();

        mNameEditText.setText(data.getString(data.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME)));
        mBreedEditText.setText(data.getString(data.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED)));
        mWeightEditText.setText(Integer.toString(data.getInt(data.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_WEIGHT))));
        mGenderSpinner.setSelection(data.getInt(data.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_GENDER)));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        //Clear the fields
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);
    }


    //Confirm discard changes
    @Override
    public void onBackPressed() {
            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that changes should be discarded.

            AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("Discard your changes and quit editing?");
        ab.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        ab.setNegativeButton("Keep Editing", null);
        ab.setCancelable(false);

        ab.show();

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        int rowsDeleted = getContentResolver().delete(clickedPetUri, null, null);

        if(rowsDeleted == 0)
            Toast.makeText(this, R.string.editor_delete_pet_failed, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, R.string.editor_delete_pet_successful, Toast.LENGTH_SHORT).show();
    }

}