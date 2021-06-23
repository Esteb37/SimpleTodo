package com.example.simpletodo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.FileObserver;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.MockView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION ="item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List <String> items;
    FloatingActionButton btnAdd;
    Button btnAdd2;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    //Invisible item to detect clicking outside text input
    MockView mockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Display elements
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd2 = findViewById(R.id.btnAdd2);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);
        //mockView = findViewById(R.id.mockView);

        //Get saved item list
        loadItems();

        //Create a LongClick listener
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {

            //When an item is long clicked
            @Override
            public void onItemLongClicked(int position) {
                //Delete the item from the model
                items.remove(position);

                //Notify the adapter;
                itemsAdapter.notifyItemRemoved(position);

                //Send alert that the item was removed
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
            }
        };

        //Create a Click listener
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {

            //When an item is clicked
            @Override
            public void onItemClicked(int position) {

                //Create an intent to transfer information between activities
                Intent i = new Intent(MainActivity.this, EditActivity.class);

                //Pass the data being edited
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);

                //Launch Edit activity
                startActivityForResult(i,EDIT_TEXT_CODE);

            }
        };

        //Adapter for the items list with click and longclick listeners
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);

        //Assign the adapter to the recyclerview
        rvItems.setAdapter(itemsAdapter);

        //Assign a layout to the recyclerview
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //When the floating button is clicked, show the element creator
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show input
                etItem.setVisibility(View.VISIBLE);

                //Show add button
                btnAdd2.setVisibility(View.VISIBLE);

                //Hide floating button
                btnAdd.setVisibility(View.GONE);

                //Focus on text input
                etItem.requestFocus();
            }
        });

        //When the add button is clicked, add element to list
        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get item list
                String todoItem = etItem.getText().toString();

                //Add item to the model
                items.add(todoItem);

                //Notify adapter that an item is inserted
                itemsAdapter.notifyItemInserted(items.size()-1);

                //Erase the value from input
                etItem.setText("");

                //Send alert that item was added
                Toast.makeText(getApplicationContext(),"Item was added", Toast.LENGTH_LONG).show();

                //Persist information
                saveItems();

                //Hide input
                etItem.setVisibility(View.GONE);

                //Hide add button
                btnAdd2.setVisibility(View.GONE);

                //Show floating button
                btnAdd.setVisibility(View.VISIBLE);
            }
        });

        //If the user clicks outside the text imput, hide input
        /*mockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity","test");
                //Hide input
                etItem.setVisibility(View.GONE);
                //Hide add button
                btnAdd2.setVisibility(View.GONE);
                //Show floating button
                btnAdd.setVisibility(View.VISIBLE);
            }
        });*/


    }


    //Get the savefile
    private File getDataFile(){
        return new File(getFilesDir(),"data.txt");
    }

    //Obtain list of elements from savefile
    private void loadItems(){
        //If the file exists
        try {
            //Parse the savefile into an array
            items = new ArrayList<>(FileUtils.readLines(getDataFile(),Charset.defaultCharset()));

        //If the file does not exist
        } catch (IOException e) {
            Log.e("MainActivity","Error reading items",e);

            //Create an empty list
            items = new ArrayList<>();
        }
    }

    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity","Error saving items",e);
        }

    }

    //Handle result of edit
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //IF the result is correct
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {

            //Retrieve updated value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);

            //Extract original position
            int itemPosition = data.getExtras().getInt(KEY_ITEM_POSITION);

            //Update the model
            items.set(itemPosition,itemText);

            //Notify the adapter
            itemsAdapter.notifyItemChanged(itemPosition);

            //Persist changes
            saveItems();

            //Send alert that the element was updated
            Toast.makeText(getApplicationContext(),"Item updated successfully!",Toast.LENGTH_SHORT);
        }else{
            //Send alert that the element was not updated
            Toast.makeText(getApplicationContext(),"Error while updating item",Toast.LENGTH_SHORT);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}