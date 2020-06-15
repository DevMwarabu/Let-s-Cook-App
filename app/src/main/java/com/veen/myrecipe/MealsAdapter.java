package com.veen.myrecipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.veen.myrecipe.Meals.MealDesc;
import com.veen.myrecipe.Meals.MealsFromCats;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.ViewHolder> {
    private List<MealList> catsLists;
    private Context context;

    public MealsAdapter(List<MealList> catsLists) {
        this.catsLists = catsLists;
    }

    @NonNull
    @Override
    public MealsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealsAdapter.ViewHolder holder, int position) {
        String strMeal = catsLists.get(position).getStrMeal();
        String strMealThumb = catsLists.get(position).getStrMealThumb();
        final String idMeal = catsLists.get(position).getIdMeal();

        holder.settingDetaisl(strMeal,strMealThumb);

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MealDesc.class);
                intent.putExtra("idMeal",idMeal);
                v.getContext().startActivity(intent);
            }
        });



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

        private void settingDetaisl(String strMeal,String strMealThumb){
            mTitle.setText(strMeal);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();

            Glide.with(context.getApplicationContext()).load(strMealThumb)
                    .into(mImageMain);
        }
    }
}
