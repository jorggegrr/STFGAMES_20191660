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
import java.util.UUID;

// PuzzleActivity.java
public class PuzzleActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;
    // Referencia al Firestore para guardar el estado del juego
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private int gridSize = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        imageView = findViewById(R.id.imagePreview);
        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        // Establece un solo listener que maneje ambas acciones
        btnUploadImage.setOnClickListener(view -> {
            openImageSelector();
        });
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
            imageView.setImageURI(imageUri);
            // Llamar a la función de carga una vez que la imagen es seleccionada y mostrada.
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // Asegúrate de que imageUri no es nulo antes de proceder
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://stf-puzzle-simplified.appspot.com");
            StorageReference puzzleImagesRef = storageRef.child("puzzles/" + UUID.randomUUID().toString());

            puzzleImagesRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> puzzleImagesRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Aquí tienes la URL de descarga, ahora carga la imagen
                        loadImageIntoPuzzle(downloadUri);
                    }))
                    .addOnFailureListener(exception -> {
                        // Informar al usuario del fallo de la subida
                        Toast.makeText(PuzzleActivity.this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
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
        ArrayList<Bitmap> puzzlePieces = new ArrayList<>(); // Lista para almacenar los bitmaps de las piezas
        ArrayList<Bitmap> OriginalImagePieces = new ArrayList<>(); // Lista para mantener un registro del orden original de las piezas

        // Cortar la imagen en piezas individuales
        int yCoord = 0;
        for (int row = 0; row < gridSize; row++) {
            int xCoord = 0;
            for (int col = 0; col < gridSize; col++) {
                Bitmap pieceBitmap = Bitmap.createBitmap(image, xCoord, yCoord, pieceWidth, pieceHeight);
                puzzlePieces.add(pieceBitmap);
                xCoord += pieceWidth;
            }
            yCoord += pieceHeight;
        }

        // Remover la última pieza y agregar un marcador `null` para representar el espacio vacío en el rompecabezas
        Bitmap lastPiece = puzzlePieces.remove(piecesNumber - 1);
        puzzlePieces.add(null); // El espacio vacío en el rompecabezas
        OriginalImagePieces.addAll(puzzlePieces); // Guardar el orden original de las piezas
        OriginalImagePieces.set(OriginalImagePieces.size() - 1, lastPiece); // Añadir la última pieza en su posición correcta

        // Establecer el adaptador del GridView con las piezas d    el rompecabezas
        GridView gridView = findViewById(R.id.puzzleGridView);
        gridView.setAdapter(new PuzzleAdapter(this, puzzlePieces, pieceWidth, pieceHeight));
    }


    private void saveImageUrlToFirestore(String imageUrl) {
        // Asumiendo que tienes una colección 'puzzle' y cada juego tiene una 'imageUrl' y 'gameState'
        String gameId = UUID.randomUUID().toString(); // Genera un ID único para el juego
        Game game = new Game(imageUrl, new GameState()); // Crea una nueva instancia de tu juego con el estado inicial
        firestore.collection("puzzle").document(gameId)
                .set(game)
                .addOnSuccessListener(aVoid -> {
                    startGame(gameId); // Inicia el juego después de guardar la imagen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PuzzleActivity.this, "Failed to save game: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void startGame(String gameId) {
        // Aquí deberías iniciar la lógica de tu juego, pasando el ID del juego si es necesario
        Toast.makeText(this, "Game started with ID: " + gameId, Toast.LENGTH_SHORT).show();
        // Puedes iniciar una nueva Activity o cambiar el fragmento, etc.
    }

    // Clase representando el estado del juego (modifica según sea necesario)
    class GameState {
        // Variables de estado del juego, por ejemplo, puntaje, nivel, etc.
    }

    // Clase representando un juego (modifica según sea necesario)
    class Game {
        String imageUrl;
        GameState gameState;

        public Game(String imageUrl, GameState gameState) {
            this.imageUrl = imageUrl;
            this.gameState = gameState;
        }
    }
}
