package jchess.core.players.ia;

import jchess.core.Chessboard;
import jchess.core.GameEngine;
import jchess.core.Square;
import jchess.core.commands.MoveCommands;
import jchess.core.pieces.Piece;
import jchess.core.visitorsPieces.VisitorPieceInterface;
import jchess.core.visitorsPieces.VisitorPieces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 09/05/2016.
 */
public class MinMaxIA extends IAInterface {
    public MinMaxIA(GameEngine gameEngine, String name, String color) {
        super(name, color, playerTypes.computer);
        this.gameEngine = gameEngine;
    }

    @Override
    public void playATurn() {
        List<Piece> listPieceToMove = this.gameEngine.getChessboard().getAllPieces(this.getColor());
        int max_val = Short.MAX_VALUE;
        Square begin = null;
        MoveCommands bestMoveCommands=null;
        //pour tous les coups possible
        for (Piece piece : listPieceToMove) {
            begin = piece.getSquare();
            List<Square> possiblity = new ArrayList<>(piece.getAllMoves());
            for (Square end : possiblity) {
                MoveCommands moveCommands = new MoveCommands(begin, end);
                this.gameEngine.getCommandsManager().execute(moveCommands);

                int val = min(3);
                if (val > max_val) {
                    max_val = val;
                    bestMoveCommands = moveCommands;
                }

                this.gameEngine.getCommandsManager().undo(false);
            }
        }
        this.gameEngine.getCommandsManager().execute(bestMoveCommands);
    }

    public int min(int profondeur) {
        if (profondeur == 0 || this.gameEngine.isBlockedChessboard()) {
            return eval();
        }
        int min_val = Integer.MAX_VALUE;
        List<Piece> listPieceToMove = this.gameEngine.getChessboard().getAllPieces(this.getColor());
        for (Piece piece : listPieceToMove) {
            Square begin = piece.getSquare();
            List<Square> possiblity = new ArrayList<>(piece.getAllMoves());
            for (Square end : possiblity) {
                MoveCommands moveCommands = new MoveCommands(begin, end);
                this.gameEngine.getCommandsManager().execute(moveCommands);

                int val = max(profondeur - 1);

                if (val < min_val) {
                    min_val = val;
                }

                this.gameEngine.getCommandsManager().undo(false);
            }

        }
        return min_val;
    }


    public int max(int profondeur) {
        if (profondeur == 0 || this.gameEngine.isBlockedChessboard()) {
            return eval();
        }
        int max_val = -Integer.MAX_VALUE;
        List<Piece> listPieceToMove = this.gameEngine.getChessboard().getAllPieces(this.getColor());
        for (Piece piece : listPieceToMove) {
            Square begin = piece.getSquare();
            List<Square> possiblity = new ArrayList<>(piece.getAllMoves());
            for (Square end : possiblity) {
                MoveCommands moveCommands = new MoveCommands(begin, end);
                this.gameEngine.getCommandsManager().execute(moveCommands);

                int val = min(profondeur - 1);

                if (val > max_val) {
                    max_val = val;
                }

                this.gameEngine.getCommandsManager().undo(false);
            }

        }
        return max_val;
    }

    public int eval() {
        int nbPions = this.gameEngine.getChessboard().getAllPieces(this.getColor()).size() + this.gameEngine.getChessboard().getAllPieces(this.gameEngine.getSettings().getPlayerBlack().getColor()).size();
        ;
        if (this.gameEngine.isBlockedChessboard()) {
            if (!this.isLoose()) {
                return 10000 - nbPions;
            } else if (this.isLoose()) {
                return -10000 + nbPions;
            } else {
                return 0;
            }
        }
        VisitorPieces visitorPieces1 = new VisitorPieces(this);
        this.gameEngine.getChessboard().accept(visitorPieces1);

        int nbPointThis = visitorPieces1.getScorePiecesM2();
        VisitorPieces visitorPieces2 = new VisitorPieces(this.gameEngine.getSettings().getPlayerBlack());
        this.gameEngine.getChessboard().accept(visitorPieces2);


        int nbPointOther = visitorPieces2.getScorePiecesM2();

        return nbPointThis
                - nbPointOther;
    }


}
