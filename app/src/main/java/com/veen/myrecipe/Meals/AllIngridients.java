package com.veen.myrecipe.Meals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.veen.myrecipe.AreaAdapter;
import com.veen.myrecipe.AreaList;
import com.veen.myrecipe.IngredientList;
import com.veen.myrecipe.IngredientsAdapter;
import com.veen.myrecipe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllIngridients extends AppCompatActivity {
    private String url = "https://www.themealdb.com/api/json/v2/9973533/list.php?i=list";
    private RecyclerView mRecyclerView;
    private List<IngredientList> mealLists;
    private IngredientsAdapter mealsAdapter;
    private TextView mCat;
    private ProgressBar mProgressBar;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    public static CheckBox mSelectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_ingridients);

        mealLists = new ArrayList<>();
        mealsAdapter = new IngredientsAdapter(mealLists);

        mRecyclerView = findViewById(R.id.recycler_main);
        mCat = findViewById(R.id.tv_cat_title);
        mProgressBar = findViewById(R.id.progressBar);
        mSelectAll = findViewById(R.id.checkbox_main);


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,1);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());



        loadingAreas(url);
    }

    private void loadingAreas(String url){
        mProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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

                                String idIngredient = jsonObject.getString("idIngredient");
                                String strIngredient = jsonObject.getString("strIngredient");
                                String strDescription = jsonObject.getString("strDescription");
                                String strType = jsonObject.getString("strType");
                                Boolean isChecked = false;

                                IngredientList ingredientList = new IngredientList(idIngredient,strIngredient,strDescription,strType,isChecked);

                                mealLists.add(ingredientList);
                                mRecyclerView.setAdapter(mealsAdapter);

                                mealLists.get(i).setChecked(isChecked);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AllIngridients.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "Connection timeout error", Toast.LENGTH_SHORT).show();
                            //This indicates that the reuest has either time out or there is no connection
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(AllIngridients.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Error indicating that there was an Authentication Failure while performing the request
                        } else if (error instanceof ServerError) {
                            Toast.makeText(AllIngridients.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Indicates that the server responded with a error response
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(AllIngridients.this, "Check your connection", Toast.LENGTH_LONG).show();
                            //Indicates that there was network error while performing the request
                        } else if (error instanceof ParseError) {
                            Toast.makeText(AllIngridients.this, "Check your connection", Toast.LENGTH_LONG).show();
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
