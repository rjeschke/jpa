/*
* Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
* See LICENSE.txt for licensing information.
*/
#include <jni.h>
#include <portaudio.h>
#include <stdlib.h>
#include <string.h>

#if defined(WIN32) || defined(WIN64)
#define int8_t __int8
#define uint8_t unsigned __int8
#define int16_t __int16
#define uint16_t unsigned __int16
#define int32_t __int32
#define uint32_t unsigned __int32
#define int64_t __int64
#define uint64_t unsigned __int64
#else
#include <stdint.h>
#endif

static jlong ptr2Long(void* ptr)
{
    return (jlong)((uint64_t)ptr);
}

static void* long2Ptr(jlong ptr)
{
    return (void*)((uint64_t)ptr);
}

typedef struct
{
    PaStream* stream;
    PaStreamParameters inputParameters;
    PaStreamParameters outputParameters;
    uint32_t iFrameSize, oFrameSize;
    JavaVM* jvm;
    jclass clazz;
    jmethodID callback, resize;
    jfieldID input, output, buffer;
} JPA_DATA;

static int jpaStreamCallback(
        const void *input, void *output, unsigned long frameCount,
        const PaStreamCallbackTimeInfo* timeInfo, PaStreamCallbackFlags statusFlags, void *userData)
{
    JPA_DATA *j = (JPA_DATA*)userData;
    JNIEnv* env;
    jobject buffer;
    void* ptr;

    if(!(*(j->jvm))->AttachCurrentThread(j->jvm, (void**)&env, NULL))
    {
        // Call the resize method to prepare the ByteBuffer
        (*env)->CallStaticVoidMethod(env, j->clazz, j->resize, (jint)frameCount);

        // Do we have an input buffer?
        buffer = (*env)->GetStaticObjectField(env, j->clazz, j->input);
        if(buffer)
        {
            // Yes, get address and copy input -> ByteBuffer
            memcpy(
                    (*env)->GetDirectBufferAddress(env, (*env)->GetObjectField(env, buffer, j->buffer)),
                    input,
                    (size_t)frameCount * (size_t)j->iFrameSize);
        }

        buffer = (*env)->GetStaticObjectField(env, j->clazz, j->output);
        ptr = buffer ? (*env)->GetDirectBufferAddress(env, (*env)->GetObjectField(env, buffer, j->buffer)) : 0;

        if(ptr)
            memset(ptr, 0, (size_t)frameCount * (size_t)j->oFrameSize);

        // Call the callback method
        (*env)->CallStaticVoidMethod(env, j->clazz, j->callback, (jint)frameCount);

        if(ptr)
            memcpy(output, ptr, (size_t)frameCount * (size_t)j->oFrameSize);

        (*(j->jvm))->DetachCurrentThread(j->jvm);
    }
    // Done
    return paContinue;
}


static jstring makeString(JNIEnv *env, const char* str)
{
    return str ? (*env)->NewStringUTF(env, str) : 0;
}

static PaStreamParameters* toStreamParameters(JNIEnv* env, jobject obj, PaStreamParameters* p)
{
    jclass jcl          = (*env)->FindClass(env, "jpa/PaStreamParameters");
    memset(p, 0, sizeof(PaStreamParameters));

    p->device           = (PaDeviceIndex)(*env)->GetIntField(env, obj,
            (*env)->GetFieldID(env, jcl, "device", "I"));
    p->channelCount     = (int)(*env)->GetIntField(env, obj,
            (*env)->GetFieldID(env, jcl, "channelCount", "I"));
    p->sampleFormat     = (PaSampleFormat)(*env)->GetIntField(env, obj,
            (*env)->GetFieldID(env, jcl, "sampleFormat", "I"));
    p->suggestedLatency = (PaTime)(*env)->GetDoubleField(env, obj,
            (*env)->GetFieldID(env, jcl, "suggestedLatency", "D"));

    return p;
}

static jobject toStreamInfo(JNIEnv* env, const PaStreamInfo* inf)
{
    jclass jcl   = (*env)->FindClass(env, "jpa/PaStreamInfo");
    jmethodID ct = (*env)->GetMethodID(env, jcl, "<init>", "()V");
    jobject ret  = (*env)->NewObject(env, jcl, ct);

    (*env)->SetDoubleField(env, ret,
            (*env)->GetFieldID(env, jcl, "inputLatency", "D"),
            (jdouble)inf->inputLatency);

    (*env)->SetDoubleField(env, ret,
            (*env)->GetFieldID(env, jcl, "outputLatency", "D"),
            (jdouble)inf->outputLatency);

    (*env)->SetDoubleField(env, ret,
            (*env)->GetFieldID(env, jcl, "sampleRate", "D"),
            (jdouble)inf->sampleRate);

    return ret;
}

