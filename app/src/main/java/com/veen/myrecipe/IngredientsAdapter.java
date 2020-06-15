package com.veen.myrecipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.veen.myrecipe.Meals.AllIngridients;
import com.veen.myrecipe.Meals.MealsByArea;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
    private List<IngredientList> catsLists;
    private Context context;

    public IngredientsAdapter(List<IngredientList> catsLists) {
        this.catsLists = catsLists;
    }

    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingridient, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final IngredientsAdapter.ViewHolder holder, final int position) {
        final String strIngredient = catsLists.get(position).getStrIngredient();

        final int selectedPosition = holder.getAdapterPosition();



        holder.settingDetaisl(strIngredient);



    }

    @Override
    public int getItemCount() {
        return catsLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mIngredient;
        private CheckBox mCheckBox;
        private LinearLayout mLinearLayout;
        private CheckBox mSelectAll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mIngredient = itemView.findViewById(R.id.tv_ingredient);
            mLinearLayout = itemView.findViewById(R.id.linea_main);
            mCheckBox = itemView.findViewById(R.id.checkbox_main);
            mSelectAll = AllIngridients.mSelectAll;

        }

        private void settingDetaisl(String strIngredient){
            mIngredient.setText(strIngredient);

        }
    }
}
