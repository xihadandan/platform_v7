package org.bouncycastle2.math.ec;

public interface PreCompCallback
{
    PreCompInfo precompute(PreCompInfo existing);
}
