package jchess.core.players.ia;

import jchess.core.Chessboard;
import jchess.core.GameEngine;
import jchess.core.Square;
import jchess.core.commands.MoveCommands;
import jchess.core.commands.MoveCommandsBuilder;
import jchess.core.pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by thoma on 09/05/2016.
 */
public class RandomIA extends IAInterface {
    public RandomIA(GameEngine gameEngine, String name, String color) {
        super(name, color, playerTypes.computer);
        this.gameEngine = gameEngine;
    }

    @Override
    public void playATurn() {
        Piece pieceToMove = this.gameEngine.getChessboard().getAllPieces(this.getColor()).get(new Random().nextInt(this.gameEngine.getChessboard().getAllPieces(this.getColor()).size()) );
        List<Square> possiblity = new ArrayList<>(pieceToMove.getAllMoves());
        while (possiblity.size() == 0) {
            pieceToMove = this.gameEngine.getChessboard().getAllPieces(this.getColor()).get( new Random().nextInt(this.gameEngine.getChessboard().getAllPieces(this.getColor()).size()));
            possiblity = new ArrayList<>(pieceToMove.getAllMoves());
        }
        Square end = possiblity.get(new Random().nextInt(possiblity.size()));
        this.gameEngine.getCommandsManager().execute(new MoveCommands(pieceToMove.getSquare(), end));
        this.gameEngine.nextMove();
    }
}
