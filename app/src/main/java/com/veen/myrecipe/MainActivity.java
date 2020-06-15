package com.veen.myrecipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.veen.myrecipe.Meals.AllIngridients;
import com.veen.myrecipe.Meals.Areas;
import com.veen.myrecipe.Meals.Favorites;
import com.veen.myrecipe.Meals.MealsFromCats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerViewCats;
    private List<CatsList> catsLists;
    private CatsAdapter catsAdapter;
    private ProgressBar mProgressBar;
    private List<MealList> stringUrls;
    private List<MealList> mealLists;
    private SlidingImage_Adapter slidingImage_adapter;

    private Button mAdd,mAreas,mRandom,mIngredients;
    private ImageView mFavs;

    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth auth;

    private SearchView searchView;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    private ViewPager viewPager;
    private int currentPage = 0;

    private String url = "https://www.themealdb.com/api/json/v2/9973533/categories.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        catsLists = new ArrayList<>();
        stringUrls = new ArrayList<>();

        catsAdapter = new CatsAdapter(catsLists);

        slidingImage_adapter = new SlidingImage_Adapter(stringUrls,MainActivity.this);

        mRecyclerViewCats = findViewById(R.id.recycler_cats);
        mProgressBar = findViewById(R.id.progressBar);
        mAdd = findViewById(R.id.btn_add_recipe);
        mAreas = findViewById(R.id.btn_areas);
        mFavs = findViewById(R.id.btn_fevs);
        mRandom = findViewById(R.id.btn_random_meals);
        mIngredients = findViewById(R.id.btn_ingredients);
        viewPager = findViewById(R.id.pager);



        mIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AllIngridients.class));
            }
        });



        mFavs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Favorites.class));
            }
        });

        mRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MealsFromCats.class);
                intent.putExtra("strCategory","randomselection.php");
                intent.putExtra("title","10 Random meals");
                intent.putExtra("url","https://www.themealdb.com/api/json//v2/9973533/");
                startActivity(intent);
            }
        });


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,3);
        mRecyclerViewCats.setLayoutManager(layoutManager);
        mRecyclerViewCats.setItemAnimator(new DefaultItemAnimator());



        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themealdb.com/add.php")));
            }
        });

        mAreas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Areas.class));
            }
        });


        loadingCats(url);


        MobileAds.initialize(this, getString(R.string.appid));
        mAdView =(AdView) findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        prepareAd();

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                        prepareAd();
                    }
                });

            }
        }, 100, 100, TimeUnit.SECONDS);

    }

    private void prepareAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void loadingCats(String url){
        mProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressBar.setVisibility(View.GONE);
                        loadingLatest();
                        if (user !=null){
                            checkingiffev(user);
                        }
                        //hiding the progressbar after completion
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray heroArray = obj.getJSONArray("categories");

                            for (int i = 0; i < heroArray.length(); i++) {
                                JSONObject jsonObject = heroArray.getJSONObject(i);

                                String idCategory = jsonObject.getString("idCategory");
                                String strCategory = jsonObject.getString("strCategory");
                                String strCategoryThumb = jsonObject.getString("strCategoryThumb");
                                String strCategoryDescription = jsonObject.getString("strCategoryDescription");

                                CatsList catsList = new CatsList(idCategory,strCategory,strCategoryThumb,strCategoryDescription);

                                catsLists.add(catsList);
                                mRecyclerViewCats.setAdapter(catsAdapter);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(MainActivity.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Error indicating that there was an Authentication Failure while performing the request
                        } else if (error instanceof ServerError) {
                            Toast.makeText(MainActivity.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Indicates that the server responded with a error response
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(MainActivity.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Indicates that there was network error while performing the request
                        } else if (error instanceof ParseError) {
                            Toast.makeText(MainActivity.this, "Check your connection", Toast.LENGTH_LONG).show();
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

    private void loadingLatest(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.themealdb.com/api/json/v2/9973533/latest.php",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray heroArray = obj.getJSONArray("meals");

                            for (int i = 0; i < heroArray.length(); i++) {
                                JSONObject jsonObject = heroArray.getJSONObject(i);

                                String strMeal = jsonObject.getString("strMeal");
                                String strMealThumb = jsonObject.getString("strMealThumb");
                                String idMeal = jsonObject.getString("idMeal");

                                MealList mealList = new MealList(strMeal,strMealThumb,idMeal);

                                stringUrls.add(mealList);

                                viewPager.setAdapter(slidingImage_adapter);
                                CircleIndicator indicator =
                                        findViewById(R.id.indicator);
                                indicator.setViewPager(viewPager);

                                indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                                    @Override
                                    public void onPageSelected(int position) {
                                        currentPage = position;

                                    }

                                    @Override
                                    public void onPageScrolled(int pos, float arg1, int arg2) {

                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int pos) {

                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(MainActivity.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Error indicating that there was an Authentication Failure while performing the request
                        } else if (error instanceof ServerError) {
                            Toast.makeText(MainActivity.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Indicates that the server responded with a error response
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(MainActivity.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Indicates that there was network error while performing the request
                        } else if (error instanceof ParseError) {
                            Toast.makeText(MainActivity.this, "Check your connection", Toast.LENGTH_LONG).show();
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



    private void checkingiffev(FirebaseUser user){
        firebaseFirestore.collection("Favorites")
                .document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()) {
                                mFavs.setVisibility(View.VISIBLE);
                            }else {
                                Toast.makeText(MainActivity.this, "Doesnt", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }
}
