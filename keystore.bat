::Platform: Windows
cd /d c:\Users\%username%\.android\
:: "keytool -help" for all available commands 
:: "keytool -list -help" for usage of list
keytool -list -v -keystore debug.keystore -storepass android
::wait for exit
pause