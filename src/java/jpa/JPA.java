package jpa;

import java.nio.ByteBuffer;

import jpa.PaBuffer.Type;

public class JPA
{
    private JPA() { /* singleton */ }
    
    private static PaBuffer   output     = null;
    private static PaBuffer   input      = null;
    private static PaCallback paCallback = null;
    private static long       jpaPtr     = 0;
    
    static
    {
        // On windows, load portaudio_x86.dll
        LibraryLoader.loadNative("win", "jpa", "portaudio_x86.dll");
        // Load JNI library
        LibraryLoader.load("jpa", "jpa");
    }
    
    public final static int paNoDevice                              = -1;
    public final static int paUseHostApiSpecificDeviceSpecification = -2;
    
    public final static int paNoFlag                                = 0;
    public final static int paClipOff                               = 0x00000001;
    public final static int paDitherOff                             = 0x00000002;
    public final static int paNeverDropInput                        = 0x00000004;

    public static native int getVersion();
    public static native String getVersionText();
    public static native int getHostApiCount();
    public static native int getDefaultHostApi();
    public static native int getDeviceCount();
    public static native int getDefaultInputDevice();
    public static native int getDefaultOutputDevice();
    public static native PaDeviceInfo getDeviceInfo(int device);
    public static native PaHostApiInfo getHostApiInfo(int hostApi);
    public static native int hostApiTypeIdToHostApiIndex(int type);
    public static native int hostApiDeviceIndexToDeviceIndex(int hostApi, int hostApiDeviceIndex);
    public static native PaHostErrorInfo getLastHostErrorInfo();
    public static native void sleep(long msec);
    public static native long getDirectByteBufferPointer(ByteBuffer buffer);
    
    public static PaError initialize()
    {
        if(jpaPtr == 0)
            jpaPtr = dataAlloc();
        
        return PaError.fromValue(paInitialize());
    }
    
    public static PaError terminate()
    {
        dataFree(jpaPtr);
        jpaPtr = 0;
        return PaError.fromValue(paTerminate());
    }
    
    public static void setCallback(PaCallback callback)
    {
        paCallback = callback;
    }
    
    public static PaError isFormatSupported(PaStreamParameters inputParameters, PaStreamParameters outputParameters, double sampleRate)
    {
        return PaError.fromValue(paIsFormatSupported(inputParameters, outputParameters, sampleRate));
    }
    
    public static String getErrorText(PaError err)
    {
        return paGetErrorText(err.getValue());
    }
    
    public static PaStreamInfo getStreamInfo()
    {
        if(jpaPtr == 0L)
            throw new NullPointerException("Missing call to JPA.initialize()");
        
        return paGetStreamInfo(jpaPtr);
    }
    
    public static PaError openStream(PaStreamParameters inputParameters, PaStreamParameters outputParameters, double sampleRate, int framesPerBuffer, int streamFlags)
    {
        if(jpaPtr == 0L)
            throw new NullPointerException("Missing call to JPA.initialize()");

        final PaError err = PaError.fromValue(paOpenStream(jpaPtr, inputParameters, outputParameters, sampleRate, framesPerBuffer, streamFlags)); 

        if(err == PaError.paNoError)
        {
            input = inputParameters != null ? new PaBuffer(pa2BufferType(inputParameters.getSampleFormat()), inputParameters.getChannelCount()) : null;
            output = outputParameters != null ? new PaBuffer(pa2BufferType(outputParameters.getSampleFormat()), outputParameters.getChannelCount()) : null;
        }
        
        return err;
    }
    
    public static PaError openDefaultStream(int numInputChannels, int numOutputChannels, PaSampleFormat sampleFormat, double sampleRate, int framesPerBuffer)
    {
        if(jpaPtr == 0L)
            throw new NullPointerException("Missing call to JPA.initialize()");
        
        final PaError err = PaError.fromValue(paOpenDefaultStream(jpaPtr, numInputChannels, numOutputChannels, sampleFormat.getValue(), sampleRate, framesPerBuffer));
        
        if(err == PaError.paNoError)
        {
            input = numInputChannels > 0 ? new PaBuffer(pa2BufferType(sampleFormat), numInputChannels) : null;
            output = numOutputChannels > 0 ? new PaBuffer(pa2BufferType(sampleFormat), numOutputChannels) : null;
        }
        
        return err;
    }

