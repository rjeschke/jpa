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

public enum PaError
{
    paNoError(0), paNotInitialized(-10000), paUnanticipatedHostError(-9999), paInvalidChannelCount(-9998), paInvalidSampleRate(
            -9997), paInvalidDevice(-9996), paInvalidFlag(-9995), paSampleFormatNotSupported(-9994), paBadIODeviceCombination(
            -9993), paInsufficientMemory(-9992), paBufferTooBig(-9991), paBufferTooSmall(-9990), paNullCallback(-9989), paBadStreamPtr(
            -9988), paTimedOut(-9987), paInternalError(-9986), paDeviceUnavailable(-9985), paIncompatibleHostApiSpecificStreamInfo(
            -9984), paStreamIsStopped(-9983), paStreamIsNotStopped(-9982), paInputOverflowed(-9981), paOutputUnderflowed(-9980), paHostApiNotFound(
            -9979), paInvalidHostApi(-9978), paCanNotReadFromACallbackStream(-9977), paCanNotWriteToACallbackStream(-9976), paCanNotReadFromAnOutputOnlyStream(
            -9975), paCanNotWriteToAnInputOnlyStream(-9974), paIncompatibleStreamHostApi(-9973), paBadBufferPtr(-9972), paUnknown(
            -1);

    private final int value;

    private PaError(int value)
    {
        this.value = value;
    }

    public static PaError fromValue(int value)
    {
        PaError[] codes = PaError.values();

        for (int i = 0; i < codes.length; i++)
        {
            if (value == codes[i].value) return codes[i];
        }
        return PaError.paUnknown;
    }

    public int getValue()
    {
        return this.value;
    }
}
