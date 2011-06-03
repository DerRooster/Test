keytool -v -import -storetype BKS -storepass meinpasswort -trustcacerts -alias visionerawebmail -file D:\ssh\mailvisionera.cer -keystore .\owncerts.bks
keytool -v -list   -storetype BKS -storepass meinpasswort -alias visionerawebmail -keystore .\owncerts.bks

PAUSE