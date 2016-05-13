package jchess.core.utils.timePerStroke;

/**
 * Created by thoma on 12/05/2016.
 */
public abstract class TimePerStrokeSaveWithOption extends TimePerStrokeSave{
    TimePerStrokeSave timePerStrokeSave;

    public TimePerStrokeSaveWithOption(TimePerStrokeSave timePerStrokeSave) {
        super(timePerStrokeSave.getTime());
    }

    public String getTimeStrokeInformation(){
        return timePerStrokeSave.getTimeStrokeInformation();
    }
}
