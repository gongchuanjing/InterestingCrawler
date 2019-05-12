package tk.chuanjing.demo.crawler.job.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import tk.chuanjing.demo.crawler.job.pojo.JobInfo;

public interface JobInfoDao extends JpaRepository<JobInfo, Long> {
}
