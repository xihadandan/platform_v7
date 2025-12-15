package org.bouncycastle2.crypto.agreement;

import java.math.BigInteger;

import org.bouncycastle2.crypto.CipherParameters;
import org.bouncycastle2.crypto.params.DHUPrivateParameters;
import org.bouncycastle2.crypto.params.DHUPublicParameters;
import org.bouncycastle2.util.Arrays;
import org.bouncycastle2.util.BigIntegers;

/**
 * FFC Unified static/ephemeral agreement as described in NIST SP 800-56A.
 */
public class DHUnifiedAgreement
{
    private DHUPrivateParameters privParams;

    public void init(
        CipherParameters key)
    {
        this.privParams = (DHUPrivateParameters)key;
    }

    public int getFieldSize()
    {
        return (privParams.getStaticPrivateKey().getParameters().getP().bitLength() + 7) / 8;
    }

    public byte[] calculateAgreement(CipherParameters pubKey)
    {
        DHUPublicParameters pubParams = (DHUPublicParameters)pubKey;

        DHBasicAgreement sAgree = new DHBasicAgreement();
        DHBasicAgreement eAgree = new DHBasicAgreement();

        sAgree.init(privParams.getStaticPrivateKey());

        BigInteger sComp = sAgree.calculateAgreement(pubParams.getStaticPublicKey());

        eAgree.init(privParams.getEphemeralPrivateKey());

        BigInteger eComp = eAgree.calculateAgreement(pubParams.getEphemeralPublicKey());

        return Arrays.concatenate(
            BigIntegers.asUnsignedByteArray(this.getFieldSize(), eComp),
            BigIntegers.asUnsignedByteArray(this.getFieldSize(), sComp));
    }
}
