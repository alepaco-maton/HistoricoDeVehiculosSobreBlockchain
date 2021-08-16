/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.security.jwt;

import com.zaxxer.hikari.HikariConfig;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Configuration
@EnableTransactionManagement
public class JpaConfig extends HikariConfig {

    @Bean(name = "customDataSource")
    @ConfigurationProperties("spring.datasource")
    public DataSource customDataSource() {
        return DataSourceBuilder.create().build();
    }

//    @Bean
//    public AbstractEntityManagerFactoryBean entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean em
//                = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(customDataSource());
//        em.setPackagesToScan(new String[]{"bo.com.micrium.modulobase.model"});
//
//        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
////        em.setJpaProperties(additionalProperties());
//
//        return em;
//    }

//    @Bean //Creates our PlatformTransactionManager. Registering both the EntityManagerFactory and the DataSource to be shared by the EMF and JDBCTemplate
//    public PlatformTransactionManager transactionManager(EntityManagerFactory emf, DataSource dataSource) {
//        JpaTransactionManager tm = new JpaTransactionManager(emf);
//        tm.setDataSource(dataSource);
//        return tm;
//    }

//    Properties additionalProperties() {
//        Properties properties = new Properties();
////        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
////        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
//        return properties;
//    }
}
