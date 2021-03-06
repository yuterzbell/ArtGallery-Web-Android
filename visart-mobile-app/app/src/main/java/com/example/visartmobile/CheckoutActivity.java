package com.example.visartmobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.visartmobile.util.ArtListing;
import com.example.visartmobile.util.HttpUtils;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CheckoutActivity extends AppCompatActivity {
    private Handler mHandler;
    private String listingId;
    private String artPieceId;
    private String listingPrice;
    private String listingArtist;
    private String typedAddress;
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Boolean isAtGallery = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // prevents user from going back to previous activity
                    startActivity(loginIntent);
                    finish();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        Bundle extras = getIntent().getExtras();
        listingId = extras.getString("idCodeListing");

        mHandler = new Handler(Looper.getMainLooper());

    }

    public void clickedAddress(View view) {
        EditText et = (EditText) findViewById(R.id.addressField);
        et.setVisibility(View.VISIBLE);
        typedAddress = ((EditText) findViewById(R.id.addressField)).getText().toString();
        isAtGallery = false;
    }

    public void clickedPickUp(View view) {
        EditText et = (EditText) findViewById(R.id.addressField);
        et.setVisibility(View.INVISIBLE);
        typedAddress = "TBD";
        isAtGallery = true;
    }

    /**
     * This method performs the buying by getting the artlisting the customer wants to buy and its
     * information. Then this method creates an art order and a ticket and finally redirects the
     * User to the order success page or displays an error message.
     * @param view
     */
    public void clickedPurchase(View view) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HttpUtils.get("artlisting/get/" + listingId, new String[][]{}, (resp1) -> {
            if (resp1.isSuccessful()) {
                try {
                    final ArtListing al = ArtListing.parseJSON(new JSONObject(resp1.body().string()));
                    // buy first artpiece from al now:
                    String[][] data = {
                            {"aIsDelivered", "false"},
                            {"pieceLocation", isAtGallery ? "AtGallery" : "Offsite"}, // TODO: change true to a condition, based on radio box
                            {"aTargetAddress", typedAddress}, //do address
                            {"aDeliveryTracker", "TBD"},
                            {"artPieceId", al.getArtPieceIds().length > 0 ? al.getArtPieceIds()[0] : ""}
                    };
                    HttpUtils.postForm("/artorder/create/", data, (resp2) -> {
                        if (resp2.isSuccessful()) {
                            try {
                                JSONObject ao = new JSONObject(resp2.body().string());
                                String[][] ticketData = {
                                        {"aIsPaymentConfirmed", "false"},
                                        {"aPaymentAmount", String.valueOf(al.getPrice())},
                                        {"aOrder", ao.getString("idCode")},
                                        {"aCustomer", userId},
                                        {"aArtist", al.getArtistId()}
                                };
                                HttpUtils.postForm("tickets/create", ticketData, (resp3) -> {
                                    if (resp3.isSuccessful()) {
                                        // ticket was created yay!
                                        System.out.println("you bought artwork yay!");
                                        showToastFromThread("You bought this artwork yay!");
                                        purchaseClicked();
                                    } else {
                                        System.err.println("Error: " + resp3.code());
                                        System.err.println("Unsuccessful ticket creation");
                                    }
                                });
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // unsuccessful artorder creation
                            System.err.println("Unsuccessful artorder creation");
                            System.err.println("Error: " + resp2.code());
                        }
                    });

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Error: " + resp1.code());
                System.err.println("Unsuccessful artlisting retrieval");
            }
        });
        
    }

    public void getPurchaseListingInfo() {
        try {
            HttpUtils.get("artlisting/get/" + listingId, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    showToastFromThread("Database failed to connect");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        System.out.println("Call sucessful");

                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            ArtListing listing = ArtListing.parseJSON(json);
                            artPieceId = listing.getArtPieceId();
                            listingPrice = (String.valueOf(listing.getPrice()));
                            listingArtist = listing.getArtistId();

                        } catch (Exception e) {
                            System.out.println("Error creating JSON object: " + e.getMessage());
                        }

                    } else {
                        System.out.println("error occured");
                    }
                }
            });
        } catch (Exception ex) {

        }
    }

    public void showToastFromThread(String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    /**
     * This method redirects the user to the order success page
     */
    public void purchaseClicked() {
        Intent orderIntent = new Intent(getApplicationContext(), OrderSuccess.class);
        orderIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // prevents user from going back to previous activity
        startActivity(orderIntent);
        finish();
    }

    /**
     * This method is called when the user clicks the button.
     * Calls the  method to redirect user to the order success page.
     *
     * @param view
     */
    public void onPurchaseClick(View view) {
        purchaseClicked();
    }

}