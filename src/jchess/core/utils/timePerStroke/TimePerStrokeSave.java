package jchess.core.utils.timePerStroke;

/**
 * Created by thoma on 12/05/2016.
 */
public class TimePerStrokeSave {

    public TimePerStrokeSave(int time){
        this.time =time;
    }

    public int getTime() {
        return time;
    }

    private int time;

    public String getTimeStrokeInformation(){
        return this.time+" sec";
    }
}
