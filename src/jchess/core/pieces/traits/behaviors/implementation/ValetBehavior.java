package jchess.core.pieces.traits.behaviors.implementation;

import jchess.core.Square;
import jchess.core.pieces.Piece;
import jchess.core.pieces.traits.behaviors.Behavior;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by thoma on 08/05/2016.
 */
public class ValetBehavior extends Behavior {
    public ValetBehavior(Piece piece) {
        super(piece);
    }

    /**
     * Annotation to superclass Piece changing pawns location
     * // knight all movesManager<br/>
     * //  _______________ Y:<br/>
     * // |_|_|_|_|_|_|_|_|0<br/>
     * // |_|_|_|_|_|_|_|_|1<br/>
     * // |_|_|_|_|_|_|_|_|2<br/>
     * // |_|_|_|1|_|_|_|_|3<br/>
     * // |_|_|3|V|4|_|_|_|4<br/>
     * // |_|_|_|2|_|_|_|_|5<br/>
     * // |_|_|_|_|_|_|_|_|6<br/>
     * // |_|_|_|_|_|_|_|_|7<br/>
     * //X:0 1 2 3 4 5 6 7
     * //
     *
     * @return ArrayList with new possition of pawn
     */
    @Override
    public Set<Square> getSquaresInRange() {
        Set<Square> list = new HashSet<>();
        Square[][] squares = piece.getChessboard().getSquares();

        int pozX = piece.getSquare().getPozX();
        int pozY = piece.getSquare().getPozY();

        //TODO cavalier deplacement
        int[][] squaresInRange = {
                {pozX - 1, pozY}, //1
                {pozX + 1, pozY}, //2
                {pozX , pozY - 1}, //3
                {pozX, pozY + 1}, //4
        };

        for (int[] squareCoordinates : squaresInRange) {
            int x = squareCoordinates[0];
            int y = squareCoordinates[1];
            if (!piece.isOut(x, y)) {
                list.add(squares[x][y]);
            }
        }
        return list;
    }

}
