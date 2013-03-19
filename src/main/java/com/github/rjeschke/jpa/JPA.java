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

import java.nio.ByteBuffer;

import com.github.rjeschke.jpa.PaBuffer.Type;
import com.github.rjeschke.libload.LibLoad;

public class JPA
{
    private JPA()
    { /* singleton */
    }

    private static PaBuffer   output                                  = null;
    private static PaBuffer   input                                   = null;
    private static PaCallback paCallback                              = null;
    private static long       jpaPtr                                  = 0;

    static
    {
        // On windows, load portaudio.dll
        LibLoad.loadNative("win", "com.github.rjeschke.jpa.native", "portaudio.dll");
        // Load JNI library
        LibLoad.load("com.github.rjeschke.jpa.native", "jpa");
    }

    public final static int   paNoDevice                              = -1;
    public final static int   paUseHostApiSpecificDeviceSpecification = -2;

    public final static int   paNoFlag                                = 0;
    public final static int   paClipOff                               = 0x00000001;
    public final static int   paDitherOff                             = 0x00000002;
    public final static int   paNeverDropInput                        = 0x00000004;

    /**
     * Retrieve the release number of the currently running PortAudio build.
     * 
     * @return The release number.
     */
    public static native int getVersion();

    /**
     * Retrieve a textual description of the current PortAudio build.
     * 
     * @return Textual description of the current build.
     */
    public static native String getVersionText();

    /**
     * Retrieve the number of available host APIs. Even if a host API is
     * available it may have no devices available.
     * 
     * @return A non-negative value indicating the number of available host APIs
     *         or, a PaErrorCode (which are always negative) if PortAudio is not
     *         initialized or an error is encountered.
     */
    public static native int getHostApiCount();

    /**
     * Retrieve the index of the default host API.
     * <p>
     * The default host API will be the lowest common denominator host API on
     * the current platform and is unlikely to provide the best performance.
     * </p>
     * 
     * @return A non-negative value ranging from 0 to (Pa_GetHostApiCount()-1)
     *         indicating the default host API index or, a PaErrorCode (which
     *         are always negative) if PortAudio is not initialized or an error
     *         is encountered.
     */
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

    /**
     * Library initialization function - call this before using PortAudio.
     * <p>
     * This function initializes internal data structures and prepares
     * underlying host APIs for use. With the exception of Pa_GetVersion(),
     * Pa_GetVersionText(), and Pa_GetErrorText(), this function MUST be called
     * before using any other PortAudio API functions.
     * </p>
     * <p>
     * If Pa_Initialize() is called multiple times, each successful call must be
     * matched with a corresponding call to Pa_Terminate(). Pairs of calls to
     * Pa_Initialize()/Pa_Terminate() may overlap, and are not required to be
     * fully nested.
     * </p>
     * <p>
     * Note that if Pa_Initialize() returns an error code, Pa_Terminate() should
     * NOT be called.
     * </p>
     * 
     * @return paNoError if successful, otherwise an error code indicating the
     *         cause of failure.
     * @see com.github.rjeschke.jpa.JPA#terminate()
     */
    public static PaError initialize()
    {
        if (jpaPtr == 0) jpaPtr = dataAlloc();

        return PaError.fromValue(paInitialize());
    }

    /**
     * Library termination function - call this when finished using PortAudio.
     * <p>
     * This function deallocates all resources allocated by PortAudio since it
     * was initialized by a call to Pa_Initialize(). In cases where
     * Pa_Initialise() has been called multiple times, each call must be matched
     * with a corresponding call to Pa_Terminate(). The final matching call to
     * Pa_Terminate() will automatically close any PortAudio streams that are
     * still open.
     * </p>
     * <p>
     * Pa_Terminate() MUST be called before exiting a program which uses
     * PortAudio. Failure to do so may result in serious resource leaks, such as
     * audio devices not being available until the next reboot.
     * </p>
     * 
     * @return paNoError if successful, otherwise an error code indicating the
     *         cause of failure.
     * @see com.github.rjeschke.jpa.JPA#initialize()
     */
    public static PaError terminate()
    {
        dataFree(jpaPtr);
        jpaPtr = 0;
        return PaError.fromValue(paTerminate());
    }

    /**
     * Sets the user call-back function.
     * 
     * @param callback
     *            User call-back object.
     */
    public static void setCallback(PaCallback callback)
    {
        paCallback = callback;
    }

    public static PaError isFormatSupported(PaStreamParameters inputParameters, PaStreamParameters outputParameters,
            double sampleRate)
    {
        return PaError.fromValue(paIsFormatSupported(inputParameters, outputParameters, sampleRate));
    }

    /**
     * Translate the supplied PortAudio error code into a human readable
     * message.
     * 
     * @param err
     *            PortAudio error code.
     * @return Error code as a human readable message.
     */
    public static String getErrorText(PaError err)
    {
        return paGetErrorText(err.getValue());
    }

