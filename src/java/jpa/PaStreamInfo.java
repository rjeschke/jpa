/*
* Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
* See LICENSE.txt for licensing information.
*/
package jpa;

public class PaStreamInfo
{
    private double inputLatency;
    private double outputLatency;
    private double sampleRate;
    
    private PaStreamInfo()
    {
        // Object creation from JNI
    }
    
    public double getInputLatency()
    {
        return this.inputLatency;
    }

    public double getOutputLatency()
    {
        return this.outputLatency;
    }
    
    public double getSampleRate()
    {
        return this.sampleRate;
    }
}
