package ru.serge2nd.samples.hib.atmks.config;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import ru.serge2nd.samples.hib.atmks.data.Identifiable;

import java.io.Serializable;

public class SequenceOrExistingIdGenerator extends SequenceStyleGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object e) throws HibernateException {
        if (e instanceof Identifiable<?> i)
            return i.getId();
        return super.generate(session, e);
    }
}
