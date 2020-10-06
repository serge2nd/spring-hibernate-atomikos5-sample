package ru.serge2nd.samples.hib.atmks;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.serge2nd.samples.hib.atmks.config.DataConfig;
import ru.serge2nd.samples.hib.atmks.config.TxConfig;
import ru.serge2nd.samples.hib.atmks.config.TxSessionTemplate;

@ExtendWith(SpringExtension.class)
@TestPropertySource({"classpath:application-test.properties", "classpath:application-test-h2.properties"})
@ContextConfiguration(classes = {DataConfig.class, TxConfig.class, TxSessionTemplate.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class H2Test extends AbstractDbTest {
}
