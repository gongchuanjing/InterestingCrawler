package tk.chuanjing.demo.crawler.jd.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

@Component
public class HttpUtils {

    //下载图片的存放路径
    //private String imgPath = "E:\\Workspace\\WorkspaceIntelliJ_IDEA\\InterestingCrawler\\src\\main\\resources\\images\\";
    private String imgPath = "H:\\Download\\InterestingCrawler\\images\\";

    private PoolingHttpClientConnectionManager cm;

    public HttpUtils() {
        this.cm = new PoolingHttpClientConnectionManager();

        //设置最大连接数
        this.cm.setMaxTotal(100);

        //设置每个主机的最大连接数
        this.cm.setDefaultMaxPerRoute(10);
    }

    /**
     * 根据请求地址下载页面数据
     *
     * @param url
     * @return 页面数据
     */
    public String doGetHtml(String url) {
        //获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

        //创建httpGet请求对象，设置url地址
        HttpGet httpGet = new HttpGet(url);

        //设置请求信息
        httpGet.setConfig(getConfig());

        CloseableHttpResponse response = null;
        try {
            //使用HttpClient发起请求，获取响应
            response = httpClient.execute(httpGet);

            //解析响应，返回结果
            if (response.getStatusLine().getStatusCode() == 200) {
                //判断响应体Entity是否不为空，如果不为空就可以使用EntityUtils
                if (response.getEntity() != null) {
                    String content = EntityUtils.toString(response.getEntity(), "utf8");
                    return content;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭response
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //不能关闭HttpClient，由连接池管理HttpClient
            //httpClient.close();
        }
        //返回空串
        return "";
    }


    /**
     * 下载图片
     *
     * @param url
     * @return 图片名称
     */
    public String doGetImage(String url) {
        //获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

        //创建httpGet请求对象，设置url地址
        // 这行代码会有问题：如果地址中涉及了特殊字符，如‘｜’‘&’等。所以不能直接用String代替URI来访问。必
        // 须采用%0xXX方式来替代特殊字符。但这种办法不直观。所以只能先把String转成URL，再能过URL生成URI的
        // 方法来解决问题。代码如下
        //HttpGet httpGet = new HttpGet(url);
        HttpGet httpGet = null;
        try{
            URL url1 = new URL(url);
            if(StringUtils.isEmpty(url1.getProtocol()) ||
                    StringUtils.isEmpty(url1.getHost()) ||
                    StringUtils.isEmpty(url1.getPath())) {

                System.out.println("抓取数据遇到异常，本条数据可能不完整……即将抓取下一条数据……");
                return null;
            }
            URI uri = new URI(url1.getProtocol(), url1.getHost(), url1.getPath(), url1.getQuery(), null);
            httpGet = new HttpGet(uri);
        }catch(Exception e){
            e.printStackTrace();
        }


        //设置请求信息
        httpGet.setConfig(getConfig());

        CloseableHttpResponse response = null;
        try {
            //使用HttpClient发起请求，获取响应
            response = httpClient.execute(httpGet);

            //解析响应，返回结果
            if (response.getStatusLine().getStatusCode() == 200) {
                //判断响应体Entity是否不为空
                if (response.getEntity() != null) {
                    //下载图片
                    //获取图片的后缀
                    String extName = url.substring(url.lastIndexOf("."));

                    //创建图片名，重命名图片
                    String picName = UUID.randomUUID().toString() + extName;

                    //下载图片
                    //声明OutPutStream
                    OutputStream outputStream = new FileOutputStream(new File(imgPath + picName));
                    response.getEntity().writeTo(outputStream);

                    //返回图片名称
                    return picName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭response
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //不能关闭HttpClient，由连接池管理HttpClient
            //httpClient.close();
        }
        //如果下载失败，返回空串
        return "";
    }

    //设置请求信息
    private RequestConfig getConfig() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(1000)            //创建连接的最长时间
                .setConnectionRequestTimeout(500)   // 获取连接的最长时间
                .setSocketTimeout(10000)            //数据传输的最长时间
                .build();

        return config;
    }
}
