package com.example.simpletodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {

    //Display elements
    EditText editItem;
    Button editBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //Get display elements
        editItem = findViewById(R.id.editItem);
        editBtn = findViewById(R.id.editBtn);

        //Assign window title
        getSupportActionBar().setTitle("Edit Item");

        //Assign value of edited item to text input
        editItem.setText(getIntent().getStringExtra("item_text"));

        //Wait for the edit button to be clicked
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create an intent that contains the result
                Intent intent = new Intent();

                //Pass the results of editing
                intent.putExtra("item_text",editItem.getText().toString());
                intent.putExtra("item_position",getIntent().getExtras().getInt("item_position"));

                //Set the result of the intent
                setResult(RESULT_OK,intent);

                //Finish activity
                finish();
            }
        });
    }
}