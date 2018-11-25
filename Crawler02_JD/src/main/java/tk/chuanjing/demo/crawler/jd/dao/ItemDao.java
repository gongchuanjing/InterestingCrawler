package tk.chuanjing.demo.crawler.jd.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import tk.chuanjing.demo.crawler.jd.pojo.Item;

public interface ItemDao extends JpaRepository<Item,Long> {
}
