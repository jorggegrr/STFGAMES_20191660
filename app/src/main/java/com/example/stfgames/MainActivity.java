package com.example.stfgames;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    Button btnStfPuzzleSV, btnStfMemoryClassic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStfPuzzleSV = findViewById(R.id.btnStfPuzzleSV);
        btnStfMemoryClassic = findViewById(R.id.btnStfMemoryClassic);

        btnStfPuzzleSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PuzzleActivity.class);
                startActivity(intent);
            }
        });

        btnStfMemoryClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reemplaza MemoryGameActivity.class con la actividad correcta que tengas para el juego de memoria.
               // Intent intent = new Intent(MainActivity.this, MemoryGameActivity.class);
                //startActivity(intent);
            }
        });
    }
}

