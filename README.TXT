Creating a keystore file.
If validity is out, use the following cli to regenerate a keystore:
rm src/test/keystore
keytool -genkeypair -alias foo_alias -keystore src/test/keystore -keypass key-passwd -validity 999 -storepass changeit
