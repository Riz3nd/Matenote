package com.rizend.matenote.model;

import com.google.firebase.firestore.PropertyName;

public class Note {
    private String titleNote;
    private String dataNote;

    public Note() {
    }

    public Note(String titleNote, String dataNote) {
        this.titleNote = titleNote;
        this.dataNote = dataNote;
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
}
