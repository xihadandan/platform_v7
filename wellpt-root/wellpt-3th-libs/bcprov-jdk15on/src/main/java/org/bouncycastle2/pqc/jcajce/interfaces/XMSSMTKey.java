package org.bouncycastle2.pqc.jcajce.interfaces;

public interface XMSSMTKey
{
    int getHeight();

    int getLayers();

    String getTreeDigest();
}
