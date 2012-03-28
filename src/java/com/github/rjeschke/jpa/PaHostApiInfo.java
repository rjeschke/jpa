/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.jpa;

public class PaHostApiInfo
{
    private int    type;
    private String name;
    private int    deviceCount;
    private int    defaultInputDevice;
    private int    defaultOutputDevice;

    private PaHostApiInfo()
    {
        // Object creation from JNI
    }

    public PaHostApiType getType()
    {
        return PaHostApiType.fromValue(this.type);
    }

    public String getName()
    {
        return this.name;
    }

    public int getDeviceCount()
    {
        return this.deviceCount;
    }

    public int getDefaultInputDevice()
    {
        return this.defaultInputDevice;
    }

    public int getDefaultOutputDevice()
    {
        return this.defaultOutputDevice;
    }
}
