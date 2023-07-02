package cn.vincent.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ShardingStrategyConfiguration;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@Configuration
@MapperScan(basePackages = "cn.vincent.dao.mapper")
public class ShardingJdbcConfig {

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        return factoryBean.getObject();
    }

    /**
     * 设置sharding数据源
     *
     * @return
     * @throws SQLException
     */
    @Bean
    @Primary
    public DataSource dataSource() throws SQLException {
        Properties properties = new Properties();
        // 开启日志
        properties.setProperty("sql.show", "true");
        // 创建sharding数据源 参数是数据源Map、分片策略对象、配置属性
        ShardingDataSource shardingDataSource = new ShardingDataSource(dataSourceMap(), shardingRule(), properties);
        return shardingDataSource;
    }

    //region 分片策略
    private ShardingRule shardingRule() {
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        shardingRuleConfiguration.setTableRuleConfigs(tableRuleConfigurations());
        ShardingRule shardingRule = new ShardingRule(shardingRuleConfiguration, dataSourceNames());
        return shardingRule;
    }

    private Collection<TableRuleConfiguration> tableRuleConfigurations() {
        ArrayList<TableRuleConfiguration> configurations = new ArrayList<TableRuleConfiguration>();

        //region order表
        //创建表分片策略对象 TableRuleConfiguration 设置逻辑表名 和 表名规则
        TableRuleConfiguration t_order =
                new TableRuleConfiguration("t_order", "sharding-$->{1..2}.order_$->{0..1}");

        //设置分表策略 分片键和算法
        ShardingStrategyConfiguration shardingTableStrategy =
                new InlineShardingStrategyConfiguration("id", "order_$->{id % 2}");
        //设置分库策略 分片键和算法
        ShardingStrategyConfiguration shardingDatabaseStrategy =
                new InlineShardingStrategyConfiguration("id", "sharding-$->{id % 2 + 1}");
        //指定order表的主键字段和生成策略
        KeyGeneratorConfiguration keyGeneratorConfiguration = new KeyGeneratorConfiguration("SNOWFLAKE", "id");

        t_order.setTableShardingStrategyConfig(shardingTableStrategy);
        t_order.setDatabaseShardingStrategyConfig(shardingDatabaseStrategy);
        t_order.setKeyGeneratorConfig(keyGeneratorConfiguration);
        configurations.add(t_order);
        //endregion

        return configurations;
    }
    //endregion

    //region 数据源
    private Collection<String> dataSourceNames() {
        List<String> list = new ArrayList<String>() {
            {
                add("sharding-1");
                add("sharding-2");
            }
        };
        return list;
    }

    private Map<String, DataSource> dataSourceMap() {
        Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>() {
            {
                put("sharding-1", sharding_1());
                put("sharding-2", sharding_2());
            }
        };
        return dataSourceMap;
    }

    @Value("${datasource.sharding-1.username}")
    private String username_1;
    @Value("${datasource.sharding-1.password}")
    private String password_1;
    @Value("${datasource.sharding-1.jdbc-url}")
    private String url_1;
    @Value("${datasource.sharding-1.driver-class-name}")
    private String driverClassName_1;

    private DataSource sharding_1() {
        HikariDataSource hikari = new HikariDataSource();
        hikari.setDriverClassName(driverClassName_1);
        hikari.setJdbcUrl(url_1);
        hikari.setUsername(username_1);
        hikari.setPassword(password_1);
        return hikari;
    }

    @Value("${datasource.sharding-2.username}")
    private String username_2;
    @Value("${datasource.sharding-2.password}")
    private String password_2;
    @Value("${datasource.sharding-2.jdbc-url}")
    private String url_2;
    @Value("${datasource.sharding-2.driver-class-name}")
    private String driverClassName_2;

    private DataSource sharding_2() {
        HikariDataSource hikari = new HikariDataSource();
        hikari.setDriverClassName(driverClassName_2);
        hikari.setJdbcUrl(url_2);
        hikari.setUsername(username_2);
        hikari.setPassword(password_2);
        return hikari;
    }
    //endregion
}