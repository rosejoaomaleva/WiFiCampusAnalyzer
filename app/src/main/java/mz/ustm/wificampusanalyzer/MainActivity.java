package mz.ustm.wificampusanalyzer;
import android.Manifest; import android.content.*; import android.content.pm.PackageManager; import android.os.Bundle; import android.widget.*; import androidx.activity.result.ActivityResultLauncher; import androidx.activity.result.contract.ActivityResultContracts; import androidx.appcompat.app.AppCompatActivity; import java.util.*;
public class MainActivity extends AppCompatActivity{
 EditText edt; String pending=""; ActivityResultLauncher<String[]> launcher;
 protected void onCreate(Bundle b){super.onCreate(b);setContentView(R.layout.activity_main);edt=findViewById(R.id.edtNetwork);Button btn=findViewById(R.id.btnSearch);
 launcher=registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),r->openIfAllowed());
 btn.setOnClickListener(v->{pending=edt.getText().toString().trim();request();});}
 void request(){List<String> p=new ArrayList<>();if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)p.add(Manifest.permission.ACCESS_FINE_LOCATION);if(android.os.Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES)!=PackageManager.PERMISSION_GRANTED)p.add(Manifest.permission.NEARBY_WIFI_DEVICES);if(p.isEmpty())open();else launcher.launch(p.toArray(new String[0]));}
 void openIfAllowed(){boolean f=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED;boolean n=android.os.Build.VERSION.SDK_INT<33||checkSelfPermission(Manifest.permission.NEARBY_WIFI_DEVICES)==PackageManager.PERMISSION_GRANTED;if(f&&n)open();}
 void open(){Intent i=new Intent(this,ResultsActivity.class);i.putExtra("filter",pending);startActivity(i);}
}
