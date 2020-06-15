package com.veen.myrecipe;

import java.util.Date;

public class FavsList extends RecipeId{
    private String idMeal;
    private Date timeStamp;

    public FavsList() {
    }

    public FavsList(String idMeal, Date timeStamp) {
        this.idMeal = idMeal;
        this.timeStamp = timeStamp;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
