package tk.chuanjing.demo.crawler_jd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.chuanjing.demo.crawler_jd.dao.ItemDao;
import tk.chuanjing.demo.crawler_jd.pojo.Item;
import tk.chuanjing.demo.crawler_jd.service.ItemService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDao itemDao;

    @Override
    @Transactional
    public void save(Item item) {
        itemDao.save(item);
    }

    @Override
    public List<Item> findAll(Item item) {
        //声明查询条件
        Example<Item> example = Example.of(item);

        //根据查询条件进行查询数据
        List<Item> list = itemDao.findAll(example);

        return list;
    }
}
