adb pull /system/etc/security/cacerts.bks ./cacerts.bks
adb pull /system/etc/security/cacerts.bks ./cacerts.bks.org 
keytool -v -list   -storetype BKS -storepass changeit -alias mycertificate -keystore .\cacerts.bks

REM loesche das Zertifikat:
keytool -v -delete -storetype BKS -storepass changeit -alias mycertificate -keystore .\cacerts.bks

PAUSE 

keytool -v -import -storetype BKS -storepass changeit -trustcacerts -alias mycertificate -file D:\ssh\mailvisionera.cer -keystore .\cacerts.bks
keytool -v -list   -storetype BKS -storepass changeit -alias mycertificate -keystore .\cacerts.bks

PAUSE