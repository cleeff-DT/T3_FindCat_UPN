package com.example.gato.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gato.R;
import com.example.gato.clases.Hash;
import com.example.gato.clases.Usuario;
import com.example.gato.sqlite.Gatos;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class SesionActivity extends AppCompatActivity implements View.OnClickListener {

    private static String urlIniciarSesion = "http://gatitos.atwebpages.com/services/iniciarSesion.php";

    EditText txtCorreo, txtClave;
    Button btnIngresar, btnSalir, btnRegistrate;
    CheckBox chkRecordar;
    TextView lblOlvidoC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtCorreo = findViewById(R.id.logTxtUsuario);
        txtClave = findViewById(R.id.logTxtContra);
        btnIngresar = findViewById(R.id.logBtnIngresar);
        btnRegistrate = findViewById(R.id.logBtnRegistrate);
        btnSalir = findViewById(R.id.logBtnSalir);
        chkRecordar = findViewById(R.id.logChxRecordar);
        lblOlvidoC = findViewById(R.id.logLblOlvidoC);

        btnIngresar.setOnClickListener((this));
        btnSalir.setOnClickListener(this);
        btnRegistrate.setOnClickListener(this);
        lblOlvidoC .setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logBtnIngresar)
            iniciarSesion(txtCorreo.getText().toString(), txtClave.getText().toString());
        else if (view.getId() == R.id.logBtnSalir)
            salir();
        else if (view.getId() == R.id.logBtnRegistrate)
            registrar();
        else if (view.getId() == R.id.logLblOlvidoC)
            OlvidarContraseña();
    }

    private void OlvidarContraseña() {
        Intent rcpContra = new Intent(this, RcpContraActivity.class);
        startActivity(rcpContra);
        finish();
    }

    private void registrar() {
        Intent registro = new Intent(this, RegistroActivity.class);
        startActivity(registro);
        finish();
    }

    private void iniciarSesion(String txtCorreo, String txtClave) {

        AsyncHttpClient ahcIniciarSesion = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        Gatos bv = new Gatos(getApplicationContext());
        Hash hash = new Hash();
        txtClave = hash.StringToHash(txtClave, "SHA256").toLowerCase();
        params.put("correo",txtCorreo);
        params.put("clave",txtClave);

        //validar en una base de datos
        ahcIniciarSesion.post(urlIniciarSesion, params, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                if(statusCode == 200){
                    try {
                        JSONArray jsonArray = new JSONArray(rawJsonResponse);
                        if(jsonArray.length() > 0){
                            int id = jsonArray.getJSONObject(0).getInt("id_persona");
                            if(id == -1){
                                Toast.makeText(getApplicationContext(), "Credenciales incorrectas: ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Usuario usuario = new Usuario();
                                usuario.setId(id);
                                usuario.setDni(jsonArray.getJSONObject(0).getString("dni"));
                                usuario.setNombre(jsonArray.getJSONObject(0).getString("nombre"));
                                usuario.setApellidos(jsonArray.getJSONObject(0).getString("apellidos"));
                                usuario.setCelular(jsonArray.getJSONObject(0).getString("celular").charAt(0));
                                usuario.setCorreo(jsonArray.getJSONObject(0).getString("correo"));
                                usuario.setClave(jsonArray.getJSONObject(0).getString("clave"));
                                usuario.setId_distrito(jsonArray.getJSONObject(0).getInt("id_distrito"));

                                if(chkRecordar.isChecked())
                                    bv.agregarUsuario(usuario.getId(), usuario.getCorreo(), usuario.getClave());

                                Intent bienvenida = new Intent(getApplicationContext(), BienvenidaActivity.class);
                                bienvenida.putExtra("usuario", usuario);
                                startActivity(bienvenida);
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                Toast.makeText(getApplicationContext(),"Error: "+statusCode, Toast.LENGTH_LONG).show();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }
    private void salir() {
        System.exit(1);
    }

}