package org.bouncycastle2.crypto.test;

import org.bouncycastle2.crypto.engines.SkipjackEngine;
import org.bouncycastle2.crypto.params.KeyParameter;
import org.bouncycastle2.util.encoders.Hex;
import org.bouncycastle2.util.test.SimpleTest;

/**
 */
public class SkipjackTest
    extends CipherTest
{
    static SimpleTest[]  tests = 
            {
                new BlockCipherVectorTest(0, new SkipjackEngine(),
                        new KeyParameter(Hex.decode("00998877665544332211")),
                        "33221100ddccbbaa", "2587cae27a12d300")
            };

    SkipjackTest()
    {
        super(tests, new SkipjackEngine(), new KeyParameter(Hex.decode("00998877665544332211")));
    }

    public String getName()
    {
        return "SKIPJACK";
    }

    public static void main(
        String[]    args)
    {
        runTest(new SkipjackTest());
    }
}
