package org.bouncycastle2.asn1.test;

import java.io.IOException;

import org.bouncycastle2.asn1.ASN1Primitive;
import org.bouncycastle2.asn1.icao.CscaMasterList;
import org.bouncycastle2.util.Arrays;
import org.bouncycastle2.util.io.Streams;
import org.bouncycastle2.util.test.SimpleTest;

public class CscaMasterListTest
    extends SimpleTest
{
    public String getName()
    {
        return "CscaMasterList";
    }

    public void performTest()
        throws Exception
    {
        byte[] input = getInput("masterlist-content.data");
        CscaMasterList parsedList
            = CscaMasterList.getInstance(ASN1Primitive.fromByteArray(input));

        if (parsedList.getCertStructs().length != 3)
        {
            fail("Cert structure parsing failed: incorrect length");
        }

        byte[] output = parsedList.getEncoded();
        if (!Arrays.areEqual(input, output))
        {
            fail("Encoding failed after parse");
        }
    }

    private byte[] getInput(String name)
        throws IOException
    {
        return Streams.readAll(getClass().getResourceAsStream(name));
    }

    public static void main(
        String[] args)
    {
        runTest(new CscaMasterListTest());
    }
}
