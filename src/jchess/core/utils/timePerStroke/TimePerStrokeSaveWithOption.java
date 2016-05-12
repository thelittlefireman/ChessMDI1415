package jchess.core.utils.timePerStroke;

/**
 * Created by thoma on 12/05/2016.
 */
public abstract class TimePerStrokeSaveWithOption extends TimePerStrokeSave{
    TimePerStrokeSave timePerStrokeSave;

    public TimePerStrokeSaveWithOption(int time) {
        super(time);
    }

    public String getTimeStrokeInformation(){
        return timePerStrokeSave.getTimeStrokeInformation();
    }
}
