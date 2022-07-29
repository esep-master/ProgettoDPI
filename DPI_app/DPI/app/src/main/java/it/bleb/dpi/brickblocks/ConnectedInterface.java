/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2020. All rights reserved.
 * https://bleb.it
 *
 * Last modified 30/03/2020 18:21
 */

package it.bleb.dpi.brickblocks;

public interface ConnectedInterface {
    void BeforeConnection(boolean propagate);
    void AfterConnection(boolean propagate);
    void AfterDisconnection(boolean propagate);
}
