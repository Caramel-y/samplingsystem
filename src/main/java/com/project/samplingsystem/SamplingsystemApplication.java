package com.project.samplingsystem;

import com.project.samplingsystem.dao.annotation.MybatisDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**

 * @author wuwenbin
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "com.project.samplingsystem.dao", annotationClass = MybatisDao.class)
@EnableCaching

public class SamplingsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SamplingsystemApplication.class, args);
    }

}
