package com.example.gato.actividades;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gato.R;
import com.example.gato.clases.Hash;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {

    private static String urlMostrarDistrito = "http://gatitos.atwebpages.com/services/mostrarDistritos.php";
    private static String urlAgregarPersona = "http://gatitos.atwebpages.com/services/agregarPersona.php";
    EditText txtNombre, txtApellidos, txtDni, txtCelular, txtCorreo, txtClave, txtClave2;
    Spinner cboDistritos;
    CheckBox chkTerminos;
    Button btnCrear, btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombre = findViewById(R.id.regTxtNombre);
        txtApellidos = findViewById(R.id.regTxtApellido);
        txtDni = findViewById(R.id.regTxtDni);
        txtCelular = findViewById(R.id.regTxtCelular);
        txtCorreo = findViewById(R.id.regTxtCorreo);
        txtClave = findViewById(R.id.regTxtContra);
        txtClave2 = findViewById(R.id.regTxtContra2);

        cboDistritos = findViewById(R.id.regCboDistrito);
        chkTerminos = findViewById(R.id.regChkTerminos);
        btnCrear = findViewById(R.id.regBtnCrear);
        btnRegresar = findViewById(R.id.regBtnRegresar);

        chkTerminos.setOnClickListener(this);
        btnCrear.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);

        //esto obliga a que se acepte los terminos
        btnCrear.setEnabled(false);

        llenarDistritos();
    }

    private void llenarDistritos() {

        //Crear un objeto para realizar la tarea asincronahacia en web services
        AsyncHttpClient ahcMostrarDistrito = new AsyncHttpClient();
        //crear un adatador por defecto al spiner
        cboDistritos.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{"--Seleccione Distrito--"}));
        //ejecutar ña tarea asincronica
        ahcMostrarDistrito.get(urlMostrarDistrito, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                if (statusCode == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(rawJsonResponse);
                        String[] distritos = new String[jsonArray.length() + 1];
                        distritos[0] = "-- Seleccione Distrito --";
                        for (int i = 1; i < jsonArray.length() + 1; i++) {
                            distritos[i] = jsonArray.getJSONObject(i - 1).getString("nombre_distrito");
                        }
                        cboDistritos.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_spinner_dropdown_item, distritos));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                Toast.makeText(getApplicationContext(), "ERROR: " + statusCode, Toast.LENGTH_LONG).show();
            }
            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.regBtnRegresar)
            regresar();
        else if (view.getId() == R.id.regChkTerminos)
            mostrarTerminos();
        else if (view.getId() == R.id.regBtnCrear)
            validarFormulario();
    }

    private void validarFormulario() {
        String nombre = txtNombre.getText().toString().trim();
        String apellidos = txtApellidos.getText().toString().trim();
        String dni = txtDni.getText().toString().trim();
        String celular = txtCelular.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();
        String clave = txtClave.getText().toString();
        String clave2 = txtClave2.getText().toString();
        int distritoSeleccionado = cboDistritos.getSelectedItemPosition();

        if (nombre.isEmpty() || nombre.length() < 2) {
            txtNombre.setError("Ingrese un nombre válido (mínimo 2 caracteres)");
            txtNombre.requestFocus();
            return;
        }
        if (apellidos.isEmpty() || apellidos.length() < 2) {
            txtApellidos.setError("Ingrese apellidos válidos (mínimo 2 caracteres)");
            txtApellidos.requestFocus();
            return;
        }
        if (dni.isEmpty() || dni.length() != 8 || !dni.matches("\\d+")) {
            txtDni.setError("Ingrese un DNI válido (8 dígitos)");
            txtDni.requestFocus();
            return;
        }
        if (celular.isEmpty() || celular.length() != 9 || !celular.matches("\\d+")) {
            txtCelular.setError("Ingrese un número de celular válido (9 dígitos)");
            txtCelular.requestFocus();
            return;
        }
        if (distritoSeleccionado == 0) {
            Toast.makeText(this, "Seleccione un distrito válido", Toast.LENGTH_SHORT).show();
            cboDistritos.requestFocus();
            return;
        }
        if (correo.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            txtCorreo.setError("Ingrese un correo electrónico válido");
            txtCorreo.requestFocus();
            return;
        }
        if (clave.isEmpty() || clave.length() < 3) {
            txtClave.setError("Ingrese una contraseña de al menos 8 caracteres");
            txtClave.requestFocus();
            return;
        }
        if (!clave.equals(clave2)) {
            txtClave2.setError("Las contraseñas no coinciden");
            txtClave2.requestFocus();
            return;
        }
        if (!chkTerminos.isChecked()) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        crearCuenta(); // Si todas las validaciones pasan, se crea la cuenta
    }

    private void crearCuenta() {
        Hash hash = new Hash();

        AsyncHttpClient ahcAgregarPersona = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("nombre", txtNombre.getText().toString());
        params.put("apellidos", txtApellidos.getText().toString());
        params.put("dni", txtDni.getText().toString());
        params.put("celular", txtCelular.getText().toString());
        params.put("id_distrito", cboDistritos.getSelectedItemPosition());
        params.put("correo", txtCorreo.getText().toString());
        params.put("clave", Hash.StringToHash(txtClave.getText().toString(), "SHA256").toLowerCase());
        ahcAgregarPersona.post(urlAgregarPersona, params, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                if (statusCode == 200) {
                    int status = rawJsonResponse.isEmpty() ? 0 : Integer.parseInt(rawJsonResponse);
                    if (status == 1) {
                        Toast.makeText(getApplicationContext(), "Usuario agregado", Toast.LENGTH_SHORT).show();
                        Intent sesion = new Intent(getApplicationContext(), SesionActivity.class);
                        startActivity(sesion);
                        finish();
                    } else

                        Toast.makeText(getApplicationContext(), "Error al agregar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                Toast.makeText(getApplicationContext(), "ERROR: " + statusCode, Toast.LENGTH_LONG).show();
            }
            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }
    private void regresar() {
        Intent sesion = new Intent(this, SesionActivity.class);
        startActivity(sesion);
        finish();
    }
    private void mostrarTerminos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Términos y Condiciones");
        builder.setMessage("Términos y Condiciones de Uso" +
                "Actualizado el 01-09-2024\n" +
                "\n" +
                "Bienvenido a Burros al Volante. Al acceder y utilizar nuestra" +
                " aplicación móvil, aceptas los siguientes términos y condiciones. " +
                "Si no estás de acuerdo con estos términos, por favor no utilices" +
                " la app.\n" +
                "\n" +
                "1. Aceptación de los Términos\n" +
                "\n" +
                "Al descargar, instalar o usar Burros al Volante, aceptas " +
                "estos términos y condiciones y nuestra política de privacidad." +
                " Si no aceptas estos términos, no debes usar la app.\n" +
                "\n" +
                "2. Uso de la App\n" +
                "\n" +
                "2.1 Licencia de Uso: Te otorgamos una licencia no exclusiva, " +
                "intransferible y revocable para usar la app en tu dispositivo " +
                "móvil conforme a estos términos.\n" +
                "\n" +
                "2.2 Restricciones: No puedes modificar, reproducir, distribuir, " +
                "vender, o crear trabajos derivados de la app sin nuestro " +
                "consentimiento previo por escrito. Tampoco debes usar la app " +
                "para fines ilegales o no autorizados.\n" +
                "\n" +
                "3. Registro y Seguridad\n" +
                "\n" +
                "3.1 Cuenta de Usuario: Para acceder a ciertas funciones, debes " +
                "crear una cuenta proporcionando información veraz y completa. " +
                "Eres responsable de mantener la confidencialidad de tu cuenta " +
                "y contraseña.\n" +
                "\n" +
                "3.2 Seguridad: Nos reservamos el derecho de suspender o cancelar " +
                "tu cuenta si sospechamos que se está utilizando de manera " +
                "fraudulenta o en violación de estos términos.\n" +
                "\n" +
                "4. Contenido de Usuario\n" +
                "\n" +
                "4.1 Responsabilidad del Contenido: Eres el único responsable " +
                "del contenido que publiques o transmitas a través de la app. " +
                "No publicaremos ni aprobaremos contenido que sea ilegal, " +
                "ofensivo o que viole los derechos de terceros.\n" +
                "\n" +
                "4.2 Licencia de Contenido: Al publicar contenido en la app, " +
                "nos otorgas una licencia mundial, no exclusiva, libre de regalías " +
                "y sublicenciable para usar, reproducir y distribuir dicho contenido.\n" +
                "\n" +
                "5. Propiedad Intelectual\n" +
                "\n" +
                "Todos los derechos de propiedad intelectual sobre la app y su contenido, " +
                "incluyendo marcas registradas, derechos de autor y patentes, pertenecen " +
                "a Burros Volante o a sus licenciantes.\n" +
                "\n" +
                "6. Modificaciones de la App y Términos\n" +
                "\n" +
                "Nos reservamos el derecho de modificar o interrumpir la app en " +
                "cualquier momento, así como de actualizar estos términos. Las " +
                "modificaciones entrarán en vigor en cuanto se publiquen en la app." +
                " Tu uso continuado de la app después de dichas modificaciones implica " +
                "tu aceptación de los nuevos términos.\n" +
                "\n" +
                "7. Limitación de Responsabilidad\n" +
                "\n" +
                "La app se proporciona \"tal cual\" y \"según disponibilidad\". " +
                "No garantizamos que la app estará libre de errores o que funcionará" +
                " sin interrupciones. En la máxima medida permitida por la ley, no " +
                "seremos responsables de ningún daño indirecto, incidental o consecuente " +
                "que surja del uso o la imposibilidad de uso de la app.\n" +
                "\n" +
                "8. Ley Aplicable\n" +
                "\n" +
                "Estos términos se rigen por las leyes de Perú. Cualquier disputa que " +
                "surja en relación con estos términos será resuelta en los tribunales " +
                "competentes de Lima/Lima.\n" +
                "\n" +
                "9. Contacto\n" +
                "\n" +
                "Si tienes preguntas sobre estos términos, puedes contactarnos en " +
                "burritos_volante@upn.pe o en Av El Sol 461, San Juan de Lurigancho 15434\n" +
                "\n" +
                "10. Terminación\n" +
                "\n" +
                "Podemos suspender o terminar tu acceso a la app si incumples " +
                "estos términos o por cualquier motivo que consideremos necesario " +
                "para proteger la integridad de la app.\n");
        chkTerminos.setChecked(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chkTerminos.setChecked(true);
                btnCrear.setEnabled(true);
                dialog.dismiss();
            }
        });
        AlertDialog terminos = builder.create();
        terminos.setCancelable(false);
        terminos.setCanceledOnTouchOutside(false);
        //terminos.getWindow().setType(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY);
        terminos.show();
    }
}