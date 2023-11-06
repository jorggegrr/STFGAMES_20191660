package com.example.stfgames;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
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

public class MemoryGameActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int MAX_IMAGES = 15;
    private ArrayList<Uri> imageUris;
    private ArrayAdapter<Uri> adapter;
    private GridView imagesGridView;
    private Button addButton;
    private TextView countText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        imagesGridView = findViewById(R.id.imagesGridView);
        addButton = findViewById(R.id.addButton);
        countText = findViewById(R.id.countText);
        imageUris = new ArrayList<>();

        adapter = new ArrayAdapter<Uri>(this, R.layout.image_item, R.id.imageItem, imageUris);
        imagesGridView.setAdapter(adapter);

        // Actualizar la cantidad de imágenes seleccionadas
        updateCount();

        // Agregar la lógica para el botón "Agregar imágenes"
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUris.size() < MAX_IMAGES) {
                    openGallery();
                } else {
                    Toast.makeText(MemoryGameActivity.this, "Se ha alcanzado el máximo de imágenes.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el GridView para manejar el clic en el botón de eliminar
        imagesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imageUris.remove(position);
                adapter.notifyDataSetChanged();
                updateCount();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            if(data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for(int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    if (!imageUris.contains(imageUri) && imageUris.size() < MAX_IMAGES) {
                        imageUris.add(imageUri);
                    }
                }
            } else if(data.getData() != null) {
                Uri imageUri = data.getData();
                if (!imageUris.contains(imageUri)) {
                    imageUris.add(imageUri);
                }
            }
            adapter.notifyDataSetChanged();
            updateCount();
        }
    }

    private void updateCount() {
        countText.setText(String.format("Total de imágenes seleccionadas: %d", imageUris.size()));
    }

}