    public static PaError closeStream()
    {
        if(jpaPtr == 0L)
            throw new NullPointerException("Missing call to JPA.initialize()");
        
        return PaError.fromValue(paCloseStream(jpaPtr));
    }
    
    public static PaError startStream()
    {
        if(jpaPtr == 0L)
            throw new NullPointerException("Missing call to JPA.initialize()");
        
        return PaError.fromValue(paStartStream(jpaPtr));
    }
    
    public static PaError stopStream()
    {
        if(jpaPtr == 0L)
            throw new NullPointerException("Missing call to JPA.initialize()");
        
        final PaError err = PaError.fromValue(paStopStream(jpaPtr));
        
        if(err == PaError.paNoError)
        {
            while(paIsStreamActive(jpaPtr) > 0 || paIsStreamStopped(jpaPtr) == 0)
            {
                try { Thread.sleep(5); } catch (InterruptedException e) { break; }
            }
        }
        
        return err;
    }
    
    public static PaError abortStream()
    {
        if(jpaPtr == 0L)
            throw new NullPointerException("Missing call to JPA.initialize()");
        
        return PaError.fromValue(paAbortStream(jpaPtr));
    }
    
    public static boolean isStreamActive()
    {
        return jpaPtr == 0L ? false : paIsStreamActive(jpaPtr) != 0;
    }

    public static boolean isStreamStopped()
    {
        return jpaPtr == 0L ? true : paIsStreamStopped(jpaPtr) != 0;
    }

    public static double getStreamCpuLoad()
    {
        return jpaPtr == 0L ? 0.0 : paGetStreamCpuLoad(jpaPtr);
    }

    public static int getSampleSize(PaSampleFormat format)
    {
        return paGetSampleSize(format.getValue());
    }
    
    public static double getStreamTime()
    {
        if(jpaPtr == 0L)
            throw new NullPointerException("Missing call to JPA.initialize()");
        
        return paGetStreamTime(jpaPtr);
    }

    // Gets called from native code to prepare the buffers
    @SuppressWarnings("unused")
	private static void resize(int frames)
    {
        if(output != null)
        {
            output.resize(frames);
            output.clear();
        }
        if(input  != null)
        {
            input.resize(frames);
            input.clear();
        }
    }

    // Gets called from native code
    @SuppressWarnings("unused")
	private static void callback(int frames)
    {
        if(paCallback != null)
            paCallback.paCallback(input, output, frames);
    }
    
    protected static PaBuffer.Type pa2BufferType(PaSampleFormat format)
    {
        switch(format)
        {
        case paFloat32:
            return Type.FLOAT;
        case paInt32:
            return Type.INT;
        case paInt16:
            return Type.SHORT;
        case paInt8:
        case paUInt8:
        default:
            return Type.BYTE;
        }
    }
    
    private static native long dataAlloc();
    private static native void dataFree(long ptr);
    private static native int paInitialize();
    private static native int paTerminate();
    private static native String paGetErrorText(int code);
    private static native int paIsFormatSupported(PaStreamParameters inputParameters, PaStreamParameters outputParameters, double sampleRate);
    private static native int paCloseStream(long ptr);
    private static native int paStartStream(long ptr);
    private static native int paStopStream(long ptr);
    private static native int paAbortStream(long ptr);
    private static native int paIsStreamStopped(long ptr);
    private static native int paIsStreamActive(long ptr);
    private static native PaStreamInfo paGetStreamInfo(long ptr);
    private static native double paGetStreamCpuLoad(long ptr);
    private static native int paOpenStream(long ptr, PaStreamParameters inputParameters, PaStreamParameters outputParameters, double sampleRate, int framesPerBuffer, int streamFlags);
    private static native int paOpenDefaultStream(long ptr, int numInputChannels, int numOutputChannels, int sampleFormat, double sampleRate, int framesPerBuffer);
    private static native int paGetSampleSize(int format);
    private static native double paGetStreamTime(long ptr);
}
