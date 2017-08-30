/**
 * 
 */
package iie.wxy.wifilocrecoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

/**
 * @author wxy
 *
 */
public class NetworkUtils {

	/**
	 * 
	 */
	public NetworkUtils() {
	}

	public static String doPost(String url, List<NameValuePair> params){
        HttpEntity requestHttpEntity = null;
        try {
            requestHttpEntity = new UrlEncodedFormEntity(
                    params);
            // URL使用基本URL即可，其中不需要加参数
            HttpPost httpPost = new HttpPost(url);
            // 将请求体内容加入请求中
            httpPost.setEntity(requestHttpEntity);
//            Log.d("wxy", "doPost: "+httpPost.toString());
            // 需要客户端对象来发送请求
            HttpClient httpClient = new DefaultHttpClient();

            // 发送请求
            HttpResponse response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                String s = dealResponseResult(response.getEntity().getContent());
                return s;
            }else{
                Log.d("wxy", "get ret "+code);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
