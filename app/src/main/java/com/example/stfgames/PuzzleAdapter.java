package com.example.stfgames;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;

public class PuzzleAdapter extends BaseAdapter {
        private int gridSize = 3;
        private Context context;
        private ArrayList<Bitmap> puzzlePieces;
        private int pieceWidth, pieceHeight;

        public PuzzleAdapter(Context context, ArrayList<Bitmap> puzzlePieces, int pieceWidth, int pieceHeight) {
            this.context = context;
            this.puzzlePieces = puzzlePieces;
            this.pieceWidth = pieceWidth;
            this.pieceHeight = pieceHeight;
        }

        @Override
        public int getCount() {
            return puzzlePieces.size();
        }

        @Override
        public Object getItem(int position) {
            return puzzlePieces.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(pieceWidth, pieceHeight));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setPadding(0, 0, 0, 0);
            imageView.setImageBitmap(puzzlePieces.get(position));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Encontrar la posición del espacio vacío
                    int emptySpaceIndex = puzzlePieces.indexOf(null);
                    // Comprobar si la pieza actual está al lado del espacio vacío
                    if (isAdjacent(position, emptySpaceIndex)) {
                        // Intercambiar piezas
                        Collections.swap(puzzlePieces, position, emptySpaceIndex);
                        // Notificar al adaptador que los datos han cambiado para que actualice la UI
                        notifyDataSetChanged();
                        // Puedes agregar aquí más lógica si necesitas, por ejemplo, comprobar si el juego ha terminado
                    }
                }
            });

            return imageView;
        }

        // Método para comprobar si dos piezas están al lado una de la otra
        private boolean isAdjacent(int position, int emptySpaceIndex) {
            // Calcular la fila y columna basándose en la posición
            int row = position / gridSize;
            int col = position % gridSize;
            int emptyRow = emptySpaceIndex / gridSize;
            int emptyCol = emptySpaceIndex % gridSize;

            // Comprobar si la pieza está al lado del espacio vacío (arriba, abajo, izquierda, derecha)
            return (row == emptyRow && Math.abs(col - emptyCol) == 1) ||
                    (col == emptyCol && Math.abs(row - emptyRow) == 1);
        }

        // Método para actualizar las piezas del rompecabezas
        public void setPuzzlePieces(ArrayList<Bitmap> puzzlePieces) {
            this.puzzlePieces = puzzlePieces;
            notifyDataSetChanged(); // Esto notificará al adaptador que los datos han cambiado
        }

        // Método para actualizar los tamaños de las piezas
        public void setPieceSize(int pieceWidth, int pieceHeight) {
            this.pieceWidth = pieceWidth;
            this.pieceHeight = pieceHeight;
            notifyDataSetChanged(); // Esto notificará al adaptador que los datos han cambiado
        }
    }
