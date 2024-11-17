package com.example.gato.fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;
import com.example.gato.R;
import com.example.gato.actividades.AlbergueBarrancoActivity;
import com.example.gato.actividades.AlbergueMirafloresActivity;
import com.example.gato.actividades.AlbergueSanIsidroActivity;
import com.example.gato.actividades.AlbergueSurcoActivity;
import com.example.gato.actividades.EditarPerfilActivity;

public class HomeFragment extends Fragment {

    private MapView mapView;

    public HomeFragment() {
        // Constructor vacío
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configuración de OSMDroid
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Centrar el mapa en Lima
        GeoPoint lima = new GeoPoint(-12.0464, -77.0428); // Coordenadas de Lima
        mapView.getController().setZoom(12); // Ajustar nivel de zoom
        mapView.getController().setCenter(lima);

        // Agregar marcadores con imágenes y clics
        addMarker( -12.147177986533691, -77.01798930882333, "Barranco", R.drawable.icon_albergue); // Barranco
        addMarker(-12.125887749861445, -77.01967404347737, "Miraflores", R.drawable.icon_albergue); // Miraflores
        addMarker(-12.103889072414825, -77.05758128878524, "San Isidro", R.drawable.icon_albergue); // San Isidro
        addMarker(-11.955991305606581, -76.93712917712709, "Surco", R.drawable.icon_albergue); // Surco

        return view;
    }

    private void addMarker(double lat, double lon, String title, int iconResId) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setIcon(ContextCompat.getDrawable(getContext(), iconResId)); // Establecer la imagen de la etiqueta
        marker.setTitle(title); // Establecer el título del marcador (usado en el OnClickListener)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // Alineación del texto

        // Añadir el marcador al mapa
        mapView.getOverlays().add(marker);

        // Establecer un listener para el clic en el marcador
        marker.setOnMarkerClickListener((m, mapView) -> {
            switch (m.getTitle()) {
                case "Barranco":
                    openBarrancoActivity();
                    break;
                case "Miraflores":
                    openMirafloresActivity();
                    break;
                case "San Isidro":
                    openSanIsidroActivity();
                    break;
                case "Surco":
                    openSurcoActivity();
                    break;
            }
            return true;
        });
    }

    private void openBarrancoActivity() {
        // Intent para abrir la actividad correspondiente a Barranco
        Intent intent = new Intent(getContext(), AlbergueBarrancoActivity.class);
        startActivity(intent);
    }

    private void openMirafloresActivity() {
        // Intent para abrir la actividad correspondiente a Miraflores
        Intent intent = new Intent(getContext(), AlbergueMirafloresActivity.class);
        startActivity(intent);
    }

    private void openSanIsidroActivity() {
        // Intent para abrir la actividad correspondiente a San Isidro
        Intent intent = new Intent(getContext(), AlbergueSanIsidroActivity.class);
        startActivity(intent);
    }

    private void openSurcoActivity() {
        // Intent para abrir la actividad correspondiente a Surco
        Intent intent = new Intent(getContext(), AlbergueSurcoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); // Activar el mapa
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause(); // Pausar el mapa
    }

}
