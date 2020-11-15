package ru.serge2nd.samples.hib.atmks.config;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import static ru.serge2nd.ObjectAssist.nullSafe;

/** Transaction configuration */
@Configuration
public class TxConfig extends AbstractJtaPlatform {

    /** Transaction manager context */
    @Bean(initMethod = "init", destroyMethod = "shutdownForce")
    @SuppressWarnings("ContextJavaBeanUnresolvedMethodsInspection")
    UserTransactionService userTransactionService() { return new UserTransactionServiceImp(); }

    /** Transaction manager */
    @Bean(initMethod = "init", destroyMethod = "close")
    @SuppressWarnings("ContextJavaBeanUnresolvedMethodsInspection")
    TransactionManager userTransactionManager() {
        nullSafe(userTransactionService(), "no user transaction service");
        var utm = new UserTransactionManager();

        utm.setStartupTransactionService(false);
        utm.setForceShutdown(true);
        return utm;
    }

    /** Spring transaction manager adapter */
    @Bean PlatformTransactionManager transactionManager(TransactionManager utm) {
        return new JtaTransactionManager((UserTransaction)utm, utm);
    }

    @Override
    protected TransactionManager locateTransactionManager() { return userTransactionManager(); }
    @Override
    protected UserTransaction    locateUserTransaction()    { return (UserTransaction)userTransactionManager(); }
}
