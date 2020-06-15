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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.veen.myrecipe.Meals.MealsFromCats;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CatsAdapter extends RecyclerView.Adapter<CatsAdapter.ViewHolder> {
    private List<CatsList> catsLists;
    private Context context;

    public CatsAdapter(List<CatsList> catsLists) {
        this.catsLists = catsLists;
    }

    @NonNull
    @Override
    public CatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatsAdapter.ViewHolder holder, int position) {
        String idCategory = catsLists.get(position).getIdCategory();
        final String strCategory = catsLists.get(position).getStrCategory();
        String strCategoryThumb = catsLists.get(position).getStrCategoryThumb();
        String strCategoryDescription = catsLists.get(position).getStrCategoryDescription();

        holder.settingDetaisl(strCategory,strCategoryThumb);

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MealsFromCats.class);
                intent.putExtra("strCategory",strCategory);
                intent.putExtra("title",strCategory);
                intent.putExtra("url","https://www.themealdb.com/api/json/v2/9973533/filter.php?c=");
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
        private CircleImageView mImageMain;
        private LinearLayout mLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.tv_cat_title);
            mImageMain = itemView.findViewById(R.id.image_cat);
            mLinearLayout = itemView.findViewById(R.id.linea_main);

        }

        private void settingDetaisl(String strCategory,String strCategoryThumb){
            mTitle.setText(strCategory);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();

            Glide.with(context.getApplicationContext()).load(strCategoryThumb)
                    .into(mImageMain);
        }
    }
}
