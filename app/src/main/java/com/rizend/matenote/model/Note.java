package com.rizend.matenote.model;

public class Note {
    private String titleNote;
    private String dataNote;
    private String userNote;

    public Note() {
    }

    public Note(String titleNote, String dataNote, String userNote) {
        this.titleNote = titleNote;
        this.dataNote = dataNote;
        this.userNote = userNote;
    }

    public String getTitleNote() {
        return titleNote;
    }

    public void setTitleNote(String titleNote) {
        this.titleNote = titleNote;
    }

    public String getDataNote() {
        return dataNote;
    }

    public void setDataNote(String dataNote) {
        this.dataNote = dataNote;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }
}
