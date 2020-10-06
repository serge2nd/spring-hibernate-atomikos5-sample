package ru.serge2nd.samples.hib.atmks.data;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {
    T getId();
}
