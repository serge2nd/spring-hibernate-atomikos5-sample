package ru.serge2nd.samples.hib.atmks.data;

import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.lang.invoke.MethodHandles.lookup;
import static ru.serge2nd.ObjectAssist.errNotInstantiable;

public class Helpers {
    private Helpers() { throw errNotInstantiable(lookup()); }

    public static final int STRMAXLEN = 2000;

    public static <E> boolean has(Predicate<E> predicate, Supplier<Iterator<E>> its) {
        for (Iterator<E> it = its.get(); it.hasNext();)
            if (predicate.test(it.next()))
                return true;
        return false;
    }
}
