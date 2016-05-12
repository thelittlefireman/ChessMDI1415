package jchess.core.visitorsPieces;

import jchess.core.players.Player;
import jchess.core.pieces.implementation.*;

/**
 * Created by thoma on 09/05/2016.
 */
public class VisitorPieces implements VisitorPieceInterface {

    Player player;

    public VisitorPieces(Player pl) {
        this.player = pl;
    }

    private int numberKing;
    private int numberQueen;
    private int numberValet;

    public int getNumberKing() {
        return numberKing;
    }

    public int getNumberQueen() {
        return numberQueen;
    }

    public int getNumberValet() {
        return numberValet;
    }

    public int getNumberKnight() {
        return numberKnight;
    }

    public int getNumberBishop() {
        return numberBishop;
    }

    public int getNumberPawn() {
        return numberPawn;
    }

    public int getNumberRook() {
        return numberRook;
    }

    private int numberKnight;
    private int numberBishop;
    private int numberPawn;
    private int numberRook;

    @Override
    public void visit(King king) {
        if (king.getPlayer().getName().equals(player.getName())) {
            numberKing++;
        }
    }

    @Override
    public void visit(Queen queen) {
        if (queen.getPlayer().getName().equals(player.getName())) {
            numberQueen++;
        }
    }

    @Override
    public void visit(Valet valet) {

        if (valet.getPlayer().getName().equals(player.getName())) {
            numberValet++;
        }
    }

    @Override
    public void visit(Knight knight) {

        if (knight.getPlayer().getName().equals(player.getName())) {
            numberKnight++;
        }
    }

    @Override
    public void visit(Bishop bishop) {

        if (bishop.getPlayer().getName().equals(player.getName())) {
            numberBishop++;
        }
    }

    @Override
    public void visit(Pawn pawn) {
        if (pawn.getPlayer().getName().equals(player.getName())) {
            numberPawn++;
        }
    }

    @Override
    public void visit(Rook rook) {
        if (rook.getPlayer().getName().equals(player.getName())) {
            numberRook++;
        }
    }

    /**
     *
     * @return
     */
    public String  getNumberPiecesM1() {
        String rst=""+this.player.getName();
        rst+="Bishop : "+ this.getNumberBishop()*3;
        rst+="King : "+this.getNumberKing()*1000;
        rst+="Knight : "+ this.getNumberKnight()*3;
        rst+="Pawn : "+ this.getNumberPawn()*1;
        rst+="Queen : "+ this.getNumberQueen()*10;
        rst+="Rook : "+ this.getNumberRook()*5;
        rst+="Valet : "+ this.getNumberValet()*3;
        return rst;
    }

    /**
     * pion : 1, fou/cavalier : 3, tour : 5, reine : 10, roi : 1000).
     * @param pl : joueur
     * @return
     */
    public int getScorePiecesM2() {
        int score =0;
        score+= this.getNumberBishop()*3;
        score+=this.getNumberKing()*1000;
        score+= this.getNumberKnight()*3;
        score+= this.getNumberPawn()*1;
        score+= this.getNumberQueen()*10;
        score+= this.getNumberRook()*5;
        score+= this.getNumberValet()*3;
        return score;
    }
}
