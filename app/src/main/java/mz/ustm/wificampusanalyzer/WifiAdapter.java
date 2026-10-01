package mz.ustm.wificampusanalyzer;
import android.view.*; import android.widget.*; import androidx.annotation.NonNull; import androidx.recyclerview.widget.RecyclerView; import java.util.List;
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.VH>{
 private final List<WifiNetwork> data; public WifiAdapter(List<WifiNetwork> d){data=d;}
 @NonNull public VH onCreateViewHolder(@NonNull ViewGroup p,int v){return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_wifi,p,false));}
 public void onBindViewHolder(@NonNull VH h,int i){WifiNetwork n=data.get(i);h.ssid.setText(n.getSsid());h.rssi.setText("Intensidade: "+n.getRssi()+" dBm");h.q.setText(n.getQuality());}
 public int getItemCount(){return data.size();}
 static class VH extends RecyclerView.ViewHolder{TextView ssid,rssi,q;VH(View v){super(v);ssid=v.findViewById(R.id.txtSsid);rssi=v.findViewById(R.id.txtRssi);q=v.findViewById(R.id.txtQuality);}}
}
