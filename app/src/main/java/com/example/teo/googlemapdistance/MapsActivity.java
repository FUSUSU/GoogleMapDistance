package com.example.teo.googlemapdistance;

import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener {

    //Our Map
    private GoogleMap mMap;

    //To store longitude and latitude from map.
    //Lưu trử kinh độ và vĩ độ từ bản đồ.
    private double longitude;
    private double latitude;

    //From -> the first coordinate from where we need to calculate the distance
    //Từ -> tọa độ đầu tiên từ nời chúng ta cần tính khoảng cách
    private double fromLongitude;
    private double fromLatitude;

    //To -> the second coordinate to where we need to calculate the distance
    //Đến -> tọa độ thứ hai đến nời chúng ta cần tính khoảng cách.
    private double toLongitude;
    private double toLatitude;

    //Google ApiClient
    private GoogleApiClient googleApiClient;

    //Our buttons
    private Button buttonSetTo; //button Vị trí đến
    private Button buttonSetFrom; //button Vị trí đi
    private Button buttonCalcDistance; //button tính khoảng cách

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleapi client
        // ATTENTION: this "addApi (AppIndex.API)" was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();

        //Ánh xạ
        buttonSetTo = (Button) findViewById(R.id.buttonSetTo);
        buttonSetFrom = (Button) findViewById(R.id.buttonSetFrom);
        buttonCalcDistance = (Button) findViewById(R.id.buttonCalcDistance);

        //Gắn ống nghe cho button
        buttonSetTo.setOnClickListener(this);
        buttonSetFrom.setOnClickListener(this);
        buttonCalcDistance.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(googleApiClient, getIndexApiAction());
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
        // Thêm đánh dấu Sydney và di chuyển camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).draggable(true)); //kéo.
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney")); //Tiêu đề.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney)); //di chuyển camera.
        mMap.setOnMarkerDragListener(this); //Gắn bộ nghe đánh dấu cho map
        mMap.setOnMapLongClickListener(this); //Gắn bộ nghe LongClick - nhấn giữ lâu cho map
    }

    //Getting current location
    //Nhận vị trí hiện tại
    private void getCurrentLocation() {
        mMap.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            //Lấy kinh độ và vĩ độ
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }

    }

    //Function to move the map
    private void moveMap(){
        //Creating a LatLng object to store Coordinates.
        //Tạo một một đối tượng LatLng để lưu trữ tạo độ.
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        //Thêm đánh dấu bản đồ.
        mMap.addMarker(new MarkerOptions()
            .position(latLng) //Setting position //Cài đặt vị trí
            .draggable(true) //Making the marker draggable
            .title("Current Location")); //Adding a title // Thêm một tiêu đề.

        //Moving the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://host/path"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW) //TODO: choose an action type.
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(googleApiClient, getIndexApiAction());
        googleApiClient.disconnect();
    }
}
