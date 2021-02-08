package ru.ramich.musiclist;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyRetrofit {
    public static Retrofit getRetrofit(){
        return new  Retrofit.Builder()
                .baseUrl("http://garomir.hopto.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
