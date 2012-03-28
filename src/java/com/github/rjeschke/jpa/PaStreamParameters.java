/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.jpa;

public class PaStreamParameters
{
    private final int    device;
    private final int    channelCount;
    private final int    sampleFormat;
    private final double suggestedLatency;

    public PaStreamParameters(int device, int channelCount, PaSampleFormat sampleFormat, double suggestedLatency)
    {
        this.device           = device;
        this.channelCount     = channelCount;
        this.sampleFormat     = sampleFormat.getValue();
        this.suggestedLatency = suggestedLatency;
    }

    public int getDevice()
    {
        return this.device;
    }

    public int getChannelCount()
    {
        return this.channelCount;
    }

    public PaSampleFormat getSampleFormat()
    {
        return PaSampleFormat.fromValue(this.sampleFormat);
    }

    public double getSuggestedLatency()
    {
        return this.suggestedLatency;
    }
}
