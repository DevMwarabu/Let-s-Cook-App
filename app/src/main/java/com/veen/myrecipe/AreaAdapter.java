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
import com.veen.myrecipe.Meals.Areas;
import com.veen.myrecipe.Meals.MealDesc;
import com.veen.myrecipe.Meals.MealsByArea;

import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder> {
    private List<AreaList> catsLists;
    private Context context;

    public AreaAdapter(List<AreaList> catsLists) {
        this.catsLists = catsLists;
    }

    @NonNull
    @Override
    public AreaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_are, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AreaAdapter.ViewHolder holder, int position) {
        final String strArea = catsLists.get(position).getStrArea();

        holder.settingDetaisl(strArea);

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MealsByArea.class);
                intent.putExtra("strArea",strArea);
                v.getContext().startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return catsLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mArea;
        private LinearLayout mLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mArea = itemView.findViewById(R.id.tv_area);
            mLinearLayout = itemView.findViewById(R.id.linea_main);

        }

        private void settingDetaisl(String strMeal){
            mArea.setText(strMeal);
        }
    }
}
