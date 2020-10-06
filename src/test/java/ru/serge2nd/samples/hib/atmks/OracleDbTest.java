package ru.serge2nd.samples.hib.atmks;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.serge2nd.samples.hib.atmks.config.DataConfig;
import ru.serge2nd.samples.hib.atmks.config.TxConfig;
import ru.serge2nd.samples.hib.atmks.config.TxSessionTemplate;

@EnabledIf("#{systemProperties['OracleDbTest'] != null}")
@ExtendWith(SpringExtension.class)
@TestPropertySource({"classpath:application-test.properties", "classpath:application-test-oracle.properties"})
@ContextConfiguration(classes = {DataConfig.class, TxConfig.class, TxSessionTemplate.class})
@TestInstance(Lifecycle.PER_CLASS)
@DirtiesContext
class OracleDbTest extends AbstractDbTest {
}
