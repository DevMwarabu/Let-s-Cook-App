package com.veen.myrecipe;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

/**
 * Created by Mwarabu on 3/19/2018.
 */

public class RecipeId {

    @Exclude
    public String RecipeId;

    public <T extends RecipeId> T withId(@NonNull final String id){
        this.RecipeId = id;
        return  (T) this;
    }

}
