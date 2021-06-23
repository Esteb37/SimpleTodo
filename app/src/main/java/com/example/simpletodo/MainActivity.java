package com.example.simpletodo;

import android.content.Context;
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

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd2 = findViewById(R.id.btnAdd2);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);
        mockView = findViewById(R.id.mockView);

        loadItems();

        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {

            @Override
            public void onItemLongClicked(int position) {
                //Delete the item from the model
                items.remove(position);

                //Notify the adapter;
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
            }
        };

        itemsAdapter = new ItemsAdapter(items, onLongClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show input
                etItem.setVisibility(View.GONE);
                //Show add button
                btnAdd2.setVisibility(View.GONE);
                //Hide floating button
                btnAdd.setVisibility(View.VISIBLE);
                //Focus on text input
                etItem.requestFocus();
            }
        });

        //When plus button is clicked
        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get item list
                String todoItem = etItem.getText().toString();
                //Add item to the model
                items.add(todoItem);
                //Notify adapter that an item is inserted
                itemsAdapter.notifyItemInserted(items.size()-1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(),"Item was added", Toast.LENGTH_LONG).show();
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
        mockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide input
                etItem.setVisibility(View.GONE);
                //Hide add button
                btnAdd2.setVisibility(View.GONE);
                //Show floating button
                btnAdd.setVisibility(View.VISIBLE);
            }
        });


    }



    private File getDataFile(){
        return new File(getFilesDir(),"data.txt");
    }
    private void loadItems(){

        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(),Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity","Error reading items",e);
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
}