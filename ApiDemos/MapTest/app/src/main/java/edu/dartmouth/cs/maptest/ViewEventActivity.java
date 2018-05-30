package edu.dartmouth.cs.maptest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ViewEventActivity extends AppCompatActivity {

    private static final String TAG = "ViewEventActivity";
    private RadioGroup mRadio_Amount;
    private int like;
    private int dislike;
    private int totalLikes;
    private ImageButton upVote;
    private ImageButton downVote;

    private String mTitle;
    private String mAmount;
    private String mSupported;
    private String mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        View view = findViewById(R.id.eventview);

        mRadio_Amount = findViewById(R.id.food_rating_amt);
        like = 0;
        dislike = 0;
        upVote = findViewById(R.id.thumbs_up_image);
        downVote = findViewById(R.id.thumbs_down_image);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //enable back-arrow
        }
        ArrayList<String> data = getIntent().getStringArrayListExtra("MarkerData");
        TextView title = view.findViewById(R.id.title);
        title.setText(data.get(0));
        TextView address = view.findViewById(R.id.address);
        address.setText(data.get(1));
        TextView food = view.findViewById(R.id.food);
        food.setText(data.get(2));

        /*
        ArrayList<String> data = getIntent().getStringArrayListExtra("MarkerData");
        TextView title = view.findViewById(R.id.title);
        title.setText(data.get(0));
        TextView food = view.findViewById(R.id.food);
        food.setText(data.get(1));
        TextView location = view.findViewById(R.id.event_location);
        event_location.setText(data.get(2));
        TextView address = view.findViewById(R.id.address);
        address.setText(data.get(3));
        */
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


    public void userReview(View v) {

            upVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    upVote.setBackgroundColor(Color.parseColor("#0B6623"));
                    like++;
                }
            });

            downVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downVote.setBackgroundColor(Color.parseColor("#0B6623"));
                    dislike++;
                }
            });

        totalLikes = like - dislike;

        ReviewedEventEntry event = new ReviewedEventEntry(mTitle, mAmount, totalLikes, mCategory);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.push().setValue(event.getEvent())
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Insert is done!
                            Log.d(TAG, "Success!");
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.event_reviewed_confirmation), Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed
                            if (task.getException() != null)
                                Log.w(TAG, task.getException().getMessage());
                        }
                    }
                });
        Intent intent = new Intent(ViewEventActivity.this,
                MapsActivity.class);
        startActivity(intent);

    }

}
