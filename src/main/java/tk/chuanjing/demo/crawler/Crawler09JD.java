package tk.chuanjing.demo.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tk.chuanjing.demo.crawler_jd.pojo.Item;
import tk.chuanjing.demo.crawler_jd.util.HttpUtils;

import java.util.Date;

/**
 * @author ChuanJing
 * @date 2018/11/22 1:16
 */
public class Crawler09JD {

    private HttpUtils httpUtils = new HttpUtils();

    private static final ObjectMapper MAPPER =  new ObjectMapper();

    public static void main(String[] args) {
        Crawler09JD crawler09JD = new Crawler09JD();
        try {
            crawler09JD.crawlerJD();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void crawlerJD() throws Exception {
        //声明需要解析的初始地址
        //手机
        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=%E6%89%8B%E6%9C%BA&cid2=653&cid3=655&s=113&click=0&page=";
        //电视，没有SPU
        //String url = "https://search.jd.com/Search?keyword=%E7%94%B5%E8%A7%86&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=%E7%94%B5%E8%A7%86&stock=1&s=117&click=0&page=";

        //按照页面对手机的搜索结果进行遍历解析
        for (int i = 1; i < 4; i=i+2) {
            String html = httpUtils.doGetHtml(url + i);

            //解析页面，获取商品数据并存储
            parse(html);
        }

        System.out.println("-----------------------------数据抓取完成！-----------------------------");
    }

    //解析页面，获取商品数据并存储
    private void parse(String html) throws Exception {
        //解析html获取Document
        Document doc = Jsoup.parse(html);

        //获取spu信息
        Elements spuEles = doc.select("div#J_goodsList > ul > li");

        for(Element spuEle : spuEles) {
            //获取spu
            long spu = Long.parseLong(spuEle.attr("data-spu"));

            //获取sku信息
            Elements skuEles = spuEle.select("li.ps-item");

            for(Element skuEle : skuEles) {
                //获取sku
                long sku = Long.parseLong(skuEle.select("[data-sku]").attr("data-sku"));

                //根据sku查询商品数据
                Item item = new Item();
                item.setSku(sku);
                /*
                List<Item> list = itemService.findAll(item);

                if (list != null && list.size() > 0) {
                    //如果商品存在，就进行下一个循环，该商品不保存，因为已存在
                    continue;
                }
                */

                //设置商品的spu
                item.setSpu(spu);

                //获取商品的详情的url
                String itemUrl = "https://item.jd.com/" + sku + ".html";
                item.setUrl(itemUrl);

                //获取商品的图片
                String picUrl = "https:" + skuEle.select("img[data-sku]").first().attr("data-lazy-img");
                picUrl = picUrl.replace("/n9/", "/n1/");
                String picName = httpUtils.doGetImage(picUrl);
                if(picName != null && picName!= "") {
                    item.setPic(picName);
                }

                //获取商品的价格
                String priceJson = httpUtils.doGetHtml("https://p.3.cn/prices/mgets?skuIds=J_" + sku);
                double price = MAPPER.readTree(priceJson).get(0).get("p").asDouble();
                item.setPrice(price);

                //获取商品的标题
                String itemInfo = httpUtils.doGetHtml(item.getUrl());
                String title = Jsoup.parse(itemInfo).select("div.sku-name").text();
                item.setTitle(title);

                item.setCreated(new Date());
                item.setUpdated(item.getCreated());

                //保存商品数据到数据库中
                //itemService.save(item);
                System.out.println("抓取到数据：：" + item);//先不保存到数据库，打印出来看看
            }
        }

    }
}
