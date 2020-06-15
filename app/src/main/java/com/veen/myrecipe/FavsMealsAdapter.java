package com.veen.myrecipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.veen.myrecipe.Meals.Favorites;
import com.veen.myrecipe.Meals.MealDesc;
import com.veen.myrecipe.Meals.MealsByArea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FavsMealsAdapter extends RecyclerView.Adapter<FavsMealsAdapter.ViewHolder> {
    private List<FavsList> catsLists;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private FirebaseAuth auth;

    public FavsMealsAdapter(List<FavsList> catsLists) {
        this.catsLists = catsLists;
    }

    @NonNull
    @Override
    public FavsMealsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        context = parent.getContext();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavsMealsAdapter.ViewHolder holder, final int position) {
        final String idMeal = catsLists.get(position).getIdMeal();

        holder.settingDetaisl(idMeal);

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MealDesc.class);
                intent.putExtra("idMeal",idMeal);
                v.getContext().startActivity(intent);
            }
        });

        holder.mLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                firebaseFirestore.collection("Favorites")
                        .document(user.getUid()).collection("Favorites").document(idMeal)
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                            clearing(position);
                        }
                    }
                });

                return true;
            }
        });



    }

    public void clearing(int position){
        catsLists.remove(position);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return catsLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private ImageView mImageMain;
        private LinearLayout mLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.tv_cat_title);
            mImageMain = itemView.findViewById(R.id.image_cat);
            mLinearLayout = itemView.findViewById(R.id.linea_main);

        }

        private void settingDetaisl(String idMeal){
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.themealdb.com/api/json/v2/9973533/lookup.php?i="+idMeal,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            ProgressBar progressBar = Favorites.progressBar;
                            progressBar.setVisibility(View.GONE);
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONArray heroArray = obj.getJSONArray("meals");

                                for (int i = 0; i < heroArray.length(); i++) {
                                    JSONObject jsonObject = heroArray.getJSONObject(i);

                                    String strMeal = jsonObject.getString("strMeal");
                                    String strMealThumb = jsonObject.getString("strMealThumb");
                                    String idMeal = jsonObject.getString("idMeal");

                                    mTitle.setText(strMeal);


                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.centerCrop();

                                    Glide.with(context.getApplicationContext()).load(strMealThumb)
                                            .into(mImageMain);

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(context.getApplicationContext(), "Connection timeout error", Toast.LENGTH_SHORT).show();
                                //This indicates that the reuest has either time out or there is no connection
                            } else if (error instanceof AuthFailureError) {
                                Toast.makeText(context, "Check your connection", Toast.LENGTH_LONG).show();
                                //Error indicating that there was an Authentication Failure while performing the request
                            } else if (error instanceof ServerError) {
                                Toast.makeText(context, "Check your connection", Toast.LENGTH_LONG).show();
                                //Indicates that the server responded with a error response
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(context, "Check your connection", Toast.LENGTH_LONG).show();
                                //Indicates that there was network error while performing the request
                            } else if (error instanceof ParseError) {
                                Toast.makeText(context, "Check your connection", Toast.LENGTH_LONG).show();
                                // Indicates that the server response could not be parsed
                            }
                        }
                    }) {

            };

            //creating a request queue
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            //adding the string request to request queue
            requestQueue.add(stringRequest);
        }
    }
}