static jobject toHostErrorInfo(JNIEnv* env, const PaHostErrorInfo* inf)
{
    jclass jcl   = (*env)->FindClass(env, "jpa/PaHostErrorInfo");
    jmethodID ct = (*env)->GetMethodID(env, jcl, "<init>", "()V");
    jobject ret  = (*env)->NewObject(env, jcl, ct);

    (*env)->SetIntField(env, ret,
            (*env)->GetFieldID(env, jcl, "hostApiType", "I"), (jint)inf->hostApiType);
    (*env)->SetLongField(env, ret,
            (*env)->GetFieldID(env, jcl, "hostApiType", "I"), (jlong)inf->errorCode);
    (*env)->SetObjectField(env, ret,
            (*env)->GetFieldID(env, jcl, "errorText", "Ljava/lang/String;"), makeString(env, inf->errorText));

    return ret;
}

static jobject toHostApiInfo(JNIEnv* env, const PaHostApiInfo* inf)
{
    jclass jcl   = (*env)->FindClass(env, "jpa/PaHostApiInfo");
    jmethodID ct = (*env)->GetMethodID(env, jcl, "<init>", "()V");
    jobject ret  = (*env)->NewObject(env, jcl, ct);

    (*env)->SetIntField(env, ret,
            (*env)->GetFieldID(env, jcl, "type", "I"), (jint)inf->type);
    (*env)->SetObjectField(env, ret,
            (*env)->GetFieldID(env, jcl, "name", "Ljava/lang/String;"), makeString(env, inf->name));
    (*env)->SetIntField(env, ret,
            (*env)->GetFieldID(env, jcl, "deviceCount", "I"), (jint)inf->deviceCount);
    (*env)->SetIntField(env, ret,
            (*env)->GetFieldID(env, jcl, "defaultInputDevice", "I"), (jint)inf->defaultInputDevice);
    (*env)->SetIntField(env, ret,
            (*env)->GetFieldID(env, jcl, "defaultOutputDevice", "I"), (jint)inf->defaultOutputDevice);

    return ret;
}

static jobject toPaDeviceInfo(JNIEnv* env, const PaDeviceInfo* inf)
{
    jclass jcl   = (*env)->FindClass(env, "jpa/PaDeviceInfo");
    jmethodID ct = (*env)->GetMethodID(env, jcl, "<init>", "()V");
    jobject ret  = (*env)->NewObject(env, jcl, ct);

    (*env)->SetObjectField(env, ret,
            (*env)->GetFieldID(env, jcl, "name", "Ljava/lang/String;"),
            makeString(env, inf->name));
    (*env)->SetIntField(env, ret,
            (*env)->GetFieldID(env, jcl, "hostApi", "I"),
            (jint)inf->hostApi);
    (*env)->SetIntField(env, ret,
            (*env)->GetFieldID(env, jcl, "maxInputChannels", "I"),
            (jint)inf->maxInputChannels);
    (*env)->SetIntField(env, ret,
            (*env)->GetFieldID(env, jcl, "maxOutputChannels", "I"),
            (jint)inf->maxOutputChannels);
    (*env)->SetDoubleField(env, ret,
            (*env)->GetFieldID(env, jcl, "defaultLowInputLatency", "D"),
            (jdouble)inf->defaultLowInputLatency);
    (*env)->SetDoubleField(env, ret,
            (*env)->GetFieldID(env, jcl, "defaultLowOutputLatency", "D"),
            (jdouble)inf->defaultLowOutputLatency);
    (*env)->SetDoubleField(env, ret,
            (*env)->GetFieldID(env, jcl, "defaultHighInputLatency", "D"),
            (jdouble)inf->defaultHighInputLatency);
    (*env)->SetDoubleField(env, ret,
            (*env)->GetFieldID(env, jcl, "defaultHighOutputLatency", "D"),
            (jdouble)inf->defaultHighOutputLatency);
    (*env)->SetDoubleField(env, ret,
            (*env)->GetFieldID(env, jcl, "defaultSampleRate", "D"),
            (jdouble)inf->defaultSampleRate);

    return ret;
}

