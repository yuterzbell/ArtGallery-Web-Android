package com.example.visartmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.visartmobile.util.HttpUtils;
import com.example.visartmobile.util.UserAuth;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
    }

    public void clickedAddress(View view) {
        EditText et=(EditText)findViewById(R.id.addressField);
        et.setVisibility(View.VISIBLE);

    }

    public void clickedPickUp(View view) {
        EditText et=(EditText)findViewById(R.id.addressField);
        et.setVisibility(View.INVISIBLE);
    }

    public void clickedPurchase(View view) {

    }
}