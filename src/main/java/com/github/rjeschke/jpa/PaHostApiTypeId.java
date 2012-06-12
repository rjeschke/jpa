/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
