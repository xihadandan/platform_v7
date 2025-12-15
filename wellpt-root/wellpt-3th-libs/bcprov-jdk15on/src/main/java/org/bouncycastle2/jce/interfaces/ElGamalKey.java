package org.bouncycastle2.jce.interfaces;

import javax.crypto.interfaces.DHKey;

import org.bouncycastle2.jce.spec.ElGamalParameterSpec;

public interface ElGamalKey
    extends DHKey
{
    public ElGamalParameterSpec getParameters();
}
