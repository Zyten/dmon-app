package xyz.zyten.rdmon;

/**
 * Created by zyten on 13/5/2016.
 */

public class Precaution{

    private static final String TAG = "Precaution";
    private Integer rangeID;
    private Integer healthID;
    private String desc;


    public Precaution(){

    }

    public Precaution(Integer rangeID, Integer healthID, String desc){
        this.rangeID = rangeID;
        this.healthID= healthID;
        this.desc = desc;
    }

    public Integer getRangeID(){
        return rangeID;
    }

    public Integer getHealthID(){
        return healthID;
    }

    public String getPrecaution(){
        return this.desc;
    }
}