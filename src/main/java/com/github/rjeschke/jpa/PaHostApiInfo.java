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
