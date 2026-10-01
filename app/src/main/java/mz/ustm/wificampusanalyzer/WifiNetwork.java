package mz.ustm.wificampusanalyzer;
public class WifiNetwork {
 private final String ssid; private final int rssi;
 public WifiNetwork(String ssid,int rssi){this.ssid=ssid;this.rssi=rssi;}
 public String getSsid(){return ssid;} public int getRssi(){return rssi;}
 public String getQuality(){if(rssi>=-50)return "Excelente";if(rssi>=-60)return "Boa";if(rssi>=-70)return "Regular";return "Fraca";}
}
