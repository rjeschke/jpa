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
