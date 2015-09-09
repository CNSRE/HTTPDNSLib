/**
 * 
 */
package com.sina.util.dnscache.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.sina.util.dnscache.Tools;

/**
 * 
 * 项目名称: DNSCache 类名称: ApacheHttpClientNetworkRequests 
 * 类描述: 轻量级的网络请求库 
 * 创建人: fenglei 
 * 使用 Apache HttpClient 实现网络请求 
 * 创建时间: 2015-3-30 上午11:50:49
 * 
 * 修改人: xingyu10 
 * 修改时间: 2015-9-9 
 * 修改备注: HTTPS不进行域名校验
 * 
 * @version V2.0
 */
public class ApacheHttpClientNetworkRequests implements INetworkRequests {
    private static final int SOCKET_OPERATION_TIMEOUT = 60 * 1000;
    private static final int CONNECTION_TIMEOUT = 30 * 1000;
    private static final int SOCKET_BUFFER_SIZE = 8192;

    public String requests(String url) {
        return requests(url, "");
    }

    public static HttpClient newInstance() {
        HttpParams params = new BasicHttpParams();
        // 不自动处理重定向请求
        params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        HttpConnectionParams.setStaleCheckingEnabled(params, true);
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_OPERATION_TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);
        HttpConnectionParams.setTcpNoDelay(params, true);
        ConnManagerParams.setMaxTotalConnections(params, 50);
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean();
        connPerRoute.setDefaultMaxPerRoute(4);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
        socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        schemeRegistry.register(new Scheme("https", socketFactory, 443));
        ClientConnectionManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
        return new DefaultHttpClient(manager, params);
    }

    @Override
    public String requests(String url, String host) {
        HashMap<String, String> map = null;
        if (host == null || host.equals("")) {
            map = null;
        } else {
            map = new HashMap<String, String>();
            map.put("host", host);
        }

        return requests(url, map);
    }

    @Override
    public String requests(String url, HashMap<String, String> head) {

        String result = null;
        BufferedReader reader = null;

        try {
            HttpClient client = newInstance();
            HttpGet request = new HttpGet();

            if (head != null) {
                for (Entry<String, String> entry : head.entrySet()) {
                    Tools.log("TAG", "" + entry.getKey() + "  -  " + entry.getValue());
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }

            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);

            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer strBuffer = new StringBuffer("");
            String line = null;

            while ((line = reader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public byte[] requestsByteArr(String url, HashMap<String, String> head) {

        byte[] result = null;

        try {
            HttpClient client = newInstance();
            HttpGet request = new HttpGet();

            if (head != null) {
                for (Entry<String, String> entry : head.entrySet()) {
                    Tools.log("TAG", "" + entry.getKey() + "  -  " + entry.getValue());
                    request.addHeader(entry.getKey(), entry.getValue());
                }
            }

            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);

            result = readStream(response.getEntity().getContent());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return result;
    }

    /*
     * 得到图片字节流 数组大小
     */
    public static byte[] readStream(InputStream inStream) {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            outStream.close();
            inStream.close();

        } catch (Exception e) {
        }

        Tools.log("TAG", "outStream.toByteArray()=" + outStream.toByteArray());

        return outStream.toByteArray();
    }

    public static boolean upLoadFile(String url, File file) {
        boolean result = false;
        try {
            if (null == url || null == file || !file.exists() || file.length() < 1) {
                return false;
            }
            /**
             * 第一部分
             */
            URL urlObj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

            /**
             * 设置关键值
             */
            con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false); // post方式不能使用缓存

            // 设置请求头信息
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");

            // 设置边界
            String BOUNDARY = "----------" + System.currentTimeMillis();
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            // 请求正文信息

            // 第一部分：
            StringBuilder sb = new StringBuilder();
            sb.append("--"); // ////////必须多两道线
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");

            byte[] head = sb.toString().getBytes("utf-8");

            // 获得输出流
            OutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(head);

            // 文件正文部分
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();

            // 结尾部分
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
            out.write(foot);
            out.flush();
            out.close();

            int statusCode = con.getResponseCode();
            if (statusCode == 200) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
