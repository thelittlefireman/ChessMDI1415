package jchess.core.commands;

/**
 * Created by thoma on 09/05/2016.
 */
public interface CommandInterface {
    public void execute();
    public void undo();
    public void redo();
}
