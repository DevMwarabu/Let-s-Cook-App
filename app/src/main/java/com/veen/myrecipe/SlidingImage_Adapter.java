package com.veen.myrecipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.veen.myrecipe.Meals.MealDesc;

import java.util.List;

public class SlidingImage_Adapter extends PagerAdapter {
    private List<MealList> stringUrls;
    private LayoutInflater inflater;
    private Context context;

    public SlidingImage_Adapter(List<MealList> stringUrls, Context context) {
        this.stringUrls = stringUrls;
        this.context = context;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return stringUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.view_pager_item, view, false);

        assert imageLayout != null;
        final ImageView imageView = imageLayout
                .findViewById(R.id.image_main);
        final TextView textView = imageLayout.findViewById(R.id.tv_title);
        final TextView mCount = imageLayout.findViewById(R.id.tv_count);

        int finalPositon = position+1;

        textView.setText(stringUrls.get(position).getStrMeal());
        mCount.setText(""+finalPositon+"/"+stringUrls.size());

        Glide.with(context.getApplicationContext()).load(stringUrls.get(position).getStrMealThumb()).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MealDesc.class);
                intent.putExtra("idMeal",stringUrls.get(position).getIdMeal());
                v.getContext().startActivity(intent);
            }
        });

        view.addView(imageLayout, 0);

        return imageLayout;
    }
}
