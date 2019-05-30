package tk.chuanjing.demo;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ChuanJing
 * @date 2019/5/30 1:11
 *
 * 使用Tess4J尝试文字识别
 */
public class ImageDetect {

    private static String path = "E:\\Workspace\\WorkspaceIntelliJ_IDEA\\InterestingCrawler\\";

    public static void main(String[] args) throws TesseractException {
        normal();
        //testVerify();
    }

    /**
     * 识别规整的文字
     */
    private static void normal() {
        File imageFile = new File(path + "Crawler01_Demo\\src\\main\\resources\\ImageDetect_ZhongWen.png");
        Tesseract tessreact = new Tesseract();
        //需要指定训练集，训练集到 https://github.com/tesseract-ocr/tessdata 下载。下载后必须放在\tess4j\tessdata文件夹下
        tessreact.setDatapath(path + "Crawler01_Demo\\src\\main\\resources\\tess4j\\tessdata");
        //注意  默认是英文识别，如果做中文识别，需要单独设置。
        tessreact.setLanguage("chi_sim");
        try {
            String result = tessreact.doOCR(imageFile);
            System.out.println("识别结果：" + result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * 尝试识别中文验证码
     * @throws TesseractException
     */
    public static void testVerify() throws TesseractException {
        File imageFile = new File(path + "Crawler01_Demo\\src\\main\\resources\\ImageDetect_Verify.png");
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        instance.setLanguage("chi_sim"); //加载语言包
        instance.setDatapath(path + "Crawler01_Demo\\src\\main\\resources\\tess4j\\tessdata");
        String result = instance.doOCR(change(imageFile));
        System.out.println("验证码识别结果：" + result);
    }

    public static BufferedImage change(File file) {
        // 读取图片字节数组
        BufferedImage textImage = null;
        try {
            InputStream in = new FileInputStream(file);
            BufferedImage image = ImageIO.read(in);
            textImage = ImageHelper.convertImageToGrayscale(ImageHelper.getSubImage(image, 0, 0, image.getWidth(), image.getHeight()));  //对图片进行处理
            textImage = ImageHelper.getScaledInstance(image, image.getWidth() * 5, image.getHeight() * 5);  //将图片扩大5倍

        } catch (IOException e) {
            e.printStackTrace();
        }

        return textImage;
    }
}
