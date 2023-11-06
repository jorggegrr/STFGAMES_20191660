package com.example.stfgames;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class PuzzleActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private int gridSize = 3;
    private ArrayList<Bitmap> originalImagePieces;
    private ArrayList<Bitmap> puzzlePieces;
    private GridView gridView;
    private PuzzleAdapter puzzleAdapter;
    private Button btnStartGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        gridView = findViewById(R.id.puzzleGridView);
        btnStartGame = findViewById(R.id.btnStartGame);
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(v -> openImageSelector());


        btnStartGame.setOnClickListener(v -> iniciarOReiniciarJuego());

        originalImagePieces = new ArrayList<>();
        puzzlePieces = new ArrayList<>();
        puzzleAdapter = new PuzzleAdapter(this, puzzlePieces, 0, 0);
    }


    private void iniciarOReiniciarJuego() {
        if (puzzlePieces.isEmpty()) {
            Toast.makeText(this, "Por favor, carga una imagen primero.", Toast.LENGTH_SHORT).show();
            return;
        }
        mezclarPuzzle();
        btnStartGame.setText("Reiniciar Juego");
        puzzleAdapter.setPuzzlePieces(puzzlePieces);
        puzzleAdapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    private void mezclarPuzzle() {
        // Asegúrate de implementar aquí la lógica para mezclar las piezas
        // Deja el último elemento (el vacío) en su lugar
        Collections.shuffle(puzzlePieces.subList(0, puzzlePieces.size() - 1));
        puzzleAdapter.notifyDataSetChanged(); // Avisar al adaptador que los datos han cambiado
        verificarVictoria(); // Para el caso en que el azar ponga todas las piezas en orden
    }

    private void verificarVictoria() {
        // Verificar si el rompecabezas está resuelto
        if (originalImagePieces.equals(puzzlePieces)) {
            Toast.makeText(this, "Se culminó el juego", Toast.LENGTH_LONG).show();
            // Para reiniciar el juego o cerrar la actividad puedes llamar aquí a otro método o usar finish()
        }
    }

    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }


    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference storageRef = storage.getReferenceFromUrl("gs://stf-puzzle-simplified.appspot.com");
            StorageReference puzzleImagesRef = storageRef.child("puzzles/" + UUID.randomUUID().toString());

            puzzleImagesRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> puzzleImagesRef.getDownloadUrl().addOnSuccessListener(this::loadImageIntoPuzzle))
                    .addOnFailureListener(exception -> Toast.makeText(PuzzleActivity.this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void loadImageIntoPuzzle(Uri downloadUri) {
        Glide.with(this)
                .asBitmap()
                .load(downloadUri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        crearPiezasPuzzle(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void crearPiezasPuzzle(Bitmap image) {
        int piecesNumber = gridSize * gridSize;
        int pieceWidth = image.getWidth() / gridSize;
        int pieceHeight = image.getHeight() / gridSize;

        originalImagePieces.clear();
        puzzlePieces.clear(); // Asegúrate de que estás limpiando la lista de instancia, no una local

        int yCoord = 0;
        for (int row = 0; row < gridSize; row++) {
            int xCoord = 0;
            for (int col = 0; col < gridSize; col++) {
                Bitmap pieceBitmap = Bitmap.createBitmap(image, xCoord, yCoord, pieceWidth, pieceHeight);
                puzzlePieces.add(pieceBitmap); // Agregas a la lista de instancia
                originalImagePieces.add(pieceBitmap);
                xCoord += pieceWidth;
            }
            yCoord += pieceHeight;
        }

        puzzlePieces.set(piecesNumber - 1, null); // El espacio vacío en el rompecabezas

        saveOriginalStateToFirestore(originalImagePieces);

        gridView.setAdapter(new PuzzleAdapter(this, puzzlePieces, pieceWidth, pieceHeight));
        iniciarOReiniciarJuego();
    }

    private void saveOriginalStateToFirestore(ArrayList<Bitmap> originalImagePieces) {
          }

    private void startGame(String gameId) {
        Toast.makeText(this, "Game started with ID: " + gameId, Toast.LENGTH_SHORT).show();
       }

    // Clases GameState y Game deben implementarse de acuerdo a la lógica de tu juego
    class GameState {

    }

    class Game {
        String imageUrl;
        GameState gameState;

        public Game(String imageUrl, GameState gameState) {
            this.imageUrl = imageUrl;
            this.gameState = gameState;
        }

    }

}
