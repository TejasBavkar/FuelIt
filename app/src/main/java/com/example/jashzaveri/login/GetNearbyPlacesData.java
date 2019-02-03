package com.example.jashzaveri.login;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.example.jashzaveri.login.MapsActivity.latitude;
import static com.example.jashzaveri.login.MapsActivity.longitude;


class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private String googlePlacesData;
    private GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... objects){
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s){

        List<HashMap<String, String>> nearbyPlaceList;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        Log.d("nearbyplacesdata","called parse method");
        showNearbyPlaces(nearbyPlaceList);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
    {
        double min_distance=Integer.MAX_VALUE;
        int index_nearest=0;
        double n_lat = 0,n_lng=0;
        for(int i = 0; i < nearbyPlaceList.size(); i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lng"));

            LatLng latLng = new LatLng( lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : "+ vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            float result[] = new float[10];
            Location.distanceBetween(latitude, longitude, lat, lng, result);
            if(result[0]<min_distance){
                min_distance = result[0];
                index_nearest = i;
                n_lat=lat;
                n_lng=lng;
            }
        }
        LatLng latLng = new LatLng(n_lat , n_lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Destination Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptions.draggable(true);
        mMap.addMarker(markerOptions);


    }
}
