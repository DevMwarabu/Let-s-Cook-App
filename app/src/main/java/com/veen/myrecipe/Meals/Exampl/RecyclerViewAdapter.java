package com.veen.myrecipe.Meals.Exampl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.veen.myrecipe.MealList;
import com.veen.myrecipe.MealListTets;
import com.veen.myrecipe.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    // The unified native ad view type.
    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;
    private Context mContext;

    // The list of Native ads and menu items.
    private final List<MealListTets> mRecyclerViewItems;

    public RecyclerViewAdapter(Context context, List<MealListTets> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;
    }

    @Override
    public int getItemViewType(int position) {

        Object recyclerViewItem = mRecyclerViewItems.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,UnifiedNativeAdView adView) {
        adView.setNativeAd(nativeAd);
    }

    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private ImageView mImageMain;
        private LinearLayout mLinearLayout;

        MenuItemViewHolder(View view) {
            super(view);

            mTitle = view.findViewById(R.id.tv_cat_title);
            mImageMain = view.findViewById(R.id.image_cat);
            mLinearLayout = view.findViewById(R.id.linea_main);
        }


        private void settingDetaisl(String strMeal,String strMealThumb){
            mTitle.setText(strMeal);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();

            Glide.with(mContext.getApplicationContext()).load(strMealThumb)
                    .into(mImageMain);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.ad_unified,
                        viewGroup, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case MENU_ITEM_VIEW_TYPE:
                // Fall through.
            default:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_meal, viewGroup, false);
                mContext = viewGroup.getContext();
                return new MenuItemViewHolder(menuItemLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) mRecyclerViewItems.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;
            case MENU_ITEM_VIEW_TYPE:
                // fall through
            default:
                MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;

                String strMeal = mRecyclerViewItems.get(position).getStrMeal();
                String strMealThumb = mRecyclerViewItems.get(position).getStrMealThumb();
                final String idMeal = mRecyclerViewItems.get(position).getIdMeal();

                menuItemHolder.settingDetaisl(strMeal,strMealThumb);
        }
    }
}
