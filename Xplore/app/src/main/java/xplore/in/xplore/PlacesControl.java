package xplore.in.xplore;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.facebook.AccessToken;

/**
 * Created by Sachin on 4/10/2016.
 * Manages Places related requests to Graph API
 */
public class PlacesControl {

    private static long MIN_TIME_TO_UPDATE = 10000;
    private static long MIN_RADIUS = 100;

    protected void getNearbyPlaces(Context context, Location location) {
        if(location == null) {
            // check if location is turned on by the user and take necessary actions if not

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            checkLocationAvailability(locationManager, context);

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    doPlaceSearch(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if(locationManager != null) {
                if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    // should we show explanation for getting permission?
//                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                        Toast.makeText(context, "Location permission needed to function", Toast.LENGTH_LONG).show();
//                    }
//                    else { // no explanation required. Continue with requesting permissino
//                        ActivityCompat.requestPermissions(MainActivity, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION},  );
//                    }
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_TO_UPDATE, MIN_RADIUS, locationListener);
            }

        }
    }

    protected void getNearbyPlaces(Context context, String location) {
//        newPlaceSearchRequest(AccessToken.getCurrentAccessToken(), )
    }

    protected void checkLocationAvailability(LocationManager locationManager, final Context context) {
        boolean isGPSEnabled;
        boolean isNetworkEnabled;

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSEnabled || !isNetworkEnabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Location services disabled. Turn on location.");
            dialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(context, "Enable location to use all features", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    protected void doPlaceSearch(Location location) {

    }
}
