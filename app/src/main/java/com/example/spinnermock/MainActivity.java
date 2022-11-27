package com.example.spinnermock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    TextView textView;
    ArrayList<String> courseList;
    ArrayList <String> futureList;
    Dialog dialog;
    String addCourse;

    Button addButton, home;
    ListView courseView;


    FirebaseFirestore courseStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //arraylist that stores the course codes
        textView = findViewById(R.id.text_view);
        courseList = new ArrayList<>();

        //setting up database for courses
        courseStore = courseStore.getInstance();





        //we have to add spaces so that the indexing doesn't cut off the last letter
        courseList.add("CSCB36 ");
        courseList.add("CSCC01 ");
        courseList.add("CSCC09 ");
        courseList.add("CSCB64 ");
        courseList.add("CSCA48 ");
        courseList.add("CSCA37 ");
        courseList.add("CSCB07 ");
        courseList.add(("CSCD01"+" "));
        courseList.add("CSCD58 ");

        //arraylist that stores future student courses
        futureList = new ArrayList<>();


        //initialize buttons and search, lists
        courseView = findViewById(R.id.courseView); //this is the layout that displays selected courses
        addButton = (Button) findViewById(R.id.addFutureCourse);
        home = (Button) findViewById(R.id.home_button);


        ArrayAdapter<String> viewAdapter = new ArrayAdapter<>( this, android.R.layout.simple_list_item_1, futureList);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, Home.class);
                MainActivity.this.startActivity(myIntent);


            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(addCourse.isEmpty()) && futureList.contains(addCourse) == false) {
                    futureList.add(addCourse);
                    //viewAdapter.notifyDataSetChanged();
                    //Log.i("RM", futureList.get(futureList.size()));
                    courseView.setAdapter(viewAdapter);


                }


            }
        });




        //
        courseView.setOnItemClickListener(new AdapterView.OnItemClickListener(){


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int to_remove = i;

                new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Are you sure you want to delete course from list?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            futureList.remove(to_remove);
                            viewAdapter.notifyDataSetChanged();
                        }
                    })

                    .setNegativeButton("No", null)
                    .show();

                //return true;
            }
        });





        //spinner filter + functionality

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Initialize dialog
                dialog = new Dialog(MainActivity.this);

                //set custom dialog
                dialog.setContentView((R.layout.dialog_searchable_spinner));

                //set custom height and width
                dialog.getWindow().setLayout(650, 800);

                //set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();


                //Initialize and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                //Initialize array adaptor

                ArrayAdapter <String> course_adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, courseList);

                listView.setAdapter(course_adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        //Filter array list
                        course_adapter.getFilter().filter(charSequence);


                    }

                    @Override
                    public void afterTextChanged(Editable editable) {



                    }
                });


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //when item selected from list
                        //set selected item on text view
                        addCourse = course_adapter.getItem(i);


                        textView.setText(course_adapter.getItem(i));

                        dialog.dismiss();
                    }
                });
            }
        });








        //below is for when we need to reinitialize the arraylist everytime a new course is added

//
//        1. create function that retrieves from database, and saves all the course codes into an array list which re-initializes every time the data is changed
//        2. button that takes the string in spinner and adds it to an array list
//        3. display array list somewhere on screen
//
//
//
//        DatabaseHandler.initialise();
//
//        CourseEventListener render = new CourseEventListener() {
//            @Override
//            public void onCourseAdded(Course course) {
//                // Do stuff
//            }
//
//            @Override
//            public void onCourseChanged(Course course) {
//                // Do stuff
//            }
//
//            @Override
//            public void onCourseRemoved(Course course) {
//                // Do stuff
//            }
//        };
//
//        Course.addListener(render);

    }

    protected class courseListAdapter extends ArrayAdapter<String>{
        private int layout;
        public courseListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if (convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);

                ViewHolder viewHolder = new ViewHolder();

                viewHolder.course = (TextView) convertView.findViewById(R.id.courseView);
                viewHolder.delete = (Button) convertView.findViewById(R.id.btn);

                final int positionToRemove = position;


                viewHolder.delete.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        futureList.remove(viewHolder.course);



//                        newAdapter.notifyDataSetChanged();


                    }
                });

                convertView.setTag(viewHolder);
                //store the references
            }
            else{
                mainViewholder = (ViewHolder) convertView.getTag();
                mainViewholder.course.setText(addCourse);
//                getItem(position)
            }



            return convertView;
        }
    }

    public class ViewHolder{
        TextView course;
        Button delete;


    }
}