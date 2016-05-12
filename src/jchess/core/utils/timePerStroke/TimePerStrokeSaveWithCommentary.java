package jchess.core.utils.timePerStroke;

/**
 * Created by thoma on 12/05/2016.
 */
public class TimePerStrokeSaveWithCommentary extends TimePerStrokeSaveWithOption{
    TimePerStrokeSave timePerStrokeSave;
    String commentary;
    public TimePerStrokeSaveWithCommentary(TimePerStrokeSave timePerStrokeSave, String commentary){
        super(timePerStrokeSave.getTime());
        this.timePerStrokeSave=timePerStrokeSave;
        this.commentary =commentary;
    }
    @Override
    public String getTimeStrokeInformation(){
        return timePerStrokeSave.getTimeStrokeInformation()+ " Commentaire : "+this.commentary;
    }
}
