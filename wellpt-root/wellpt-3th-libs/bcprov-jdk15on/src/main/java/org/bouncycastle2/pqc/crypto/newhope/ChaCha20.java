package org.bouncycastle2.pqc.crypto.newhope;

import org.bouncycastle2.crypto.engines.ChaChaEngine;
import org.bouncycastle2.crypto.params.KeyParameter;
import org.bouncycastle2.crypto.params.ParametersWithIV;

class ChaCha20
{
    static void process(byte[] key, byte[] nonce, byte[] buf, int off, int len)
    {
        ChaChaEngine e = new ChaChaEngine(20);
        e.init(true, new ParametersWithIV(new KeyParameter(key), nonce));
        e.processBytes(buf, off, len, buf, off);
    }
}
