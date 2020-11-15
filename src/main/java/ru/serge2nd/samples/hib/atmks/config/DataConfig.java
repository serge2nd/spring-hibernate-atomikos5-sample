package ru.serge2nd.samples.hib.atmks.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.serge2nd.collection.HardProperties;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;
import static org.hibernate.cfg.AvailableSettings.JPA_VALIDATION_FACTORY;
import static org.hibernate.cfg.AvailableSettings.JTA_PLATFORM;
import static org.springframework.orm.jpa.vendor.Database.H2;
import static org.springframework.orm.jpa.vendor.Database.valueOf;
import static ru.serge2nd.ObjectAssist.nullSafe;
import static ru.serge2nd.bean.PropertyUtil.propertyOrigin;
import static ru.serge2nd.bean.PropertyUtil.propertySuffixOrigin;
import static ru.serge2nd.stream.util.CollectingOptions.UNMODIFIABLE;
import static ru.serge2nd.stream.util.Collecting.collect;
import static ru.serge2nd.stream.MappingCollectors.aFlatMapToList;

/** JDBC & JPA configuration */
@Configuration
@RequiredArgsConstructor
public class DataConfig {
    static final String DATABASE         = "spring.jpa.database";
    static final String JTA_DATA_SOURCE  = "spring.jta.atomikos.datasource.unique-resource-name";
    static final String JTA_MAX_POOL     = "spring.jta.atomikos.datasource.max-pool-size";
    static final String JTA_CONN_TIMEOUT = "spring.jta.atomikos.datasource.borrow-connection-timeout";
    static final String JTA_ISOLATION    = "spring.jta.atomikos.datasource.default-isolation-level";
    static final String XA_CLASS         = "spring.jta.atomikos.datasource.xa-data-source-class-name";
    final ConfigurableEnvironment env;

    /** XA data source properties */
    @Bean Map<String, String> xaProps() { return asMap
    (       "spring.jta.atomikos.datasource.xa-properties."
    );}
    /** JPA properties */
    @Bean Map<String, String> jpaProps() { return properties
    (       "javax.persistence."
    ,       "hibernate."
    );}

    /** Persistent entity validator */
    @Bean LocalValidatorFactoryBean validatorFactory() {
        var v = new LocalValidatorFactoryBean();
        v.setMessageInterpolator(new ParameterMessageInterpolator());
        return v;
    }

    /** JDBC data source */
    @Bean(initMethod = "init", destroyMethod = "close")
    @SuppressWarnings("ContextJavaBeanUnresolvedMethodsInspection")
    DataSource jtaDataSource() {
        var jtaDs = new AtomikosDataSourceBean();

        jtaDs.setUniqueResourceName   (env.getProperty(JTA_DATA_SOURCE));
        jtaDs.setXaDataSourceClassName(env.getProperty(XA_CLASS));
        jtaDs.setDefaultIsolationLevel(env.getProperty(JTA_ISOLATION, Integer.class, TRANSACTION_READ_COMMITTED));

        jtaDs.setXaProperties(HardProperties.of(xaProps()));
        jtaDs.setMaxPoolSize            (nullSafe(env.getProperty(JTA_MAX_POOL, Integer.class), "no max pool size"));
        jtaDs.setBorrowConnectionTimeout(nullSafe(env.getProperty(JTA_CONN_TIMEOUT, Integer.class), "no connection timeout"));
        return jtaDs;
    }

    /** JPA entity manager factory */
    @Bean LocalContainerEntityManagerFactoryBean emf(JtaPlatform jtaPlatform) {
        var emf = new LocalContainerEntityManagerFactoryBean();
        var props = emf.getJpaPropertyMap();

        emf.setJtaDataSource   (jtaDataSource());
        emf.setJpaVendorAdapter(jpaVendorAdapter());

        props.putAll(jpaProps());
        props.put(JPA_VALIDATION_FACTORY, validatorFactory());
        props.put(JTA_PLATFORM          , jtaPlatform);
        return emf;
    }

    /** Spring JPA vendor adapter */
    @Bean JpaVendorAdapter jpaVendorAdapter() {
        var vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(valueOf(env.getProperty(DATABASE, H2.name())));
        return vendorAdapter;
    }

    /** Environment property names */
    @Bean List<String> envPropertyNames() {
        return collect(env.getPropertySources(), aFlatMapToList(ConfigHelper::propertyNames, UNMODIFIABLE));
    }

    Map<String, String> properties(String... prefixes) {
        return propertyOrigin(env::getProperty, prefixes).get(envPropertyNames());
    }
    Map<String, String> asMap(String... prefixes) {
        return propertySuffixOrigin(env::getProperty, prefixes).get(envPropertyNames());
    }
}
