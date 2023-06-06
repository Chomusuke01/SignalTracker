package com.example.test;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.test.databinding.ActivityMapsBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import inf.um.storageexample.StorageHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCircleClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private FusedLocationProviderClient client;

    private Location lastLocation;

    private TelephonyManager telephonyManager;

    private HashMap<Integer, LinkedList<InfoPunto>> etapas;

    private Integer etapaActual;

    private HashSet<Integer> antenasLocalizadas;

    private String fileName;

    private TextView textViewEtapa;
    private LocationCallback callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = LocationServices.getFusedLocationProviderClient(this);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        etapaActual = 0;
        textViewEtapa = (TextView) binding.getRoot().findViewById(R.id.infoEtapaView);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textViewEtapa.setTextSize(20);
        textViewEtapa.setBackgroundColor(Color.WHITE);
        textViewEtapa.setText(String.format("%s: %d", getString(R.string.etapaActual), etapaActual));
        etapas = new HashMap<>();
        antenasLocalizadas = new HashSet<>();
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if(!extras.isEmpty()){
            Object extra = extras.get("FileName");
            if (extra instanceof String){
                fileName = String.valueOf(extra);
            }
        }

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
        LatLng spain = new LatLng(40.463667,  -3.74922);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(spain));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCircleClickListener(this);
        showPosition();
        showLocationUpdates();
    }
    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Toast.makeText(this, String.valueOf(marker.getTag()), Toast.LENGTH_SHORT).show();
        return false;
    }
    private void showPosition() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);

            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        switch (requestCode){
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    showPosition();
                    showLocationUpdates();
                }else {
                    Toast.makeText(this, "Need permisions to work", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void nuevaEtapa(){
        this.etapaActual += 1;
        textViewEtapa.setText(getString(R.string.etapaActual) + ": " + etapaActual);
    }

    public void onNuevaEtapaPressed(View v){
        nuevaEtapa();
    }

    public void onFinPressed(View v){
        fin();
    }


    public void fin(){

        JsonObject res = new JsonObject();
        JsonArray etapaArray = new JsonArray();

        for(int etapa: etapas.keySet()){

            List<Integer> dbms = etapas.get(etapa).stream().map(InfoPunto::getdBm).collect(Collectors.toList());
            List<Integer> strength = etapas.get(etapa).stream().map(InfoPunto::getSignalStrength).collect(Collectors.toList());

            Etapa infoEtapa = new Etapa();
            infoEtapa.setdBmMax(Collections.max(dbms));
            infoEtapa.setdBmMin(Collections.min(dbms));
            infoEtapa.setdBmMean((int)dbms.stream().mapToDouble(a -> a).average().getAsDouble());

            infoEtapa.setMaxSignalLevel(Collections.max(strength));
            infoEtapa.setMinSignalLevel(Collections.min(strength));
            infoEtapa.setSignalMean((int)strength.stream().mapToDouble(a -> a).average().getAsDouble());
            infoEtapa.setNombre("Etapa_" + etapa);
            etapaArray.add(infoEtapa.toJson());
        }
        res.add("Etapas", etapaArray);
        try {
            StorageHelper.saveStringToFile(fileName,res.toString(),this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finish();
    }

    public void showLocationUpdates(){

        LocationRequest locationRequest = new LocationRequest.Builder(2000).setMinUpdateIntervalMillis(3000).build();
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (lastLocation == null || location.distanceTo(lastLocation) >= 5) {

                    InfoPunto infoPunto = cellInfo();
                    if (infoPunto != null){
                        int color = signalColor(infoPunto.getSignalStrength());

                        CircleOptions punto = new CircleOptions()
                                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                                .radius(3).fillColor(color)
                                .strokeColor(Color.BLACK).strokeWidth(2)
                                .clickable(true)
                                ;

                        Circle circle = mMap.addCircle(punto);
                        circle.setTag(getString(R.string.nivelSeñal) + ": " + infoPunto.getSignalStrength() + "\ndBm: " + infoPunto.getdBm());
                        lastLocation = location;
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        client.requestLocationUpdates(locationRequest, callback, null);
    }

    private int signalColor(int nivel){
        int color = -1;
        switch (nivel){
            case 1:
                color = Color.RED;
                break;
            case 2:
                color = Color.rgb(255,128,0);
                break;

            case 3:
                color = Color.YELLOW;
                break;

            case 4:
                color = Color.GREEN;
                break;
        }
        return color;
    }


    public void localizarAntenas(int mcc, int mnc, int lac, int cellID){

        Call<Data> call = RetrofitClient.getInstance().getMyApi().getAntennaInformtion(1.1,"open", mcc, mnc, lac, cellID);

        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                Data result = response.body();
                AntennaResults antena = result.getAntennaResults();
                LatLng ant = new LatLng(antena.getLatitud(), antena.getLongitud());
                mMap.addMarker(new MarkerOptions().position(ant));
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }

    public InfoPunto cellInfo() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},0);
            return null;
        }
        List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
        List<Integer> dBms = new ArrayList<>();
        List<Integer> signalStrength = new ArrayList<>();

        for (CellInfo info : cellInfoList){
            if (info instanceof CellInfoLte){
                CellInfoLte cellInfoLte = (CellInfoLte) info;
                CellIdentityLte id = cellInfoLte.getCellIdentity();
                dBms.add(cellInfoLte.getCellSignalStrength().getDbm());
                signalStrength.add(cellInfoLte.getCellSignalStrength().getLevel());
                if (cellInfoLte.isRegistered() && !antenasLocalizadas.contains(id.getCi())){

                    int mcc;
                    int mnc;
                    int cellID = id.getCi();
                    int lac = id.getTac();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
                        mcc = id.getMcc();
                        mnc = id.getMnc();
                    }else {
                        mcc = Integer.parseInt(id.getMccString());
                        mnc = Integer.parseInt(id.getMncString());
                    }
                    localizarAntenas(mcc,mnc,lac,cellID);
                    antenasLocalizadas.add(cellID);
                }
            }
        }
        if (dBms.isEmpty()){
            return null;
        }
        int maxDBM = Collections.max(dBms);
        int maxStrength = Collections.max(signalStrength);
        InfoPunto infoPunto = new InfoPunto(maxDBM, maxStrength);
        if (etapas.get(etapaActual) == null){
            LinkedList<InfoPunto> nuevaEtapa = new LinkedList<>();
            nuevaEtapa.add(infoPunto);
            etapas.put(etapaActual,nuevaEtapa);
        }else{
            etapas.get(etapaActual).add(infoPunto);
        }
        return infoPunto;
    }

    @Override
    public void onCircleClick(@NonNull Circle circle) {
        Toast.makeText(this, String.valueOf(circle.getTag()), Toast.LENGTH_SHORT).show();
    }
}