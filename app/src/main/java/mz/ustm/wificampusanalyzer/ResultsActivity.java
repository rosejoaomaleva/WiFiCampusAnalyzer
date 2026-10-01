package mz.ustm.wificampusanalyzer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

 private static final int LOCATION_PERMISSION_REQUEST = 100;

 private WifiManager wifiManager;
 private WifiAdapter adapter;

 private final List<WifiNetwork> networks =
         new ArrayList<>();

 private TextView summary;

 private String filter = "";

 // ============================================================
 // RECEIVER - RECEBE O RESULTADO DA PESQUISA WI-FI
 // ============================================================

 private final BroadcastReceiver wifiReceiver =
         new BroadcastReceiver() {

          @Override
          public void onReceive(
                  Context context,
                  Intent intent) {

           if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
                   .equals(intent.getAction())) {

            showResults();
           }
          }
         };

 // ============================================================
 // ON CREATE
 // ============================================================

 @Override
 protected void onCreate(Bundle savedInstanceState) {

  super.onCreate(savedInstanceState);

  setContentView(R.layout.activity_results);

  // Recebe o nome da rede enviado pela MainActivity
  filter = getIntent().getStringExtra("filter");

  if (filter == null) {
   filter = "";
  }

  // TextView de resumo
  summary = findViewById(R.id.txtSummary);

  // RecyclerView
  RecyclerView recyclerView =
          findViewById(R.id.recyclerWifi);

  recyclerView.setLayoutManager(
          new LinearLayoutManager(this)
  );

  // Adapter
  adapter = new WifiAdapter(networks);

  recyclerView.setAdapter(adapter);

  // Wi-Fi Manager
  wifiManager =
          (WifiManager)
                  getApplicationContext()
                          .getSystemService(
                                  Context.WIFI_SERVICE
                          );

  // ========================================================
  // REGISTRAR BROADCAST RECEIVER
  // ========================================================

  IntentFilter receiverFilter =
          new IntentFilter(
                  WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
          );

  if (Build.VERSION.SDK_INT >= 33) {

   registerReceiver(
           wifiReceiver,
           receiverFilter,
           Context.RECEIVER_NOT_EXPORTED
   );

  } else {

   registerReceiver(
           wifiReceiver,
           receiverFilter
   );
  }

  // Iniciar pesquisa
  startWifiScan();
 }

 // ============================================================
 // INICIAR PESQUISA WI-FI
 // ============================================================

 private void startWifiScan() {

  if (wifiManager == null) {

   Toast.makeText(
           this,
           "Wi-Fi indisponível.",
           Toast.LENGTH_LONG
   ).show();

   return;
  }

  // Verificar se o Wi-Fi está ligado
  if (!wifiManager.isWifiEnabled()) {

   Toast.makeText(
           this,
           "Ative o Wi-Fi e tente novamente.",
           Toast.LENGTH_LONG
   ).show();

   return;
  }

  // ========================================================
  // VERIFICAR PERMISSÃO DE LOCALIZAÇÃO
  // ========================================================

  if (Build.VERSION.SDK_INT >= 23) {

   if (ActivityCompat.checkSelfPermission(
           this,
           Manifest.permission.ACCESS_FINE_LOCATION
   ) != PackageManager.PERMISSION_GRANTED) {

    ActivityCompat.requestPermissions(
            this,
            new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },
            LOCATION_PERMISSION_REQUEST
    );

    return;
   }
  }

  // ========================================================
  // VERIFICAR SE A LOCALIZAÇÃO ESTÁ LIGADA
  // Compatível com API 23+
  // ========================================================

  if (!isLocationEnabledCompat()) {

   Toast.makeText(
           this,
           "Ative a localização para pesquisar Wi-Fi.",
           Toast.LENGTH_LONG
   ).show();

   try {

    startActivity(
            new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
    );

   } catch (Exception e) {

    Toast.makeText(
            this,
            "Não foi possível abrir as definições.",
            Toast.LENGTH_SHORT
    ).show();
   }

   return;
  }

  // ========================================================
  // INICIAR SCAN
  // ========================================================

  try {

   boolean started =
           wifiManager.startScan();

   if (!started) {

    Toast.makeText(
            this,
            "A pesquisa Wi-Fi não foi iniciada.",
            Toast.LENGTH_SHORT
    ).show();

    // Mesmo que o scan não inicie,
    // tentamos mostrar resultados anteriores.
    showResults();
   }

  } catch (SecurityException e) {

   Toast.makeText(
           this,
           "Permissão de Wi-Fi/localização não concedida.",
           Toast.LENGTH_LONG
   ).show();
  }
 }

