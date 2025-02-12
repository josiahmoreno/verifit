package com.example.verifit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.verifit.workoutservice.PrefWorkoutServiceImpl;
import com.example.verifit.workoutservice.WorkoutService;
import com.example.verifit.singleton.DateSelectStore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DayActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public DayExerciseAdapter workoutExerciseAdapter;
    String date_clicked;
    public List<WorkoutDay> workoutDayList;
    private KnownExerciseService knownExerciseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        knownExerciseService =  KnownExerciseServiceSingleton.INSTANCE.getKnownExerciseService(getApplicationContext());
        WorkoutService workoutService = new PrefWorkoutServiceImpl(this, DateSelectStore.INSTANCE,knownExerciseService);

        workoutDayList = workoutService.fetchWorkoutDays();
        // Self Explanatory I guess
        initActivity();
    }


    // Haven't we said that already?
    public void initActivity()
    {

        // Recycler View Stuff
        recyclerView = findViewById(R.id.recycler_view_day);

        // From Main Activity
        Intent mIntent = getIntent();
        Bundle extras = mIntent.getExtras();
        date_clicked = extras.getString("date");

        //Date Stuff
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        try {
            date = parser.parse(date_clicked);
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd YYYY");
            String formated_date = formatter.format(date);
            getSupportActionBar().setTitle(formated_date);

            ArrayList<WorkoutExercise> Today_Execrises = new ArrayList<WorkoutExercise>();

            for(int i = 0; i < workoutDayList.size(); i++)
            {
                if(date_clicked.equals(workoutDayList.get(i).getDate()))
                {
                    Today_Execrises = workoutDayList.get(i).getExercises();
                }
            }


            // Set Recycler View
            workoutExerciseAdapter = new DayExerciseAdapter(this, Today_Execrises, knownExerciseService);
            recyclerView.setAdapter(workoutExerciseAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Notify User
            if(Today_Execrises.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"No Logged Exercises",Toast.LENGTH_SHORT).show();
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    // When back button is pressed by another app
    @Override
    protected void onRestart() {
        super.onRestart();

        // Self Explanatory I guess
        initActivity();
    }

    // Menu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.day_activity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add)
        {
            Intent in = new Intent(this,ExercisesActivity.class);
            startActivity(in);
        }
        else if(item.getItemId() == R.id.diary)
        {
            Intent in = new Intent(this,DiaryActivity.class);
            in.putExtra("date",date_clicked);
            startActivity(in);
        }
        else if(item.getItemId() == R.id.calendar)
        {
            Intent in = new Intent(this,MainActivity.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}