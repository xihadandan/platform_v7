配置tomcat支持https:
1、生成证书,其中CN为域名localhost或者服务器IP地址
keytool -genkey -alias tomcat -keyalg RSA -keysize 2048 -keystore ./tomcat.keystore -dname "CN=localhost, OU=WELL-SOFT, O=PT, L=SH, ST=SH, C=CN" -keypass changeit -storepass changeit -validity 36500

keytool -export -alias tomcat -keystore ./tomcat.keystore -file ./tomcat.cer -storepass changeit

keytool -delete -alias tomcat -keystore "%JAVA_HOME%/jre/lib/security/cacerts" -storepass changeit

keytool -import -trustcacerts -alias tomcat -keystore "%JAVA_HOME%/jre/lib/security/cacerts" -file ./tomcat.cer -storepass changeit

2、配置tomcat的server.xml，添加Connector
	<Connector URIEncoding="UTF-8" SSLEnabled="true" clientAuth="false"
		keystoreFile="D:\wep\workspace2\wellpt-root-600\wellpt-web\src\main\resources\ca\tomcat.keystore"
		keystorePass="changeit" maxThreads="150" port="443"
		protocol="org.apache.coyote.http11.Http11Protocol" scheme="https"
		secure="true" sslProtocol="TLS" sslEnabledProtocols="TLSv1,TLSv1.1,TLSv1.2"
		ciphers="TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256, TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,TLS_ECDHE_RSA_WITH_RC4_128_SHA,TLS_RSA_WITH_AES_128_CBC_SHA256,TLS_RSA_WITH_AES_128_CBC_SHA,TLS_RSA_WITH_AES_256_CBC_SHA256, TLS_RSA_WITH_AES_256_CBC_SHA,SSL_RSA_WITH_RC4_128_SHA" />

3、配置webapp的web.xml，追加
	<login-config>
	     <!-- Authorization setting for SSL -->
	     <auth-method>CLIENT-CERT</auth-method>
	     <realm-name>Client Cert Users-only Area</realm-name>
	</login-config>
	<security-constraint>
	     <!-- Authorization setting for SSL -->
	     <web-resource-collection >
	     <web-resource-name >SSL</web-resource-name>
	     <url-pattern>/*</url-pattern>
	     </web-resource-collection>
	     <user-data-constraint>
	     <transport-guarantee>CONFIDENTIAL</transport-guarantee>
	     </user-data-constraint>
	</security-constraint>	