// ============================================================
// VERIFICAR LOCALIZAÇÃO - API 23+
// ============================================================

 private boolean isLocationEnabledCompat() {

  LocationManager locationManager =
          (LocationManager)
                  getSystemService(
                          Context.LOCATION_SERVICE
                  );

  if (locationManager == null) {
   return false;
  }

  // Android 9 / API 28 ou superior
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

   return locationManager.isLocationEnabled();
  }

  // Android 6 até Android 8.1 (API 23 a 27)
  int mode =
          Settings.Secure.getInt(
                  getContentResolver(),
                  Settings.Secure.LOCATION_MODE,
                  Settings.Secure.LOCATION_MODE_OFF
          );

  return mode != Settings.Secure.LOCATION_MODE_OFF;
 }

 // ============================================================
 // RECEBER RESULTADO DA PERMISSÃO
 // ============================================================

 @Override
 public void onRequestPermissionsResult(
         int requestCode,
         @NonNull String[] permissions,
         @NonNull int[] grantResults) {

  super.onRequestPermissionsResult(
          requestCode,
          permissions,
          grantResults
  );

  if (requestCode ==
          LOCATION_PERMISSION_REQUEST) {

   if (grantResults.length > 0 &&
           grantResults[0] ==
                   PackageManager.PERMISSION_GRANTED) {

    // Permissão concedida
    startWifiScan();

   } else {

    Toast.makeText(
            this,
            "A permissão de localização é necessária para pesquisar redes Wi-Fi.",
            Toast.LENGTH_LONG
    ).show();
   }
  }
 }

 // ============================================================
 // MOSTRAR RESULTADOS
 // ============================================================

 private void showResults() {

  networks.clear();

  if (wifiManager == null) {

   return;
  }

  // Verificar novamente a permissão
  if (Build.VERSION.SDK_INT >= 23) {

   if (ActivityCompat.checkSelfPermission(
           this,
           Manifest.permission.ACCESS_FINE_LOCATION
   ) != PackageManager.PERMISSION_GRANTED) {

    return;
   }
  }

  try {

   List<ScanResult> results =
           wifiManager.getScanResults();

   // ====================================================
   // PERCORRER REDES ENCONTRADAS
   // ====================================================

   for (ScanResult result : results) {

    String ssid = result.SSID;

    // Ignorar redes sem nome
    if (ssid == null ||
            ssid.trim().isEmpty()) {

     continue;
    }

    // =================================================
    // FILTRO PELO NOME DA REDE
    // =================================================

    if (!filter.isEmpty()) {

     String ssidLower =
             ssid.toLowerCase(
                     Locale.ROOT
             );

     String filterLower =
             filter.toLowerCase(
                     Locale.ROOT
             );

     if (!ssidLower.contains(
             filterLower)) {

      continue;
     }
    }

    // =================================================
    // ADICIONAR REDE
    // =================================================

    networks.add(
            new WifiNetwork(
                    ssid,
                    result.level
            )
    );
   }

  } catch (SecurityException e) {

   Toast.makeText(
           this,
           "Não foi possível acessar os resultados Wi-Fi.",
           Toast.LENGTH_SHORT
   ).show();
  }

  // ========================================================
  // ORDENAR POR INTENSIDADE DO SINAL
  // Compatível com API 23+
  // ========================================================

  Collections.sort(
          networks,
          (a, b) ->
                  Integer.compare(
                          b.getRssi(),
                          a.getRssi()
                  )
  );

  // Atualizar RecyclerView
  adapter.notifyDataSetChanged();

  // ========================================================
  // ATUALIZAR RESUMO
  // ========================================================

  if (filter.isEmpty()) {

   summary.setText(
           networks.size()
                   + " rede(s) encontrada(s)"
   );

  } else {

   summary.setText(
           networks.size()
                   + " rede(s) encontrada(s) para \""
                   + filter
                   + "\""
   );
  }
 }

 // ============================================================
 // AO VOLTAR PARA A ACTIVITY
 // ============================================================

 @Override
 protected void onResume() {

  super.onResume();

  // Se a localização foi ativada,
  // tenta pesquisar novamente.
  if (wifiManager != null &&
          wifiManager.isWifiEnabled()) {

   if (isLocationEnabledCompat()) {

    if (Build.VERSION.SDK_INT < 23 ||
            ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

     startWifiScan();
    }
   }
  }
 }

 // ============================================================
 // DESTRUIR ACTIVITY
 // ============================================================

 @Override
 protected void onDestroy() {

  super.onDestroy();

  try {

   unregisterReceiver(
           wifiReceiver
   );

  } catch (IllegalArgumentException ignored) {
   // Receiver já foi removido
  }
 }
}