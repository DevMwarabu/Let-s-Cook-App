package com.veen.myrecipe.Meals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.veen.myrecipe.CatsList;
import com.veen.myrecipe.MainActivity;
import com.veen.myrecipe.MealList;
import com.veen.myrecipe.MealsAdapter;
import com.veen.myrecipe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MealsFromCats extends AppCompatActivity {
    private String strCategory,url,title;
    private RecyclerView mRecyclerView;
    private List<MealList> mealLists;
    private MealsAdapter mealsAdapter;
    private TextView mCat;
    private ProgressBar mProgressBar;

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals_from_cats);

        strCategory = getIntent().getExtras().getString("strCategory");
        url = getIntent().getExtras().getString("url");
        title = getIntent().getExtras().getString("title");

        mealLists = new ArrayList<>();
        mealsAdapter = new MealsAdapter(mealLists);

        mRecyclerView = findViewById(R.id.recycler_main);
        mCat = findViewById(R.id.tv_cat_title);
        mProgressBar = findViewById(R.id.progressBar);


        mCat.setText(title);


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        MobileAds.initialize(this, getString(R.string.appid));
        mAdView =(AdView) findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        loadingMeals(url);

    }

    private void loadingMeals(String url){
        mProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+strCategory,
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

                                String strMeal = jsonObject.getString("strMeal");
                                String strMealThumb = jsonObject.getString("strMealThumb");
                                String idMeal = jsonObject.getString("idMeal");

                                MealList mealList = new MealList(strMeal,strMealThumb,idMeal);

                                mealLists.add(mealList);
                                mRecyclerView.setAdapter(mealsAdapter);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MealsFromCats.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(MealsFromCats.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Error indicating that there was an Authentication Failure while performing the request
                        } else if (error instanceof ServerError) {
                            Toast.makeText(MealsFromCats.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Indicates that the server responded with a error response
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(MealsFromCats.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Indicates that there was network error while performing the request
                        } else if (error instanceof ParseError) {
                            Toast.makeText(MealsFromCats.this, "Check your connection", Toast.LENGTH_LONG).show();
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
}
