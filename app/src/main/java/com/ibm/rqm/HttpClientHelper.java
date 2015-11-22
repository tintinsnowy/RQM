package com.ibm.rqm;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by JACK on 2014/12/15.
 */

public class HttpClientHelper {

    private static DefaultHttpClient httpClient;


    HttpClientHelper() {

    }

    public static synchronized HttpClient getHttpClient() {

        if (null == httpClient) {
            // 初始化工作
            try {

                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
                //允许所有主机的验证
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                HttpParams params = new BasicHttpParams();
                //HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
                HttpProtocolParams.setUseExpectContinue(params, true);

                // 设置连接管理器的超时
                ConnManagerParams.setTimeout(params, 5000);
                // 设置连接超时
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                // 设置socket超时
                HttpConnectionParams.setSoTimeout(params, 5000);

                // 设置http https支持
                SchemeRegistry schReg = new SchemeRegistry();
                schReg.register(new Scheme("http", PlainSocketFactory
                        .getSocketFactory(), 9443));
                schReg.register(new Scheme("http", PlainSocketFactory
                        .getSocketFactory(), 80));
                schReg.register(new Scheme("https", sf, 443));

                //schReg.register(new Scheme("https", sf, 9443));
                ClientConnectionManager conManager = new ThreadSafeClientConnManager(
                        params, schReg);

                //新建一个httpClient
                httpClient = new DefaultHttpClient(conManager, params);

                //新加入的代码，主要完成重定向，以及设置存储cookie。

                httpClient.setRedirectHandler(new DefaultRedirectHandler() {
                    @Override
                    public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
                        boolean isRedirect = super.isRedirectRequested(response, context);
                        if (!isRedirect) {
                            int responseCode = response.getStatusLine().getStatusCode();
                            if (responseCode == 301 || responseCode == 302) {
                                return true;
                            }
                        }
                        return isRedirect;
                    }
                });

                //设置cookie存储
                BasicCookieStore cookieStore = new BasicCookieStore();
                httpClient.setCookieStore(cookieStore);


            } catch (Exception e) {
                e.printStackTrace();
                //如果设置失败，则返回默认的httpClient
                return new DefaultHttpClient();
            }
        }
        //返回
        return httpClient;
    }


    private static class CookieStore {
    }
}


class SSLSocketFactoryEx extends SSLSocketFactory {

    SSLContext sslContext = SSLContext.getInstance("TLS");

    public SSLSocketFactoryEx(KeyStore truststore)
            throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {

        super(truststore);

        TrustManager tm = new X509TrustManager() {

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain, String authType)
                    throws java.security.cert.CertificateException {

            }

        };
        sslContext.init(null, new TrustManager[] { tm }, null);

    }

    @Override
    public Socket createSocket(Socket socket, String host, int port,
                               boolean autoClose) throws IOException, UnknownHostException {

        return sslContext.getSocketFactory().createSocket(socket, host, port,
                autoClose);

    }
    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}
