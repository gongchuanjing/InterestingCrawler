package tk.chuanjing.demo.crawler;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author ChuanJing
 * @date 2018/10/21 14:44
 *
 * HttpClient请求参数的设置
 */
public class Crawler07HttpConfig {
    public static void main(String[] args) {
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //创建HttpGet对象，设置url访问地址
        HttpGet httpGet = new HttpGet("http://www.baidu.com");


        RequestConfig config = RequestConfig.custom().setConnectTimeout(1000)           //创建连接的最长时间，单位是毫秒
                                                     .setConnectionRequestTimeout(500)  //设置获取连接的最长时间，单位是毫秒
                                                     .setSocketTimeout(10*1000)         //设置数据传输的最长时间，单位是毫秒
                                                     .build();
        //给请求设置请求信息
        httpGet.setConfig(config);


        CloseableHttpResponse response = null;
        try {
            //使用HttpClient发起请求，获取response
            response = httpClient.execute(httpGet);

            //解析响应
            if(response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "utf8");
                System.out.println("请求回来的长度：" + content.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭response
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //关闭httpClient
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
