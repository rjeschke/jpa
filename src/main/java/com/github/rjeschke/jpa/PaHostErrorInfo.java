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

public class PaHostErrorInfo
{
    private int    hostApiType;
    private long   errorCode;
    private String errorText;

    private PaHostErrorInfo()
    {
        // Object creation from JNI
    }

    public PaHostApiType getHostApiType()
    {
        return PaHostApiType.fromValue(this.hostApiType);
    }

    public long getErrorCode()
    {
        return this.errorCode;
    }

    public String getErrorText()
    {
        return this.errorText;
    }
}
