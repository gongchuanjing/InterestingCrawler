package tk.chuanjing.demo.crawler;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * @author ChuanJing
 * @date 2018/10/21 15:11
 *
 * post方式发送请求，带参数
 */
public class Crawler05HttpPostParam {
    public static void main(String[] args) throws UnsupportedEncodingException {
        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //创建HttpPost对象，设置url访问地址
        ///设置请求地址是：https://search.jd.com/Search?keyword=iPhone
        HttpPost httpPost = new HttpPost("https://search.jd.com/Search");

        //声明List集合，封装表单中的参数
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("keyword", "iPhone"));

        //创建表单的Entity对象,第一个参数就是封装好的表单数据，第二个参数就是编码
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf8");

        //设置表单的Entity对象到Post请求中
        httpPost.setEntity(formEntity);

        CloseableHttpResponse response = null;
        try {
            //使用HttpClient发起请求，获取response
            response = httpClient.execute(httpPost);

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
