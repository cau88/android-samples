package edu.dartmouth.cs.maptest;

import java.util.ArrayList;

public class EventEntry {

    public String title = "";
    public String food_type = "";
    public String location = "";
    public String address = "";
    public String amt = "";

    public EventEntry(){};

    public EventEntry(String title, String food_type, String location, String address, String amt){
        this.title = title;
        this.food_type = food_type;
        this.location = location;
        this.address = address;
        this.amt = amt;
    }

    public ArrayList<String> getEvent() {
        return new ArrayList<String>() {{
            add(title);
            add(food_type);
            add(location);
            add(address);
            add(amt);
        }};
    }
}

