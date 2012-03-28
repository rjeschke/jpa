/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.jpa;

public enum PaHostApiTypeId
{
    paInDevelopment(0),
    paDirectSound(1),
    paMME(2),
    paASIO(3),
    paSoundManager(4),
    paCoreAudio(5),
    paOSS(7),
    paALSA(8),
    paAL(9),
    paBeOS(10),
    paWDMKS(11),
    paJACK(12),
    paWASAPI(13),
    paAudioScienceHPI(14), 	
    paUnknown(-1)
    ;

    private final int value;

    private PaHostApiTypeId(int value)
    {
        this.value = value;
    }

    public static PaHostApiTypeId fromValue(int value)
    {
        PaHostApiTypeId[] codes = PaHostApiTypeId.values();

        for(int i = 0; i < codes.length; i++)
        {
            if(value == codes[i].value)
                return codes[i];
        }
        return PaHostApiTypeId.paUnknown;
    }

    public int getValue()
    {
        return this.value;
    }
}
