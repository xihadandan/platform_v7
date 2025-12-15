package org.bouncycastle2.math.ec.endo;

import org.bouncycastle2.math.ec.ECPointMap;

public interface ECEndomorphism
{
    ECPointMap getPointMap();

    boolean hasEfficientPointMap();
}
