package it.uniba.di.sms.carpooling;

public class NotificaAltervista  {

    private String TokenID;
    private String Note;

    public NotificaAltervista( String TokenID , String Note ) {
        this.TokenID = TokenID;
        this.Note = Note;
    }

    public String getTokenID() {
        return TokenID;
    }

    public void setTokenID(String tokenID) {
        TokenID = tokenID;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    @Override
    public String toString() {
        return Note;
    }
}
