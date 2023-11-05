package com.example.stfgames;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PuzzleGameState {
    public String imageUri; // URI de la imagen subida.
    public ArrayList<String> pieceLocations; // Ubicaciones de las piezas.
    public String lastUpdated; // Timestamp de la última actualización.

    // Constructor por defecto necesario para Firebase
    public PuzzleGameState() {}

    // Constructor para instanciar un objeto de estado del rompecabezas.
    public PuzzleGameState(String imageUri, ArrayList<String> pieceLocations) {
        this.imageUri = imageUri;
        this.pieceLocations = pieceLocations;
        this.lastUpdated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    // Getters y setters
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public ArrayList<String> getPieceLocations() {
        return pieceLocations;
    }

    public void setPieceLocations(ArrayList<String> pieceLocations) {
        this.pieceLocations = pieceLocations;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
