REM Installiere ein SSL-Zertifikat im Android-Emulator
REM Backup des Original-Zertifikatsspeicher cacerts.bks erfolgt in diesem Verzeichnis
REM ----

adb pull /system/etc/security/cacerts.bks ./cacerts.bks
adb pull /system/etc/security/cacerts.bks ./cacerts.bks.org 

REM loesche das Zertifikat:
keytool -v -delete -storetype BKS -storepass changeit -alias mycertificate -keystore .\cacerts.bks

PAUSE 

keytool -v -import -storetype BKS -storepass changeit -trustcacerts -alias mycertificate -file D:\ssh\mailvisionera.cer -keystore .\cacerts.bks
keytool -v -list   -storetype BKS -storepass changeit -alias mycertificate -keystore .\cacerts.bks

adb shell mount -o remount,rw /dev/block/mtdblock3 /system
adb shell mv /system/etc/security/cacerts.bks /system/etc/security/cacerts.bks_bak

adb push ./cacerts.bks /system/etc/security

PAUSE