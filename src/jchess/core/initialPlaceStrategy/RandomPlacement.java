package jchess.core.initialPlaceStrategy;

import jchess.core.Chessboard;
import jchess.core.Colors;
import jchess.core.players.Player;
import jchess.core.pieces.implementation.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 08/05/2016.
 */
public class RandomPlacement extends InitialPlaceStrategy {
    private static final Logger LOG = Logger.getLogger(RandomPlacement.class);
    private List<Integer> alreadyUsedX;
    private List<Integer> alreadyUsedY;
    public RandomPlacement (Chessboard chessboard){
        super(chessboard);
    }
    @Override
    public void setPieces(String places) {
        alreadyUsedX = new ArrayList<>();
        alreadyUsedY = new ArrayList<>();
        setFigures4NewGame(getChessboard().getSettings().getPlayerWhite());
        setFigures4NewGame(getChessboard().getSettings().getPlayerBlack());
    }

    private List<Integer> getXYRandom(){

        int x = (int) (Math.random()*this.getChessboard().getSettings().getSizeN());
        while(alreadyUsedX.contains(x)){
            x = (int) (Math.random()*this.getChessboard().getSettings().getSizeN());
        }
        int y = (int) (Math.random()*this.getChessboard().getSettings().getSizeN());
        while (alreadyUsedY.contains(y)){
            y = (int) (Math.random()*this.getChessboard().getSettings().getSizeN());
        }

        List<Integer> returnXY = new ArrayList<>();
        returnXY.add(x);
        returnXY.add(y);
        return returnXY;

    }

    /**
     * Method to set Figures in row (and set Queen and King to right position)
     *s
     * @param player which is owner of pawns
     */
    private void setFigures4NewGame( Player player) {

        this.getChessboard().getSquare(getXYRandom().get(0), getXYRandom().get(1)).setPiece(new Rook(this.getChessboard(), player));
        this.getChessboard().getSquare(getXYRandom().get(0), getXYRandom().get(1)).setPiece(new Rook(this.getChessboard(), player));
        this.getChessboard().getSquare(getXYRandom().get(0), getXYRandom().get(1)).setPiece(new Knight(this.getChessboard(), player));
        this.getChessboard().getSquare(getXYRandom().get(0), getXYRandom().get(1)).setPiece(new Knight(this.getChessboard(), player));
        this.getChessboard().getSquare(getXYRandom().get(0), getXYRandom().get(1)).setPiece(new Bishop(this.getChessboard(), player));
        this.getChessboard().getSquare(getXYRandom().get(0), getXYRandom().get(1)).setPiece(new Bishop(this.getChessboard(), player));
        for (int x = 0; x < 8; x++) {
            this.getChessboard().getSquare(getXYRandom().get(0), getXYRandom().get(1)).setPiece(new Pawn(this.getChessboard(), player));
        }

        this.getChessboard().getSquare(getXYRandom().get(0), getXYRandom().get(1)).setPiece(new Queen(this.getChessboard(), player));
        if (player.getColor() == Colors.WHITE) {
            this.getChessboard().setKingWhite(new King(this.getChessboard(), player));
            this.getChessboard().getSquare(getXYRandom().get(0), getXYRandom().get(1)).setPiece(this.getChessboard().getKingWhite());
        } else {
            this.getChessboard().setKingBlack(new King(this.getChessboard(), player));
            this.getChessboard().getSquare(getXYRandom().get(0), getXYRandom().get(1)).setPiece(this.getChessboard().getKingBlack());
        }
    }

}