JNIEXPORT jint JNICALL Java_jpa_JPA_getVersion(JNIEnv *env, jclass clazz)
{
    return (jint)Pa_GetVersion();
}

JNIEXPORT jstring JNICALL Java_jpa_JPA_getVersionText(JNIEnv *env, jclass clazz)
{
    return makeString(env, (char*)Pa_GetVersionText());
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paInitialize(JNIEnv *env, jclass clazz)
{
    return (jint)Pa_Initialize();
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paTerminate(JNIEnv *env, jclass clazz)
{
    return (jint)Pa_Terminate();
}

JNIEXPORT jstring JNICALL Java_jpa_JPA_paGetErrorText(JNIEnv *env, jclass clazz, jint error)
{
    return makeString(env, (char*)Pa_GetErrorText((PaError)error));
}

JNIEXPORT jint JNICALL Java_jpa_JPA_getHostApiCount(JNIEnv *env, jclass clazz)
{
    return (jint)Pa_GetHostApiCount();
}

JNIEXPORT jint JNICALL Java_jpa_JPA_getDefaultHostApi(JNIEnv *env, jclass clazz)
{
    return (jint)Pa_GetDefaultHostApi();
}

JNIEXPORT jint JNICALL Java_jpa_JPA_getDeviceCount(JNIEnv *env, jclass clazz)
{
    return (jint)Pa_GetDeviceCount();
}

JNIEXPORT jint JNICALL Java_jpa_JPA_getDefaultInputDevice(JNIEnv *env, jclass clazz)
{
    return (jint)Pa_GetDefaultInputDevice();
}

JNIEXPORT jint JNICALL Java_jpa_JPA_getDefaultOutputDevice(JNIEnv *env, jclass clazz)
{
    return (jint)Pa_GetDefaultOutputDevice();
}

JNIEXPORT jobject JNICALL Java_jpa_JPA_getDeviceInfo(JNIEnv *env, jclass clazz, jint index)
{
    const PaDeviceInfo *inf = Pa_GetDeviceInfo((PaDeviceIndex)index);
    return inf ? toPaDeviceInfo(env, inf) : 0;
}

JNIEXPORT jobject JNICALL Java_jpa_JPA_getHostApiInfo(JNIEnv *env, jclass clazz, jint index)
{
    const PaHostApiInfo* inf = Pa_GetHostApiInfo((PaHostApiIndex)index);
    return inf ? toHostApiInfo(env, inf) : 0;
}

JNIEXPORT jint JNICALL Java_jpa_JPA_hostApiTypeIdToHostApiIndex(JNIEnv *env, jclass clazz, jint type)
{
    return (jint)Pa_HostApiTypeIdToHostApiIndex((PaHostApiTypeId)type);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_hostApiDeviceIndexToDeviceIndex(JNIEnv *env, jclass clazz, jint hostApi, jint hostApiDeviceIndex)
{
    return (jint)Pa_HostApiDeviceIndexToDeviceIndex((PaHostApiIndex)hostApi, (int)hostApiDeviceIndex);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paIsFormatSupported(JNIEnv *env, jclass clazz, jobject jInput, jobject jOutput, jdouble sampleRate)
{
    PaStreamParameters pi, po;
    PaStreamParameters *i, *o;

    i = jInput  ? toStreamParameters(env, jInput,  &pi) : 0;
    o = jOutput ? toStreamParameters(env, jOutput, &po) : 0;

    return Pa_IsFormatSupported(i, o, (double)sampleRate);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paCloseStream(JNIEnv *env, jclass clazz, jlong jPtr)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);
    return Pa_CloseStream(j->stream);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paStartStream(JNIEnv *env, jclass clazz, jlong jPtr)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);
    return Pa_StartStream(j->stream);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paStopStream(JNIEnv *env, jclass clazz, jlong jPtr)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);
    return Pa_StopStream(j->stream);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paAbortStream(JNIEnv *env, jclass clazz, jlong jPtr)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);
    return Pa_AbortStream(j->stream);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paIsStreamStopped(JNIEnv *env, jclass clazz, jlong jPtr)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);
    return Pa_IsStreamStopped(j->stream);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paIsStreamActive(JNIEnv *env, jclass clazz, jlong jPtr)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);
    return Pa_IsStreamActive(j->stream);
}

