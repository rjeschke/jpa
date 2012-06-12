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
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class PaBuffer
{
    private ByteBuffer  byteBuffer;
    private ShortBuffer shortBuffer;
    private IntBuffer   intBuffer;
    private FloatBuffer floatBuffer;
    private final Type  type;
    private final int   channels, frameSize, sampleSize;
    private int         frameCount;

    protected PaBuffer(Type type, int channels)
    {
        this.type = type;
        this.channels = channels;
        this.frameCount = 0;

        switch(type)
        {
        default:
        case BYTE:
            this.sampleSize = 1;
            break;
        case SHORT:
            this.sampleSize = 2;
            break;
        case FLOAT:
        case INT:
            this.sampleSize = 4;
            break;
        }

        this.frameSize = this.sampleSize * this.channels;
    }

    public void clear()
    {
        this.byteBuffer.clear();
        switch(this.type)
        {
        case BYTE:
            break;
        case SHORT:
            this.shortBuffer.clear();
            break;
        case INT:
            this.intBuffer.clear();
            break;
        case FLOAT:
            this.floatBuffer.clear();
            break;
        }
    }

    protected void resize(int frames)
    {
        if(frames > this.frameCount)
        {
            this.byteBuffer = ByteBuffer.allocateDirect(frames * this.frameSize);
            this.byteBuffer.order(ByteOrder.nativeOrder());

            switch(this.type)
            {
            case FLOAT:
                this.floatBuffer = this.byteBuffer.asFloatBuffer();
                break;
            case INT:
                this.intBuffer = this.byteBuffer.asIntBuffer();
                break;
            case SHORT:
                this.shortBuffer = this.byteBuffer.asShortBuffer();
                break;
            case BYTE:
                break;
            }

            this.frameCount = frames;
        }
    }

    public Type getType()
    {
        return this.type;
    }

    public ByteBuffer getByteBuffer()
    {
        return this.byteBuffer;
    }

    public FloatBuffer getFloatBuffer()
    {
        return this.floatBuffer;
    }

    public IntBuffer getIntBuffer()
    {
        return this.intBuffer;
    }

    public ShortBuffer getShortBuffer()
    {
        return this.shortBuffer;
    }

    public int getFrames()
    {
        return this.frameCount;
    }

    public int getChannels()
    {
        return this.channels;
    }

    public int getFrameSize()
    {
        return this.frameSize;
    }

    public int getSampleSize()
    {
        return this.sampleSize;
    }

    public long getAddress()
    {
        return JPA.getDirectByteBufferPointer(this.byteBuffer);
    }

    public enum Type
    {
        FLOAT,
        INT,
        SHORT,
        BYTE
    }
}
