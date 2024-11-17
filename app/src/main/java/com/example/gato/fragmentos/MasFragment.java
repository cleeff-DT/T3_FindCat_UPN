package com.example.gato.fragmentos;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.getIntent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.util.Linkify;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gato.R;
import com.example.gato.actividades.BienvenidaActivity;
import com.example.gato.clases.Menu;
import com.example.gato.clases.Usuario;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MasFragment extends Fragment implements View.OnClickListener, Menu, OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000; // Puedes elegir cualquier número único

    private static String urlMostrarTipoReporte = "http://gatitos.atwebpages.com/services/mostrarTiposReporte.php";
    private static String urlAgregarReporte = "http://gatitos.atwebpages.com/services/agregarReporte.php";
    int userId;
    Spinner cboTipoReportes;
    ImageView imgReporte;
    Button btnTomar, btnEnviar;
    EditText txtTitulo, txtDescripcion;
    TextView txtUbicacion;

    Usuario usuario;

    static final int REQUEST_TAKE_PHOTO = 1;
    String sRutaFotoTemporal;
    Uri photoURI;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MasFragment newInstance(String param1, String param2) {
        MasFragment fragment = new MasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            userId = getArguments().getInt("userId"); // Obtener el ID del usuario
        }

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_mas, container, false);


        cboTipoReportes = vista.findViewById(R.id.frgMasCboTipoReportes);
        imgReporte = vista.findViewById(R.id.frgMasImgPreview);
        btnTomar = vista.findViewById(R.id.frgMasBtnTomarFoto);
        btnEnviar = vista.findViewById(R.id.frgMasBtnEnviar);
        txtUbicacion = vista.findViewById(R.id.frgMasTxtUbicacion);
        txtDescripcion = vista.findViewById(R.id.frgMasTxtDescripcion);

        btnEnviar.setOnClickListener(this);
        btnTomar.setOnClickListener(this);
        obtenerUbicacionYGuardarLink();
        verificarPermisos();
        cargarTipoReportes();



        Bundle bundle = this.getArguments();
        usuario = (Usuario)bundle.getSerializable("usuario");

        return vista;
    }

    private void verificarPermisos(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }
    }

    private void cargarTipoReportes() {
        //Crear un objeto para realizar la tarea asincronahacia en web services
        AsyncHttpClient ahcMostrarTipoReportes = new AsyncHttpClient();
        //crear un adatador por defecto al spiner
        cboTipoReportes.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[] {"-- Seleccione Tipo de Reporte --"}));
        //ejecutar ña tarea asincronica
        ahcMostrarTipoReportes.get(urlMostrarTipoReporte, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                if(statusCode == 200){
                    try {
                        JSONArray jsonArray = new JSONArray(rawJsonResponse);
                        String[] tipoReportes = new String[jsonArray.length()+1];
                        tipoReportes[0] = "-- Seleccione Tipo de Reporte --";
                        for (int i = 1; i < jsonArray.length() + 1 ; i++) {
                            tipoReportes[i] = jsonArray.getJSONObject(i-1).getString("descripcion_tipo");
                        }
                        cboTipoReportes.setAdapter(new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_spinner_dropdown_item, tipoReportes));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                Toast.makeText(getContext(),"ERROR: "+statusCode,Toast.LENGTH_LONG).show();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.frgMasBtnTomarFoto)
            tomarFoto();
        else if(v.getId() == R.id.frgMasBtnEnviar)
            enviarReporte();
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String FileName = "JPEG_"+timeStamp;
        File Directorio = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        System.out.println(Directorio);
        File imagen = File.createTempFile(FileName, ".jpg", Directorio);
        sRutaFotoTemporal = imagen.getAbsolutePath();
        return imagen;
    }

    private void tomarFoto() {
        try {
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent iTomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.gato.fileprovider", createImageFile());
                iTomarFoto.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(iTomarFoto, REQUEST_TAKE_PHOTO);
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Los Gatitos");
                builder.setMessage("No se puede acceder a la cámara");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    Toast.makeText(getContext(), "Tomar Foto Acepto!!", Toast.LENGTH_SHORT).show();
                    imgReporte.setImageURI(photoURI);
                }
                else{
                    Toast.makeText(getContext(), "Tomar Foto Cancelo!!", Toast.LENGTH_SHORT).show();
                    File temp = new File(sRutaFotoTemporal);
                    System.out.println(temp.getAbsolutePath());
                    temp.delete();
                }
                break;
        }
    }
    private void obtenerUbicacionYGuardarLink() {
        // Verificar si tenemos el permiso para acceder a la ubicación
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtener la última ubicación conocida
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            // Obtener las coordenadas de latitud y longitud
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Generar el link de Google Maps
                            String linkUbicacion = "https://www.google.com/maps?q=" + latitude + "," + longitude;

                            // Rellenar el TextView con el enlace
                            txtUbicacion.setText(linkUbicacion);
                            txtUbicacion.setLinksClickable(true);  // Permite hacer clic en el enlace
                            txtUbicacion.setAutoLinkMask(Linkify.WEB_URLS);  // Convierte automáticamente en un enlace web

                            Toast.makeText(getContext(), "Ubicación obtenida correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Si no tenemos el permiso, lo solicitamos
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }




    private void enviarReporte() {
        usuario = (Usuario) getArguments().getSerializable("usuario");
        AsyncHttpClient ahcAgregarReporte = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("id_persona", BienvenidaActivity.id_global);
        params.put("id_tipo_reporte", cboTipoReportes.getSelectedItemPosition());
        params.put("descripcion", txtDescripcion.getText().toString());
        params.put("imagen_gato", image_view_to_base64(imgReporte));
        params.put("link_ubicacion", txtUbicacion.getText().toString()); // Toma el valor del campo

        ahcAgregarReporte.post(urlAgregarReporte, params, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                if (statusCode == 200) {
                    int status = rawJsonResponse.isEmpty() ? 0 : Integer.parseInt(rawJsonResponse);
                    if (status == 1) {
                        Toast.makeText(getContext(), "Reporte enviado", Toast.LENGTH_SHORT).show();
                        limpiarFormulario(); // Limpiar los campos
                    } else {
                        Toast.makeText(getContext(), "Error al enviar reporte", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                Toast.makeText(getContext(), "ERROR: " + statusCode, Toast.LENGTH_LONG).show();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void limpiarFormulario() {
        txtDescripcion.setText("");
        imgReporte.setImageResource(0); // Restablecer la imagen del reporte
        txtUbicacion.setText(""); // Limpiar el campo de ubicación
    }

    private String image_view_to_base64(ImageView jiv_foto) {
        Bitmap bitmap = ((BitmapDrawable)jiv_foto.getDrawable()).getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] byteArray = stream.toByteArray();
        String imagen = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return  imagen;
    }

    @Override
    public void onClickMenu(int id) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }
}