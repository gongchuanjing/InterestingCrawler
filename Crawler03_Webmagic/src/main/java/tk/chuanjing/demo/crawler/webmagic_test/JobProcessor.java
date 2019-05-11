package tk.chuanjing.demo.crawler.webmagic_test;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author ChuanJing
 * @date 2018/11/30 17:24
 */
public class JobProcessor implements PageProcessor {

    //解析页面
    @Override
    public void process(Page page) {
        //解析返回的数据page，并且把解析的结果放到ResultItems中
        //css选择器
        page.putField("div", page.getHtml().css("div.mt h2").all());

        //XPath
        page.putField("div2", page.getHtml().xpath("//div[@id=news_div]/ul/li/div/a"));

        //正则表达式
        page.putField("div3", page.getHtml().css("div#news_div a").regex(".*江苏.*").all());

        //处理结果API
        page.putField("div4", page.getHtml().css("div#news_div a").regex(".*江苏.*").get());
        page.putField("div5", page.getHtml().css("div#news_div a").regex(".*江苏.*").toString());

        //获取链接
        //page.addTargetRequests(page.getHtml().css("div#news_div").links().regex(".*9$").all());
        //page.putField("url",page.getHtml().css("div.mt h1").all());

        //测试去重过滤器
//        page.addTargetRequest("https://www.jd.com/news.html?id=37319");
//        page.addTargetRequest("https://www.jd.com/news.html?id=37319");
//        page.addTargetRequest("https://www.jd.com/news.html?id=37319");
    }

    private Site site = Site.me()
            .setCharset("utf8")         //设置编码
            .setTimeOut(10000)          //设置超时时间，单位是ms毫秒
            .setRetrySleepTime(3000)    //设置重试的间隔时间
            .setSleepTime(3);           //设置重试次数

    @Override
    public Site getSite() {
        return site;
    }

    //主函数，执行爬虫
    public static void main(String[] args) {

        Spider.create(new JobProcessor())
                .addUrl("https://www.jd.com/moreSubject.aspx")  //设置爬取数据的页面
                //.addPipeline(new FilePipeline("C:\\Users\\tree\\Desktop\\result"))//设置结果存放位置，默认输出到控制台
                .thread(5)
                .run();


        /*
        //测试去重过滤器
        Spider spider = Spider.create(new JobProcessor())
                .addUrl("https://www.jd.com/moreSubject.aspx")  //设置爬取数据的页面
                //.addPipeline(new FilePipeline("C:\\Users\\tree\\Desktop\\result"))//设置结果存放位置，默认输出到控制台
                .thread(5)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)));//设置布隆去重过滤器，指定最多对1000万数据进行去重操作

        Scheduler scheduler = spider.getScheduler();//加上这句，可以在此打断点，然后查看使用的去重过滤器

        //执行爬虫
        spider.run();
        */
    }
}
