package com.manage.config;

import com.github.pagehelper.PageHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author luya
 * @created 2018/05/23
 */
@EnableTransactionManagement(proxyTargetClass = true)
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisConfiguration {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @ConditionalOnMissingBean(PageHelper.class)
    public PageHelper pageHelper(){
        // 添加分页组件
        PageHelper pageHelper = new PageHelper();
        Properties pageHelperProperties = new Properties();
        //reasonable : Rationalization of paging parameters, Default value is false。 When this parameter is set to true,pageNum <= 0 will query the first page, PageNum> pages (over the total number), will query the last page. Default false, the query directly based on parameters
        pageHelperProperties.setProperty("reasonable", "true");
        pageHelperProperties.setProperty("dialect", "mysql");
        pageHelperProperties.setProperty("offsetAsPageNum", "true");
        pageHelperProperties.setProperty("rowBoundsWithCount", "true");
        pageHelperProperties.setProperty("supportMethodsArguments", "true");
        pageHelper.setProperties(pageHelperProperties);
        return pageHelper;
    }
}
