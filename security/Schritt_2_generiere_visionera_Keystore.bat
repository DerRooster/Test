REM Generate Private-Key-File for Signing Android-Apps
REM -------
REM Keystore-Passwort: meinpasswort
REM Vor- und Nachname: Arno Becker
REM Organsisationseinheit: Mobile Services
REM Organisation: visionera gmbh
REM Stadt: Bonn
REM Bundesland: NRW
REM Landescode: DE
REM gleiches Paßwort für Key-File wie für Keystore
REM -------
REM Key-File gilt fuer 50 Jahre = 18250 Tage
REM Aliasname fuer Key im Keystore: visionerakey

keytool -genkey -v -keystore androidbuch.keystore -alias visionerakey -keyalg RSA -validity 18250
PAUSE