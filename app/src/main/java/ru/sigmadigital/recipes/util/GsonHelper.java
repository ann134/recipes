package ru.sigmadigital.recipes.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

    public static Gson getGson(){
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("MMM dd, yyyy, HH:mm:ss a");
        return  builder.create();
        //2019-01-09T00:25:53.000+0000
    }
}