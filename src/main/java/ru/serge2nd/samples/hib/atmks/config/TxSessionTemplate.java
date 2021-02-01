package ru.serge2nd.samples.hib.atmks.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class TxSessionTemplate {
    private final HibernateTemplate emTmpl;
    private final TransactionTemplate txTmpl;

    @Autowired
    public TxSessionTemplate(EntityManagerFactory emf, PlatformTransactionManager tm) {
        this(emf, tm, (emTmpl, txTmpl) -> {});
    }
    public TxSessionTemplate(EntityManagerFactory emf, PlatformTransactionManager tm, BiConsumer<HibernateTemplate, TransactionTemplate> cfg) {
        this(new HibernateTemplate(emf.unwrap(SessionFactory.class)), new TransactionTemplate(tm));
        cfg.accept(emTmpl, txTmpl);
    }

    public <R> R from(BiFunction<Session, TransactionStatus, R> action) {
        return txTmpl.execute(tx -> emTmpl.execute(session -> action.apply(session, tx)));
    }
    public void with(BiConsumer<Session, TransactionStatus> action) {
        txTmpl.execute(tx -> emTmpl.execute(session -> {
            action.accept(session, tx);
            return null;
        }));
    }
}
