package com.example.spinnermock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;

public class Home extends AppCompatActivity {

    EditText searchTextName;
    Button searchButton;
    Button gotoAddCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        searchTextName = (EditText) findViewById(R.id.searchTextName);

        searchButton = (Button) findViewById(R.id.searchButton);
        gotoAddCourses = (Button)findViewById(R.id.gen_addButton);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addCourse()
                //the method is defined below

                //goes to info page

            }
        });


        gotoAddCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addCourse()
                //the method is defined below

                Intent myIntent = new Intent(Home.this, MainActivity.class);
                startActivity(myIntent);

            }
        });
    }
}

