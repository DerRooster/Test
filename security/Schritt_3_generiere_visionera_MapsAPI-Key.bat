REM Generate a Maps API-Key (MD5 fingerprint) for accessing Google Maps
REM -------
REM Keystore-Passwort: meinpasswort
REM
REM Der MD5-Fingerprint lautet: D1:E1:A3:84:B2:70:59:25:66:F0:E5:49:7D:B2:F1:36
REM
REM Google Maps API-Key zu diesem Fingerprint: 0xYgdiZYM8ZCRi0hazKDZfDM79J510F2YuK4eiA
REM -------

keytool -list -alias visionerakey -keystore androidbuch.keystore
PAUSE