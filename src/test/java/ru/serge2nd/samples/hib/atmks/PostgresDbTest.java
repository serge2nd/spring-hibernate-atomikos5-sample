package ru.serge2nd.samples.hib.atmks;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.serge2nd.samples.hib.atmks.config.DataConfig;
import ru.serge2nd.samples.hib.atmks.config.TxConfig;
import ru.serge2nd.samples.hib.atmks.config.TxSessionTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@EnabledIf("#{systemProperties['PostgresDbTest'] != null}")
@ExtendWith(SpringExtension.class)
@TestPropertySource({"classpath:application-test.properties", "classpath:application-test-postgres.properties"})
@ContextConfiguration(classes = {DataConfig.class, TxConfig.class, TxSessionTemplate.class})
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext
public class PostgresDbTest extends AbstractDbTest {
    @Autowired PostgresDbTest(DataSource jtaDs) throws SQLException {
        // Avoid the PostgreSQL driver bug related to setting XA default isolation level
        jtaDs.unwrap(AtomikosDataSourceBean.class).setDefaultIsolationLevel(-1);
    }
}
