package com.example.ui;

public class ListViewBtnItem {

    private String nameStr;
    private int id;

    public Integer getID(){return id;};

    public void setName(String name){
        this.nameStr= name;
    }

    public void setID(int id){
        this.id = id;
    }

    public String getName(){
        return nameStr;
    }

}

