/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 * See LICENSE.txt for licensing information.
 */
package com.github.rjeschke.jpa;

public interface PaCallback
{
    public void paCallback(PaBuffer input, PaBuffer output, int nframes);
}
