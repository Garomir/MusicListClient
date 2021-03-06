package ru.ramich.musiclist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Song {

    @SerializedName("id")//если имя переменной не совпадает с тем, что в json
    @Expose//разрешать или запрещать serialize and deserialize данного поля
    private int id;
    @SerializedName("name")
    @Expose
    private String name;

    public Song(){}

    public Song(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
