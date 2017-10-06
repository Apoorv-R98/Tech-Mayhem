package com.example.asus.afinal;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.apache.http.conn.ConnectTimeoutException;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class DonatorActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Button callButton;
    Boolean requestActive=false;
    Handler handler = new Handler();
    TextView infoTextView;
    Boolean driverActive= true;
    public void checkForUpdates()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereExists("driverUsername");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null && objects.size() >0){
                    driverActive=true;
                    ParseQuery<ParseUser> query= ParseUser.getQuery();
                    query.whereEqualTo("username",objects.get(0).getString("driverUsername"));
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> list, ParseException e) {
                            if(e == null && objects.size()>0){
                                ParseGeoPoint driverLocation=objects.get(0).getParseGeoPoint("location");
                                if (Build.VERSION.SDK_INT<23 || ContextCompat.checkSelfPermission(DonatorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                                    Location lastKnownLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    if (lastKnownLocation!=null){
                                        ParseGeoPoint userLoaction=new ParseGeoPoint(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                                        Double distanceInMiles=driverLocation.distanceInMilesTo(userLoaction);
                                        if(distanceInMiles<0.01){
                                            infoTextView.setText("Your driver is here");
                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
                                            query.whereEqualTo("username",ParseUser.getCurrentUser().getUsername();
                                            query.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> list, ParseException e) {
                                                    if(e == null){
                                                        for (ParseObject object: objects){
                                                            object.deleteInBackground();
                                                        }
                                                    }
                                                }
                                            });

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                infoTextView.setText("");
                                                callButton.setVisibility(View.VISIBLE);
                                                callButton.setText("Request for Pickup");
                                                requestActive=false;
                                                driverActive=false;
                                            }
                                        },5000);



                                        }else{
                                            Double distanceOneDP=(double) Math.round(distanceInMiles*10)/10;
                                            infoTextView.setText("Your driver is " + distanceOneDP.toString() + "miles away!");
                                            LatLng driverLocationLatLng=new LatLng(driverLocation.getLatitude(),driverLocation.getLongitude());
                                            LatLng requestLoactionLatLng = new LatLng(userLoaction.getLatitude(),userLoaction.getLongitude());
                                            ArrayList<Marker> markers=new ArrayList<>();
                                            mMap.clear();
                                            markers.add(mMap.addMarker(new MarkerOptions().position(driverLocationLatLng).title("Driver Location")));
                                            
                                        }
                                    }
                                }
                            }
                        }
                    })
                }
            }
        })



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donator);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