    public static PaStreamInfo getStreamInfo()
    {
        if (jpaPtr == 0L) throw new NullPointerException("Missing call to JPA.initialize()");

        return paGetStreamInfo(jpaPtr);
    }

    public static PaError openStream(PaStreamParameters inputParameters, PaStreamParameters outputParameters, double sampleRate,
            int framesPerBuffer, int streamFlags)
    {
        if (jpaPtr == 0L) throw new NullPointerException("Missing call to JPA.initialize()");

        final PaError err = PaError.fromValue(paOpenStream(jpaPtr, inputParameters, outputParameters, sampleRate,
                framesPerBuffer, streamFlags));

        if (err == PaError.paNoError)
        {
            input = inputParameters != null ? new PaBuffer(pa2BufferType(inputParameters.getSampleFormat()),
                    inputParameters.getChannelCount()) : null;
            output = outputParameters != null ? new PaBuffer(pa2BufferType(outputParameters.getSampleFormat()),
                    outputParameters.getChannelCount()) : null;
        }

        return err;
    }

    public static PaError openDefaultStream(int numInputChannels, int numOutputChannels, PaSampleFormat sampleFormat,
            double sampleRate, int framesPerBuffer)
    {
        if (jpaPtr == 0L) throw new NullPointerException("Missing call to JPA.initialize()");

        final PaError err = PaError.fromValue(paOpenDefaultStream(jpaPtr, numInputChannels, numOutputChannels,
                sampleFormat.getValue(), sampleRate, framesPerBuffer));

        if (err == PaError.paNoError)
        {
            input = numInputChannels > 0 ? new PaBuffer(pa2BufferType(sampleFormat), numInputChannels) : null;
            output = numOutputChannels > 0 ? new PaBuffer(pa2BufferType(sampleFormat), numOutputChannels) : null;
        }

        return err;
    }

    public static PaError closeStream()
    {
        if (jpaPtr == 0L) throw new NullPointerException("Missing call to JPA.initialize()");

        return PaError.fromValue(paCloseStream(jpaPtr));
    }

    public static PaError startStream()
    {
        if (jpaPtr == 0L) throw new NullPointerException("Missing call to JPA.initialize()");

        return PaError.fromValue(paStartStream(jpaPtr));
    }

    public static PaError stopStream()
    {
        if (jpaPtr == 0L) throw new NullPointerException("Missing call to JPA.initialize()");

        final PaError err = PaError.fromValue(paStopStream(jpaPtr));

        if (err == PaError.paNoError)
        {
            while (paIsStreamActive(jpaPtr) > 0 || paIsStreamStopped(jpaPtr) == 0)
            {
                try
                {
                    Thread.sleep(5);
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
        }

        return err;
    }

    public static PaError abortStream()
    {
        if (jpaPtr == 0L) throw new NullPointerException("Missing call to JPA.initialize()");

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
        if (jpaPtr == 0L) throw new NullPointerException("Missing call to JPA.initialize()");

        return paGetStreamTime(jpaPtr);
    }

    // Gets called from native code to prepare the buffers
    private static void resize(int frames)
    {
        if (output != null)
        {
            output.resize(frames);
            output.clear();
        }
        if (input != null)
        {
            input.resize(frames);
            input.clear();
        }
    }

    // Gets called from native code
    private static void callback(int frames)
    {
        if (paCallback != null) paCallback.paCallback(input, output, frames);
    }

    protected static PaBuffer.Type pa2BufferType(PaSampleFormat format)
    {
        switch (format)
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

    /**
     * Threading handling: If flag is set to <code>true</code>, each callback
     * detaches the thread from the JVM, on false there's no detaching
     * performed. Use at you own risk (default == true).
     * 
     * @param flag
     *            The flag.
     */
    public static void enableThreadDetach(boolean flag)
    {
        enableThreadDetach(JPA.jpaPtr, flag);
    }

    private static native long dataAlloc();

    private static native void dataFree(long ptr);

    private static native int paInitialize();

    private static native int paTerminate();

    private static native String paGetErrorText(int code);

    private static native int paIsFormatSupported(PaStreamParameters inputParameters, PaStreamParameters outputParameters,
            double sampleRate);

    private static native int paCloseStream(long ptr);

    private static native int paStartStream(long ptr);

    private static native int paStopStream(long ptr);

    private static native int paAbortStream(long ptr);

    private static native int paIsStreamStopped(long ptr);

    private static native int paIsStreamActive(long ptr);

    private static native PaStreamInfo paGetStreamInfo(long ptr);

    private static native double paGetStreamCpuLoad(long ptr);

    private static native int paOpenStream(long ptr, PaStreamParameters inputParameters, PaStreamParameters outputParameters,
            double sampleRate, int framesPerBuffer, int streamFlags);

    private static native int paOpenDefaultStream(long ptr, int numInputChannels, int numOutputChannels, int sampleFormat,
            double sampleRate, int framesPerBuffer);

    private static native int paGetSampleSize(int format);

    private static native double paGetStreamTime(long ptr);

    private static native void enableThreadDetach(long ptr, boolean flag);
}
