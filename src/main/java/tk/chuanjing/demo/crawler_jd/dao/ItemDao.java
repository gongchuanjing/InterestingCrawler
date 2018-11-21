package tk.chuanjing.demo.crawler_jd.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import tk.chuanjing.demo.crawler_jd.pojo.Item;

public interface ItemDao extends JpaRepository<Item,Long> {
}
