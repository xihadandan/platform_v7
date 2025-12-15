package org.bouncycastle2.jcajce;

import java.security.cert.CRL;
import java.util.Collection;

import org.bouncycastle2.util.Selector;
import org.bouncycastle2.util.Store;
import org.bouncycastle2.util.StoreException;

/**
 * Generic interface for a PKIX based CRL store.
 *
 * @param <T> the CRL type.
 */
public interface PKIXCRLStore<T extends CRL>
    extends Store<T>
{
    /**
     * Return the matches associated with the passed in selector.
     *
     * @param selector the selector defining the match criteria.
     * @return a collection of matches with the selector, an empty selector if there are none.
     * @throws StoreException in the event of an issue doing a match.
     */
    Collection<T> getMatches(Selector<T> selector)
        throws StoreException;
}
