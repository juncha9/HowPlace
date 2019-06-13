package com.alkemic.howplace.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.alkemic.howplace.Singletone.InfoFinder;
import com.alkemic.howplace.Singletone.ItemManager;
import com.alkemic.howplace.Singletone.LocationChecker;
import com.alkemic.howplace.Singletone.PlaceFinder;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class SplashActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Log.d("SplashActivity","Start to Initialize");

        InitPermission();
        InitSingletone();
        boolean isFindStart = PlaceFinder.getInstance().Update();
        LoadMainActivity(isFindStart);

    }
    private void InitPermission()
    {
        Toast.makeText(this,"권한을 요청합니다.",Toast.LENGTH_LONG).show();
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
        .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        .subscribe(  granted -> {
            if (granted) {
                // All requested permissions are granted
                Toast.makeText(this,"권한이 승인되었습니다.",Toast.LENGTH_LONG).show();
                LocationChecker.getInstance().setPermission(LocationChecker.PERMISSION_GRANT);

            } else {
                // At least one permission is denied
                Toast.makeText(this,"권한이 거부되었습니다.",Toast.LENGTH_LONG).show();
                LocationChecker.getInstance().setPermission(LocationChecker.PERMISSION_DENY);
            }
        });
        int waitSecond = 0;
        while ( (waitSecond < 10) && (LocationChecker.getInstance().getPermission() < LocationChecker.PERMISSION_GRANT))
        {
            try
            {
                Thread.sleep(1000);
                Log.d("Init","Wait for permission (" +Integer.toString(waitSecond)+")" );
            } catch (InterruptedException e) {
                Log.e("Init", e.toString());
            }
            waitSecond++;
        }
        if(LocationChecker.getInstance().getPermission() == LocationChecker.PERMISSION_GRANT)
        {
            Log.d("Init","Permission granted");

        }
        else if(LocationChecker.getInstance().getPermission() <= LocationChecker.PERMISSION_BASE)
        {
            Log.d("Init","Permission denied");
        }
    }
    private void InitSingletone()
    {
        boolean isSingletoneInstanced = false;
        while (!isSingletoneInstanced) {
            isSingletoneInstanced = true;
            isSingletoneInstanced &= LocationChecker.getInstance() != null;
            isSingletoneInstanced &= ItemManager.getInstance() != null;
            isSingletoneInstanced &= PlaceFinder.getInstance() != null;
            isSingletoneInstanced &= InfoFinder.getInstance() != null;
        }
        if(ItemManager.getInstance().Initialize()) {
            Log.d("init" ,"ItemManager initialized success");
        } else {
            Log.e("init" ,"ItemManager initialized failed");
        }
        if (LocationChecker.getInstance().Initialize(context)) {
            Log.d("Init", "LocationChecker initialized success");
        } else {
            Log.e("Init", "LocationChecker initialized failed");
        }
        if (PlaceFinder.getInstance().Initialize()) {
            Log.d("Init", "PlaceFinder initialized success");
        } else {
            Log.e("Init", "PlaceFinder initialized failed");
        }
        if(InfoFinder.getInstance().Initialize(context)) {
            Log.d("Init", "InfoFinder initialized success");
        } else {
            Log.e("Init", "InfoFinder initialized failed");
        }
    }


    private void LoadMainActivity(boolean isFindStart)
    {
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("isFindStart",isFindStart);
        startActivity(intent);
        finish();
    }



}
