# Especificação do Projeto — Wi-Fi Campus Analyzer

## Objetivo
Pesquisar redes Wi-Fi próximas e apresentar a intensidade do sinal para apoiar a análise da cobertura Wi-Fi em diferentes locais do campus.

## Activities
**MainActivity:** entrada do SSID e início da pesquisa.  
**ResultsActivity:** pesquisa e apresentação dos resultados.

## Funcionalidade de rede
Uso de WifiManager, startScan(), BroadcastReceiver e getScanResults().

## Interface
ConstraintLayout, TextInputLayout, Button, TextView e RecyclerView.

## Comunicação
Intent envia o filtro digitado da MainActivity para a ResultsActivity.

## Permissões
Permissões de Wi-Fi, localização e dispositivos Wi-Fi próximos.

## Teste
Testar em diferentes locais do campus e comparar os valores RSSI.
