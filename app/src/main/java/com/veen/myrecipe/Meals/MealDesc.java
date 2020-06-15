package com.veen.myrecipe.Meals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.veen.myrecipe.MealList;
import com.veen.myrecipe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MealDesc extends AppCompatActivity {
    private ImageView mMainImage;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView mCat,mArea,mInstructions,mTags,mYoutube,mIngridients,mMeasures,mSource;
    private FloatingActionButton mFloat;


    private GoogleSignInClient mGoogleSignInClient;
    private Button mGoogleSigIn;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    private AdView mAdView;

    private String idMeal,url="https://www.themealdb.com/api/json/v2/9973533/lookup.php?i=";

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_desc);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();



        idMeal = getIntent().getExtras().getString("idMeal");

        toolbar = findViewById(R.id.main_toolbar);

        getSupportActionBar();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mProgressBar = findViewById(R.id.progressBar);

        mMainImage = findViewById(R.id.image_main);
        collapsingToolbarLayout = findViewById(R.id.main_collapsing);
        mCat = findViewById(R.id.tv_cat_title);
        mArea = findViewById(R.id.tv_area);
        mInstructions = findViewById(R.id.tv_instructions);
        mTags = findViewById(R.id.tv_tags);
        mYoutube = findViewById(R.id.tv_youtube);
        mIngridients = findViewById(R.id.tv_ingredient);
        mMeasures = findViewById(R.id.tv_measure);
        mSource = findViewById(R.id.tv_source);
        mFloat = findViewById(R.id.float_main);


        MobileAds.initialize(this, getString(R.string.appid));
        mAdView =(AdView) findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (user!=null){
            checkingiffev(user);
        }



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loadingMealDesc(url);

        mFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingFev(user,idMeal);
            }
        });


    }

    private void loadingMealDesc(String url){
        mProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+idMeal,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        //hiding the progressbar after completion
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray heroArray = obj.getJSONArray("meals");

                            for (int i = 0; i < heroArray.length(); i++) {
                                JSONObject jsonObject = heroArray.getJSONObject(i);

                                String idMeal=jsonObject.getString("idMeal");
                                String strMeal=jsonObject.getString("strMeal");
                                String strDrinkAlternate =jsonObject.getString("strDrinkAlternate");
                                String strCategory =jsonObject.getString("strCategory");
                                String strArea =jsonObject.getString("strArea");
                                String strInstructions =jsonObject.getString("strInstructions");
                                String strMealThumb =jsonObject.getString("strMealThumb");
                                String strTags =jsonObject.getString("strTags");
                                String strYoutube =jsonObject.getString("strYoutube");
                                String strIngredient1 =jsonObject.getString("strIngredient1");
                                String strIngredient2 =jsonObject.getString("strIngredient2");
                                String strIngredient3 =jsonObject.getString("strIngredient3");
                                String strIngredient4 =jsonObject.getString("strIngredient4");
                                String strIngredient5 =jsonObject.getString("strIngredient5");
                                String strIngredient6 =jsonObject.getString("strIngredient6");
                                String strIngredient7 =jsonObject.getString("strIngredient7");
                                String strIngredient8 =jsonObject.getString("strIngredient8");
                                String strIngredient9 =jsonObject.getString("strIngredient9");
                                String strIngredient10 =jsonObject.getString("strIngredient10");
                                String strIngredient11 =jsonObject.getString("strIngredient11");
                                String strIngredient12 =jsonObject.getString("strIngredient12");
                                String strIngredient13 =jsonObject.getString("strIngredient13");
                                String strIngredient14 =jsonObject.getString("strIngredient14");
                                String strIngredient15 =jsonObject.getString("strIngredient15");
                                String strIngredient16 =jsonObject.getString("strIngredient16");
                                String strIngredient17 =jsonObject.getString("strIngredient17");
                                String strIngredient18 =jsonObject.getString("strIngredient18");
                                String strIngredient19 =jsonObject.getString("strIngredient19");
                                String strIngredient20 =jsonObject.getString("strIngredient20");
                                String strMeasure1 =jsonObject.getString("strMeasure1");
                                String strMeasure2 =jsonObject.getString("strMeasure2");
                                String strMeasure3 =jsonObject.getString("strMeasure3");
                                String strMeasure4 =jsonObject.getString("strMeasure4");
                                String strMeasure5 =jsonObject.getString("strMeasure5");
                                String strMeasure6 =jsonObject.getString("strMeasure6");
                                String strMeasure7 =jsonObject.getString("strMeasure7");
                                String strMeasure8 =jsonObject.getString("strMeasure8");
                                String strMeasure9 =jsonObject.getString("strMeasure9");
                                String strMeasure10 =jsonObject.getString("strMeasure10");
                                String strMeasure11 =jsonObject.getString("strMeasure11");
                                String strMeasure12 =jsonObject.getString("strMeasure12");
                                String strMeasure13 =jsonObject.getString("strMeasure13");
                                String strMeasure14 =jsonObject.getString("strMeasure14");
                                String strMeasure15 =jsonObject.getString("strMeasure15");
                                String strMeasure16 =jsonObject.getString("strMeasure16");
                                String strMeasure17 =jsonObject.getString("strMeasure17");
                                String strMeasure18 =jsonObject.getString("strMeasure18");
                                String strMeasure19 =jsonObject.getString("strMeasure19");
                                String strMeasure20 =jsonObject.getString("strMeasure20");
                                String strSource =jsonObject.getString("strSource");
                                String dateModified =jsonObject.getString("dateModified");

                                collapsingToolbarLayout.setTitle(strMeal);

                                mCat.setText(strCategory);
                                mArea.setText(strArea+" Cuisine");
                                mInstructions.setText(strInstructions);
                                if (!strTags.equals("") && !strTags.isEmpty()){
                                    mTags.setText(strTags);
                                }else {
                                    mTags.setText("Not yet specified");
                                }
                                if (!strYoutube.equals("")) {
                                    mYoutube.setText(strYoutube);
                                }else {
                                    mYoutube.setText("No video");
                                }
                                if (!strSource.equals("")) {
                                    mSource.setText(strSource);
                                }else {
                                    mSource.setText("No link");
                                }

                                mIngridients.setText(strIngredient1+"\n"+strIngredient2+"\n"+strIngredient3+"\n"+strIngredient4
                                        +"\n"+strIngredient5+"\n"+strIngredient6+"\n"+strIngredient7+"\n"+strIngredient8+"\n"+strIngredient9
                                        +"\n"+strIngredient10+"\n"+strIngredient11+"\n"+strIngredient12+"\n"+strIngredient13+"\n"+strIngredient14
                                        +"\n"+strIngredient15+"\n"+strIngredient16+"\n"+strIngredient17+"\n"+strIngredient18+"\n"+strIngredient19
                                        +"\n"+strIngredient20);

                                mMeasures.setText(strMeasure1+"\n"+strMeasure2+"\n"+strMeasure3+"\n"+strMeasure4+"\n"+strMeasure5+"\n"+strMeasure6
                                        +"\n"+strMeasure7+"\n"+strMeasure8+"\n"+strMeasure9+"\n"+strMeasure10+"\n"+strMeasure12+"\n"+strMeasure13
                                        +"\n"+strMeasure14+"\n"+strMeasure15+"\n"+strMeasure16+"\n"+strMeasure17+"\n"+strMeasure18
                                        +"\n"+strMeasure19+"\n"+strMeasure20);

                                Glide.with(getApplicationContext()).load(strMealThumb).into(mMainImage);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MealDesc.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "Connection timeout error", Toast.LENGTH_SHORT).show();
                            //This indicates that the reuest has either time out or there is no connection
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(MealDesc.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Error indicating that there was an Authentication Failure while performing the request
                        } else if (error instanceof ServerError) {
                            Toast.makeText(MealDesc.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Indicates that the server responded with a error response
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(MealDesc.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Indicates that there was network error while performing the request
                        } else if (error instanceof ParseError) {
                            Toast.makeText(MealDesc.this, "Check your connection", Toast.LENGTH_LONG).show();
                            // Indicates that the server response could not be parsed
                        }
                    }
                }) {

        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    private void addingFev(final FirebaseUser user, final String idMeal){
        if (user != null){
            mProgressBar.setVisibility(View.VISIBLE);

            final Map<String,Object> map = new HashMap<>();
            map.put("idMeal",idMeal);
            map.put("timeStamp", Timestamp.now());

            firebaseFirestore.collection("Favorites")
                    .document(user.getUid()).collection("Favorites").document(idMeal)
                    .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        Map<String,Object> timeStamp = new HashMap<>();
                        map.put("timeStamp", Timestamp.now());
                        firebaseFirestore.collection("Favorites")
                                .document(user.getUid()).set(timeStamp);

                        Toast.makeText(MealDesc.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                        mFloat.setImageResource(R.drawable.icon_fev_after);

                        mFloat.setEnabled(false);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(MealDesc.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            //showing dialog
            final Dialog dialog = new Dialog(MealDesc.this,R.style.Theme_AppCompat_Dialog_Alert);
            dialog.setContentView(R.layout.dialog_email_need);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            //getting views from the layout
            mGoogleSigIn = dialog.findViewById(R.id.btn_yes);
            Button no = dialog.findViewById(R.id.btn_no);

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            mGoogleSigIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                    dialog.dismiss();
                }
            });


            dialog.show();
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        mProgressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            addingFev(user,idMeal);
                            mProgressBar.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(MealDesc.this, "Something went wrong "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }

                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void checkingiffev(FirebaseUser user){
        firebaseFirestore.collection("Favorites")
                .document(user.getUid()).collection("Favorites").document(idMeal).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()) {
                                mFloat.setEnabled(false);
                                mFloat.setImageResource(R.drawable.icon_fev_after);
                            }
                        }
                    }
                });
    }
}
