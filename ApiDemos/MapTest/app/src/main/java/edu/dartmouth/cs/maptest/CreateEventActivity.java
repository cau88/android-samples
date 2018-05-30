package edu.dartmouth.cs.maptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateEventActivity extends AppCompatActivity {

    private static final String TAG = "CreateEventActivity";

    private String mTitle;
    private String mAddress;
    private String mFoodType;
    private String mLocation;
    private String mSelected; //selected food amount

    private RadioGroup foodAmount = (RadioGroup) findViewById(R.id.food_amt);
    private Button selectedAmt = (Button) findViewById(R.id.large_amt);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //enable back-arrow
        }
    }

    // go back if back-arrow is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();    // go back if back-arrow pressed

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onSubmitClicked(View v) {
        mTitle = (String) ((EditText) findViewById(R.id.event_title)).getText().toString();
        mFoodType = (String) ((EditText) findViewById(R.id.event_food)).getText().toString();
        mLocation = (String) ((EditText) findViewById(R.id.event_location)).getText().toString();
        mAddress = (String) ((EditText) findViewById(R.id.event_address)).getText().toString();

        selectedAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amt = foodAmount.getCheckedRadioButtonId();
                RadioButton selected = (RadioButton) findViewById(amt);
                mSelected = (String) selected.getText();
            }
        });

        EventEntry event = new EventEntry(mTitle, mFoodType, mLocation, mAddress, mSelected);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.push().setValue(event.getEvent())
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Insert is done!
                            Log.d(TAG, "Success!");
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.event_added_confirmation), Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed
                            if (task.getException() != null)
                                Log.w(TAG, task.getException().getMessage());
                        }
                    }
                });
        Intent intent = new Intent(CreateEventActivity.this,
                MapsActivity.class);
        startActivity(intent);
    }
}

