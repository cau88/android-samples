package edu.dartmouth.cs.maptest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks<List<Comment>>  {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;

    private ImageButton profilePic;

    private ArrayAdapter<Comment> mAdapter;
    private static final int ALL_PINS_LOADER_ID = 1;

    private String mEmailValue;

    private Map<Marker, ArrayList<String>> markers = new HashMap<>();

    private PinsDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        checkPermissions();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SharedPreferences mPrefs = getSharedPreferences(Constants.sharePrefName, MODE_PRIVATE);
        String mKey = "email_key";
        mEmailValue = mPrefs.getString(mKey, "");
        profilePic = findViewById(R.id.imageButton);

        // start loader in the background thread.
        android.support.v4.app.LoaderManager mLoader = getSupportLoaderManager();
        mLoader.initLoader(ALL_PINS_LOADER_ID, null, this).forceLoad();

        Log.d(TAG, mEmailValue);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        // Add a marker at Collis and move the camera
        LatLng collis = new LatLng(43.702778, -72.289849);

        mMap.addMarker(new MarkerOptions().position(collis).title("Collis Student Center"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(collis));

//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(collis,17));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(collis,15));
        // Zoom in, animating the camera.
        /*mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);*/



    }

    public void displayMarker(ArrayList<String> data) {
        Geocoder geocoder = new Geocoder(this);
        Log.d(TAG, "YOUR ADDRESS:");
        try {
            List<Address> address = geocoder.getFromLocationName(data.get(1), 1);
            if (address.size() > 0) {
                Log.d(TAG, String.valueOf(address.get(0).getLatitude()));
                Log.d(TAG, "address received");
                LatLng pos = new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
                Marker newMarker = mMap.addMarker(new MarkerOptions().position(pos).title(data.get(0)));
                markers.put(newMarker, data);
            }
        } catch(IOException e) {
            Log.d(TAG, "IO Exception");
        }
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        ArrayList<String> data = markers.get(marker);
        Intent intent = new Intent(MapsActivity.this,
                ViewEventActivity.class);
        intent.putExtra("MarkerData",data);
        startActivity(intent);
        return true;
    }


    // if 'Register' clicked, go to ProfileActivity
    public void onSigninClicked(View v) {
        Intent intent = new Intent(MapsActivity.this,
                SignInActivity.class);
        startActivity(intent);
    }

    public void onCreateClicked(View v) {
        Log.d(TAG, mEmailValue);

        if (mEmailValue.equals("")) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_email_create_text),
                    Toast.LENGTH_SHORT).show();
        } else {
            profilePic.setBackgroundColor(Color.parseColor("#0C9354"));
            Intent intent = new Intent(MapsActivity.this,
                    CreateEventActivity.class);
            startActivity(intent);
        }

                /*Toast.makeText(getApplicationContext(),
                getString(R.string.no_email_create_text),
                Toast.LENGTH_SHORT).show();*/
        //Toast.makeText(getApplicationContext(), mEmailValue, Toast.LENGTH_SHORT).show();
    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "check permission por favor");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                            }
                        }
                    });
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                } else {
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }

    // this method is for creating different loaders.
    @NonNull
    @Override
    public Loader<List<Comment>> onCreateLoader(int id, @Nullable Bundle args) {
        if(id == ALL_PINS_LOADER_ID){
            // create an instance of the loader in our case AsyncTaskLoader
            // which loads a List of Comments List<Comment>
            return new PinsListLoader(MapsActivity.this);
        }
        return null;
    }

    // this method will be called when loader finishes its task.
    @Override
    public void onLoadFinished(@NonNull Loader<List<Comment>> loader, List<Comment> data) {
        if(loader.getId() == ALL_PINS_LOADER_ID){
            // returns the List<Comment> from queried from the db
            // Use the UI with the adapter to show the elements in a ListView
            if(data.size() > 0){
                mAdapter.addAll(data);
                // force notification -- tell the adapter to display
                mAdapter.notifyDataSetChanged();
            }

        }

    }
    // this method will be called after invoking loader.restart()
    @Override
    public void onLoaderReset(@NonNull Loader<List<Comment>> loader) {
        if(loader.getId() == ALL_PINS_LOADER_ID){
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

}
