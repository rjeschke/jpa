/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.jpa;

public class PaDeviceInfo
{
    private String name;
    private int    hostApi;
    private int    maxInputChannels;
    private int    maxOutputChannels;
    private double defaultLowInputLatency;
    private double defaultLowOutputLatency;
    private double defaultHighInputLatency;
    private double defaultHighOutputLatency;
    private double defaultSampleRate;

    private PaDeviceInfo()
    {
        // Object creation from JNI    
    }

    public String getName()
    {
        return this.name;
    }

    public int getHostApi()
    {
        return this.hostApi;
    }

    public int getMaxInputChannels()
    {
        return this.maxInputChannels;
    }

    public int getMaxOutputChannels()
    {
        return this.maxOutputChannels;
    }

    public double getDefaultLowInputLatency()
    {
        return this.defaultLowInputLatency;
    }

    public double getDefaultLowOutputLatency()
    {
        return this.defaultLowOutputLatency;
    }

    public double getDefaultHighInputLatency()
    {
        return this.defaultHighInputLatency;
    }

    public double getDefaultHighOutputLatency()
    {
        return this.defaultHighOutputLatency;
    }

    public double getDefaultSampleRate()
    {
        return this.defaultSampleRate;
    }
}
