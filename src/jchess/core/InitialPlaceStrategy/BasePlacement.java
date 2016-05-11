package jchess.core.initialPlaceStrategy;

import jchess.core.Chessboard;
import jchess.core.Colors;
import jchess.core.players.Player;
import jchess.core.pieces.implementation.*;
import org.apache.log4j.Logger;

/**
 * Created by thoma on 08/05/2016.
 */
public class BasePlacement extends InitialPlaceStrategy {
    private static final Logger LOG = Logger.getLogger(BasePlacement.class);

    public BasePlacement(Chessboard chessboard) {
        super(chessboard);
    }


    /**
     * Method setPieces on begin of new game or loaded game
     *
     * @param places  string with pieces to set on chessboard
     */
    @Override
    public void setPieces(String places) {

        if (places.equals("")) //if newGame
        {
            this.setPieces4NewGame( getChessboard().getSettings().getPlayerWhite(), getChessboard().getSettings().getPlayerBlack());
        } else //if loadedGame
        {
            return;
        }
    }/*--endOf-setPieces--*/

    /**
     *
     */
    private void setPieces4NewGame(Player plWhite, Player plBlack) {
        /* WHITE PIECES */
        Player player = plBlack;
        Player player1 = plWhite;
        this.setFigures4NewGame(0, player);
        this.setPawns4NewGame(1, player);
        this.setFigures4NewGame(getChessboard().getSettings().getSizeM() - 1, player1);
        this.setPawns4NewGame(getChessboard().getSettings().getSizeM() - 2, player1);
    }/*--endOf-setPieces(boolean upsideDown)--*/

    /**
     * Method to set Figures in row (and set Queen and King to right position)
     *
     * @param i      row where to set figures (Rook, Knight etc.)
     * @param player which is owner of pawns
     */
    private void setFigures4NewGame(int i, Player player) {
        if (i != 0 && i != this.getChessboard().getSettings().getSizeM()-1) {
            LOG.error("error setting figures like rook etc.");
            return;
        } else if (i == 0) {
            player.setGoDown(true);
        }

        this.getChessboard().getSquare(0, i).setPiece(new Rook(this.getChessboard(), player));
        this.getChessboard().getSquare(7, i).setPiece(new Rook(this.getChessboard(), player));
        this.getChessboard().getSquare(1, i).setPiece(new Knight(this.getChessboard(), player));
        this.getChessboard().getSquare(6, i).setPiece(new Knight(this.getChessboard(), player));
        this.getChessboard().getSquare(2, i).setPiece(new Bishop(this.getChessboard(), player));
        this.getChessboard().getSquare(5, i).setPiece(new Bishop(this.getChessboard(), player));


        this.getChessboard().getSquare(3, i).setPiece(new Queen(this.getChessboard(), player));
        if (player.getColor() == Colors.WHITE) {
            this.getChessboard().setKingWhite(new King(this.getChessboard(), player));
            this.getChessboard().getSquare(4, i).setPiece(this.getChessboard().getKingWhite());
        } else {
            this.getChessboard().setKingBlack(new King(this.getChessboard(), player));
            this.getChessboard().getSquare(4, i).setPiece(this.getChessboard().getKingBlack());
        }
    }

    /**
     * method set Pawns in row
     *
     * @param i      row where to set pawns
     * @param player player which is owner of pawns
     */
    private void setPawns4NewGame(int i, Player player) {
        if (i != 1 && i != 6) {
            LOG.error("error setting pawns etc.");
            return;
        }
        for (int x = 0; x < 8; x++) {
            this.getChessboard().getSquare(x, i).setPiece(new Pawn(this.getChessboard(), player));
        }
    }


}
