package ru.serge2nd.samples.hib.atmks.config;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import ru.serge2nd.samples.hib.atmks.data.Identifiable;

import java.io.Serializable;

public class SequenceOrExistingIdGenerator extends SequenceStyleGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object entity) throws HibernateException {
        Serializable id;

        if (entity instanceof Identifiable && (
            id = ((Identifiable<?>)entity).getId()) != null) {
            return id;
        }

        return super.generate(session, entity);
    }
}
