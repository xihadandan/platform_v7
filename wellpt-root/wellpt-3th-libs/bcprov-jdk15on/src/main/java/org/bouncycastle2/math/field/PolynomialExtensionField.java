package org.bouncycastle2.math.field;

public interface PolynomialExtensionField extends ExtensionField
{
    Polynomial getMinimalPolynomial();
}
