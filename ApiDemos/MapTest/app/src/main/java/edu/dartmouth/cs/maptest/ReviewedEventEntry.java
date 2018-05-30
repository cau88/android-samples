package edu.dartmouth.cs.maptest;

import java.util.ArrayList;

public class ReviewedEventEntry {

    public String title = "";
    public String amount = "";
    public Integer likes = 0;
    public String supported = "";
    public String category = "Reviewed";

    public ReviewedEventEntry(){};

    public ReviewedEventEntry(String title, String amount, Integer likes, String category){
        this.title = title;
        this.amount = amount;
        if(likes>0)
        { supported = "True";}
        else
        { supported = "False";}
        this.category = category;
    }

    public ArrayList<String> getEvent() {
        return new ArrayList<String>() {{
            add(title);
            add(amount);
            add(supported);
            add(category);
        }};
    }
}

