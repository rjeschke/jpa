/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.jpa;

public enum PaSampleFormat
{
    paFloat32 (0x00000001),
    paInt32   (0x00000002),
    paInt16   (0x00000008),
    paInt8    (0x00000010),
    paUInt8   (0x00000020),
    paUnknown         (-1);

    private final int value;

    private PaSampleFormat(int value)
    {
        this.value = value;
    }

    public static PaSampleFormat fromValue(int value)
    {
        PaSampleFormat[] codes = PaSampleFormat.values();

        for(int i = 0; i < codes.length; i++)
        {
            if(value == codes[i].value)
                return codes[i];
        }
        return PaSampleFormat.paUnknown;
    }

    public int getValue()
    {
        return this.value;
    }
}
