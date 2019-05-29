package tk.chuanjing.demo.crawler.job.task;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.chuanjing.demo.crawler.job.pojo.JobInfo;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * @author ChuanJing
 * @date 2019/5/12 11:34
 */
@Component
public class JobProcessor implements PageProcessor {

    private String url = "https://search.51job.com/list/000000,000000,0000,32%252C01,9,99,java,2," +
            "1.html?lang=c&stype=&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99" +
            "&providesalary=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line" +
            "=&specialarea=00&from=&welfare=";

    @Override
    public void process(Page page) {
        //解析页面，获取招聘信息详情的url地址
        List<Selectable> list = page.getHtml().css("div#resultList div.el").nodes();

        //判断获取到的集合是否为空
        if(list.size() == 0) {
            // 如果为空，表示这是招聘详情页,解析页面，获取招聘详情信息，保存数据
            saveJobInfo(page);
        } else {
            //如果不为空，表示这是列表页,解析出详情页的url地址，放到任务队列中
            for (Selectable s : list) {
                //获取url地址
                String jobInfoUrl = s.links().toString();
                //把获取到的url地址放到任务队列中
                page.addTargetRequest(jobInfoUrl);
            }

            //获取下一页的url
            String bkUrl = page.getHtml().css("div.p_in li.bk").nodes().get(1).links().toString();
            //把url放到任务队列中
            page.addTargetRequest(bkUrl);
        }
    }

    /**
     * 解析页面，获取招聘详情信息，保存数据
     * @param page
     */
    private void saveJobInfo(Page page) {
        //创建招聘详情对象
        JobInfo jobInfo  = new JobInfo();

        //解析页面
        Html html = page.getHtml();

        //获取数据，封装到对象中
        jobInfo.setCompanyName(html.css("div.cn p.cname a","text").toString());
        jobInfo.setCompanyAddr(Jsoup.parse(html.css("div.bmsg").nodes().get(1).toString()).text());
        jobInfo.setCompanyInfo(Jsoup.parse(html.css("div.tmsg").toString()).text());
        jobInfo.setJobName(html.css("div.cn h1","text").toString());
        jobInfo.setJobInfo(Jsoup.parse(html.css("div.job_msg").toString()).text());
        jobInfo.setUrl(page.getUrl().toString());

        //设置地址
        //jobInfo.setJobAddr(html.css("div.cn span.lname","text").toString());//51job老的页面
        /*
        <p class="msg ltype" title="上海-浦东新区&nbsp;&nbsp;|&nbsp;&nbsp;无工作经验&nbsp;&nbsp;|&nbsp;&nbsp;招1人&nbsp;&nbsp;|&nbsp;&nbsp;05-12发布">
				上海-浦东新区&nbsp;&nbsp;<span>|</span>&nbsp;&nbsp;无工作经验&nbsp;&nbsp;<span>|</span>&nbsp;&nbsp;招1人&nbsp;&nbsp;<span>|</span>&nbsp;&nbsp;05-12发布
		</p>
         */
        String content = html.css("p.msg", "text").toString();
        int index = content.indexOf("  ");//这里不是普通的空格，要复制过来
        String substring = content.substring(0, index);
        //jobInfo.setJobAddr(content.substring(0, content.indexOf("  ")));
        jobInfo.setJobAddr(substring);

        //获取薪资
        Integer[] salary = MathSalary.getSalary(html.css("div.cn strong", "text").toString());
        jobInfo.setSalaryMin(salary[0]);
        jobInfo.setSalaryMax(salary[1]);

        //获取发布时间
        //String time = Jsoup.parse(html.css("div.t1 span").regex(".*发布").toString()).text();//51job老的页面
        //“ 东莞-虎门镇    无工作经验    招5人    05-12发布    算机信息管    普通 ”，注意后面有个空格
        String[] split = content.split("发布");//split[0] = “ 东莞-虎门镇    无工作经验    招5人    05-12”
        String time = split[0].substring(split[0].length()-5);
        jobInfo.setTime(time);

        //把结果保存起来
        page.putField("jobInfo", jobInfo);
    }

    private Site site = Site.me()
                            .setCharset("gbk")          //设置编码
                            .setTimeOut(10 * 1000)      //设置超时时间
                            .setRetrySleepTime(3000)    //设置重试的间隔时间
                            .setRetryTimes(3);          //设置重试的次数

    @Override
    public Site getSite() {
        return site;
    }

    @Autowired
    private SpringDataPipeline springDataPipeline;

    /**
     * initialDelay当任务启动后，等等多久执行方法
     * fixedDelay每隔多久执行方法
     */
    @Scheduled(initialDelay = 1000, fixedDelay = 100 * 1000)
    public void process() {
        System.out.println("--------------------------开始执行定时任务--------------------");
        Spider.create(new JobProcessor())
                .addUrl(url)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                .thread(10)
                .addPipeline(springDataPipeline)
                .run();
    }
}
