package com.d4coders.goodcitizen.ui.observation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Problem;
import com.d4coders.goodcitizen.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ObservationDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String OBSERVATION_ID = "observationId";
    private static final String TAG = "ObservationDetailsActivity";

    private static final int PERMISSION_ID = 44;

    private GoogleMap googleMap;

    private TextView textViewLatitude;
    private TextView textViewLongitude;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private ImageView imageViewPicture;

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.i(TAG, "The location is => " + mLastLocation);
        }
    };

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_details);

        textViewLatitude = findViewById(R.id.text_latitude);
        textViewLongitude = findViewById(R.id.text_longitude);
        textViewTitle = findViewById(R.id.observation_title);
        textViewDescription = findViewById(R.id.observation_description);
        imageViewPicture = findViewById(R.id.observation_image);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void fetchSingleObservation(String observationId) {
        Amplify.API.query(ModelQuery.get(Problem.class, observationId),
                success -> {
            Problem problem = success.getData();
                    runOnUiThread(() -> {
                        textViewLatitude.setText(String.format(problem.getLatitude().toString(), Locale.ENGLISH));
                        textViewLongitude.setText(String.format(problem.getLongitude().toString(), Locale.ENGLISH));
                        textViewTitle.setText(problem.getTitle());
                        textViewDescription.setText(problem.getDescription());
                        Picasso.get().load(success.getData().getImageUrl()).into(imageViewPicture);

                        // place marker on map
                        latitude = problem.getLatitude();
                        longitude = problem.getLongitude();

                        LatLng latLng = new LatLng(latitude, longitude);

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        googleMap.animateCamera(cameraUpdate);
                        googleMap.addMarker(
                                new MarkerOptions()
                                        .position(latLng)
                                        .title(problem.getTitle())
                                        .snippet("This is the issue in Jordan")
                        );
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        googleMap.setTrafficEnabled(true);
                        googleMap.setBuildingsEnabled(true); // dosent seem to work in jordan but should in other countries
                        LatLng[] points = {
                                new LatLng(31.898733560570754, 35.868776784656454),
                                new LatLng(31.898842863744175, 35.874141202600086),
                                new LatLng(31.893304672997747, 35.87474201740977),
                                new LatLng(31.891555701443288, 35.860064969916),
                        };
                        googleMap.addPolygon(new PolygonOptions().add(points));
                    });
                },
                error -> {

                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_observation_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.share) {
            // THIS IS HOW WE SEND AN IMPLICIT INTENT
            // Create the text message with a string.
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "I want to share an observation");
            sendIntent.setType("text/plain");

//            Intent sendIntent = new Intent(Intent.ACTION_WEB_SEARCH);
//            sendIntent.putExtra(SearchManager.QUERY, "coffee");
//            if (sendIntent.resolveActivity(getPackageManager()) != null) {
//                startActivity(sendIntent);
//            }

            // Try to invoke the intent.
            try {
                startActivity(sendIntent);
            } catch (ActivityNotFoundException e) {
                // Define what your app should do if no activity can handle the intent.
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        Intent intent = getIntent();
        String observationId = intent.getStringExtra(OBSERVATION_ID);
        fetchSingleObservation(observationId);
    }
}