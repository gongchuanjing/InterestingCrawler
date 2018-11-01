package tk.chuanjing.demo.crawler;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author ChuanJing
 * @date 2018/11/1 17:46
 */
public class Crawler08Jsoup {

    public static void main(String[] args) throws IOException {
        //testUrl();
        //testString();
        testFile();
    }

    public static void testUrl() throws IOException {
        //解析url地址,第一个参数是访问的url，第二个参数是访问时候的超时时间
        Document doc = Jsoup.parse(new URL("https://www.baidu.com"), 1000);

        //使用标签选择器，获取title标签中的内容
        String title = doc.getElementsByTag("title").first().text();

        //打印
        System.out.println(title);
    }

    public static void testString() throws IOException {
        //使用工具类读取文件，获取字符串
        //String content = FileUtils.readFileToString(new File("test.html"), "utf8");
        String content = FileUtils.readFileToString(new File("E:\\Workspace\\WorkspaceIntelliJ_IDEA\\InterestingCrawler\\src\\main\\resources\\test.html"), "utf8");

        //解析字符串
        Document doc = Jsoup.parse(content);

        String title = doc.getElementsByTag("title").first().text();

        //打印
        System.out.println(title);
    }

    public static void testFile() throws IOException {
        //解析文件
        Document doc = Jsoup.parse(new File("E:\\Workspace\\WorkspaceIntelliJ_IDEA\\InterestingCrawler\\src\\main\\resources\\test.html"), "utf8");

        String title = doc.getElementsByTag("title").first().text();

        //打印
        System.out.println(title);
    }
}
