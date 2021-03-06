package com.sgnn7.client.tls;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class HttpClient {
	static String clientKeystore = "client.keystore";
	static char[] keystorePassword = "changeit".toCharArray();
	static char[] keyPassword = "changeit".toCharArray();

	static String clientTruststore = "client.truststore";
	static char[] truststorePassword = "changeit".toCharArray();

	private static SSLSocketFactory getFactory() throws Exception {
		KeyStore keystore = KeyStore.getInstance("JKS");
		keystore.load(new FileInputStream(clientKeystore), keystorePassword);
		KeyManagerFactory keyManagerFactory = KeyManagerFactory
				.getInstance("SunX509");
		keyManagerFactory.init(keystore, keyPassword);

		KeyStore trustStore = KeyStore.getInstance("JKS");
		trustStore.load(new FileInputStream(clientTruststore),
				truststorePassword);
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance("SunX509");
		trustManagerFactory.init(trustStore);

		SSLContext context = SSLContext.getInstance("TLSv1");
		context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory
				.getTrustManagers(), new SecureRandom());

		return context.getSocketFactory();
	}

	public static void main(String args[]) {


		try {			
			URL url = new URL("https://localhost/");
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setHostnameVerifier(new CustomHostnameVerifier());
			connection.setSSLSocketFactory(getFactory());
			
			String page = getPage(connection);
			System.out.println(page);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String getPage(HttpsURLConnection connection) throws Exception {
		//OutputStreamWriter wr = null;
		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = null;
		String line = null;

		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		connection.setReadTimeout(10000);

		connection.connect();

		bufferedReader = new BufferedReader(new InputStreamReader(connection
				.getInputStream()));
		stringBuilder = new StringBuilder();

		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line + '\n');
		}

		return stringBuilder.toString();
	}
}
