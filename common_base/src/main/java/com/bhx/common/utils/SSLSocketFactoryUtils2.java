package com.bhx.common.utils;

import android.util.Log;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SSLSocketFactoryUtils2 {


    public static SSLSocketFactory getSSLSocketFactory(InputStream... inputStreams) {
        CertificateFactory certificateFactory = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : inputStreams) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得指定流中的服务器端证书库
     */
    public static TrustManager[] getTrustManager(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            int index = 0;
            for (InputStream certificate : certificates) {
                if (certificate == null) {
                    continue;
                }
                Certificate certificate1;
                try {
                    certificate1 = certificateFactory.generateCertificate(certificate);
                } finally {
                    certificate.close();
                }

                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificate1);
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                    .getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            return trustManagerFactory.getTrustManagers();

        } catch (Exception e) {
            Log.e("httpDebug", "SSLSocketFactoryUtils", e);
        }

        return getTrustAllManager();
    }

    /**
     * 获得信任所有服务器端证书库
     */
    public static TrustManager[] getTrustAllManager() {
        return new X509TrustManager[]{new MyX509TrustManager()};
    }

    public static class MyX509TrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            System.out.println("cert: " + chain[0].toString() + ", authType: " + authType);
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
