/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package pt.iscte.dcti.aguiaj;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ElearningHack {
	
	//D41D8CD98F00B204E9800998ECF8427E
	//2833A20510D9B8ECA3B3C7586102ED52
	//96C0193113433B42A98B39D7B7422EF6
	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		
		System.out.println(buildPostData("622370EC022D8AC0DDEDBC79FA397285"));
		
	}
	public static void main2(String[] args) {
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
						// TODO Auto-generated method stub
					}

					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType)
					throws CertificateException {
						// TODO Auto-generated method stub

					}
				}};


		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}
		String content = null;
		// Now you can access an https URL without having the certificate in the truststore
		try {
			URL url = new URL("https://e-learning.iscte.pt/webapps/login/");
			Scanner scanner = new Scanner(url.openConnection().getInputStream());
			scanner.useDelimiter("\\Z");			
			content = scanner.next();

			

			String token = null;
			Scanner lineByLine = new Scanner(content);
			while(lineByLine.hasNextLine()) {
				String line = lineByLine.nextLine();
				int i = line.indexOf("NAME=\"one_time_token\"");
				if(i != -1) {
					System.out.println(line);

					token = line.substring(line.indexOf("VALUE=") + "VALUE=\"".length(), i - 2);
					System.out.println(token);
				}
			}
			
			String data = buildPostData(token);

			System.out.println(data);
			
			// Send data
			URL url2 = new URL("https://e-learning.iscte.pt/webapps/login/");
			URLConnection conn = url2.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				System.out.println(line);
			}
			wr.close();
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//		System.out.println(content);
		//		System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
		//		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		//		String content = null;
		//		URLConnection connection = null;
		//		try {
		//			connection =  new URL("https://e-learning.iscte.pt/webapps/login/").openConnection();
		//			Scanner scanner = new Scanner(connection.getInputStream());
		//			scanner.useDelimiter("\\Z");			
		//			content = scanner.next();
		//		}catch ( Exception ex ) {
		//			ex.printStackTrace();
		//		}
		//		System.out.println(content);
	}

	private static String buildPostData(String token)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		//			  var final_to_encode = hex_md5(form.password.value) + form.one_time_token.value;
		//			  form.encoded_pw.value = hex_md5(final_to_encode);
				String hex_md5 = MD5Hex.toMD5Hex("1982##core");
				String final_to_encode = hex_md5 + token;
				String form_encoded_pw = MD5Hex.toMD5Hex(final_to_encode);
		//		  var encoded_pw_unicode = calcMD5(form.password.value);
		//			  final_to_encode = encoded_pw_unicode + form.one_time_token.value;
		//			  form.encoded_pw_unicode.value = calcMD5(final_to_encode);
		//			  form.password.value = "";
		
					String encoded_pw_unicode = MD5Hex.toMD5("1982##core");
					String encoded_pw_unicode2 = AeSimpleMD5.MD5("1982##core");
					
					byte[] bytes = encoded_pw_unicode.getBytes();
					
					
					
					
					final_to_encode = encoded_pw_unicode + token;
					
					String form_encoded_pw_unicode = MD5Hex.toMD5Hex(final_to_encode);
					
					
					//		<INPUT VALUE="login" NAME="action" TYPE="HIDDEN">
					//        <INPUT VALUE="" NAME="remote-user" TYPE="HIDDEN">
					//        <INPUT VALUE="" NAME="new_loc" TYPE="HIDDEN">
					//        <INPUT VALUE="" NAME="auth_type" TYPE="HIDDEN">
					//        <INPUT VALUE="6F0220AE97AFD0268CC297CFE3C6F82C" NAME="one_time_token" TYPE="HIDDEN">
					//        <INPUT VALUE="" NAME="encoded_pw" TYPE="HIDDEN">
					//        <INPUT VALUE="" NAME="encoded_pw_unicode" TYPE="HIDDEN">
		
					// Construct data
					String data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("login", "UTF-8");
					data += URLEncoder.encode("remote-user", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
					data += "&" + URLEncoder.encode("new_loc", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
					data += "&" + URLEncoder.encode("auth_type", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
					data += "&" + URLEncoder.encode("one_time_token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
					data += "&" + URLEncoder.encode("encoded_pw", "UTF-8") + "=" + form_encoded_pw;
					data += "&" + URLEncoder.encode("encoded_pw_unicode", "UTF-8") + "=" + URLEncoder.encode(final_to_encode, "UTF-8");
					data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode("alss@iscte.pt", "UTF-8");
					data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
		return data;
	}
}