JNIEXPORT jlong JNICALL Java_jpa_JPA_dataAlloc(JNIEnv *env, jclass clazz)
{
    JPA_DATA* data = (JPA_DATA*)malloc(sizeof(JPA_DATA));
    memset(data, 0, sizeof(JPA_DATA));
    (*env)->GetJavaVM(env, &(data->jvm));
    data->clazz = clazz;
    data->callback = (*env)->GetStaticMethodID(env, clazz, "callback", "(I)V");
    data->resize = (*env)->GetStaticMethodID(env, clazz, "resize", "(I)V");
    data->input = (*env)->GetStaticFieldID(env, clazz, "input", "Ljpa/PaBuffer;");
    data->output = (*env)->GetStaticFieldID(env, clazz, "output", "Ljpa/PaBuffer;");
    data->buffer = (*env)->GetFieldID(env, (*env)->FindClass(env, "Ljpa/PaBuffer;"), "byteBuffer", "Ljava/nio/ByteBuffer;");
    return ptr2Long(data);
}

JNIEXPORT void JNICALL Java_jpa_JPA_dataFree(JNIEnv *env, jclass clazz, jlong jPtr)
{
    if(jPtr)
        free(long2Ptr(jPtr));
}

JNIEXPORT jobject JNICALL Java_jpa_JPA_paGetStreamInfo(JNIEnv *env, jclass clazz, jlong jPtr)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);
    const PaStreamInfo* inf = Pa_GetStreamInfo(j->stream);
    return inf ? toStreamInfo(env, inf) : 0;
}

JNIEXPORT jobject JNICALL Java_jpa_JPA_getLastHostErrorInfo(JNIEnv *env, jclass clazz)
{
    const PaHostErrorInfo *inf = Pa_GetLastHostErrorInfo();
    return inf ? toHostErrorInfo(env, inf) : 0;
}

JNIEXPORT jdouble JNICALL Java_jpa_JPA_paGetStreamCpuLoad(JNIEnv *env, jclass clazz, jlong jPtr)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);
    return (jdouble)Pa_GetStreamCpuLoad(j->stream);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paOpenStream(JNIEnv *env, jclass clazz, jlong jPtr, jobject jInput, jobject jOutput, jdouble sampleRate, jint frames, jint flags)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);
    PaStreamParameters *i, *o;

    i = jInput  ? toStreamParameters(env, jInput,  &(j->inputParameters)) : 0;
    o = jOutput ? toStreamParameters(env, jOutput, &(j->outputParameters)) : 0;

    j->iFrameSize = i ? (uint32_t)Pa_GetSampleSize(i->sampleFormat) * (uint32_t)i->channelCount : 0;
    j->oFrameSize = o ? (uint32_t)Pa_GetSampleSize(o->sampleFormat) * (uint32_t)o->channelCount : 0;

    return Pa_OpenStream(&(j->stream), i, o, (double)sampleRate, frames, (PaStreamFlags)flags, jpaStreamCallback, j);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paOpenDefaultStream(JNIEnv *env, jclass clazz, jlong jPtr, jint numInputChannels, jint numOutputChannels, jint sampleFormat, jdouble sampleRate, jint frames)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);

    j->iFrameSize = (uint32_t)Pa_GetSampleSize((PaSampleFormat)sampleFormat) * (uint32_t)numInputChannels;
    j->oFrameSize = (uint32_t)Pa_GetSampleSize((PaSampleFormat)sampleFormat) * (uint32_t)numOutputChannels;

    return Pa_OpenDefaultStream(&(j->stream), (int)numInputChannels, (int)numOutputChannels, (PaSampleFormat)sampleFormat, sampleRate, frames, jpaStreamCallback, j);
}

JNIEXPORT jint JNICALL Java_jpa_JPA_paGetSampleSize(JNIEnv *env, jclass clazz, jint format)
{
    return (jint)Pa_GetSampleSize((PaSampleFormat)format);
}

JNIEXPORT jdouble JNICALL Java_jpa_JPA_paGetStreamTime(JNIEnv *env, jclass clazz, jlong jPtr)
{
    JPA_DATA *j = (JPA_DATA*)long2Ptr(jPtr);
    return (jdouble)Pa_GetStreamTime(j->stream);
}

JNIEXPORT void JNICALL Java_jpa_JPA_sleep(JNIEnv *env, jclass clazz, jlong msec)
{
    Pa_Sleep((long int)msec);
}

JNIEXPORT jlong JNICALL Java_jpa_JPA_getDirectByteBufferPointer(JNIEnv *env, jclass clazz, jobject buffer)
{
    return ptr2Long((*env)->GetDirectBufferAddress(env, buffer));
}
