package com.example.gato.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gato.R;
import com.example.gato.clases.Menu;
import com.example.gato.clases.Usuario;

public class BienvenidaActivity extends AppCompatActivity implements Menu {
    TextView lblSaludo;
    Usuario usuario;
    public static int id_global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bienvenida);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lblSaludo = findViewById(R.id.bieLblSaludo);
        Usuario usuario = (Usuario)getIntent().getSerializableExtra("usuario");
        id_global = usuario.getId();
        lblSaludo.setText("Hola: "+ usuario.getCorreo());
    }

    @Override
    public void onClickMenu(int id) {
        Intent menu = new Intent(this, MenuActivity.class);
        menu.putExtra("id",id);
        menu.putExtra("usuario", usuario);
        startActivity(menu);
        finish();
    }
}