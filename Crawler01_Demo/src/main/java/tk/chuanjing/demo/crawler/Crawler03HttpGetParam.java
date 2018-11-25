package tk.chuanjing.demo.crawler;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author ChuanJing
 * @date 2018/10/21 14:54
 *
 * get方式发送请求，带参数
 */
public class Crawler03HttpGetParam {
    public static void main(String[] args) throws URISyntaxException {
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //设置请求地址是：https://github.com/search?q=InterestingCrawler
        //创建URIBuilder
        URIBuilder uriBuilder = new URIBuilder("https://github.com/search");
        //设置参数
        uriBuilder.setParameter("q", "InterestingCrawler");

        //创建HttpGet对象，设置url访问地址
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        System.out.println("发起请求的信息："+httpGet);

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
