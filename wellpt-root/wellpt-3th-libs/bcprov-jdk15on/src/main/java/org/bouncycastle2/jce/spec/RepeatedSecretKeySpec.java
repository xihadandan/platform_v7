package org.bouncycastle2.jce.spec;

/**
 * A simple object to indicate that a symmetric cipher should reuse the
 * last key provided.
 * @deprecated use super class org.bouncycastle2.jcajce.spec.RepeatedSecretKeySpec
 */
public class RepeatedSecretKeySpec
    extends org.bouncycastle2.jcajce.spec.RepeatedSecretKeySpec
{
    private String algorithm;

    public RepeatedSecretKeySpec(String algorithm)
    {
        super(algorithm);
    }
}
