package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.types.Address;

import java.io.IOException;

public class TransferAll extends BalanceTransfer{

    private Boolean keepAlive;

    public TransferAll(Address destination,Boolean keepAlive){
        super(4,destination);
        this.keepAlive=keepAlive;
    }

    @Override
    public void internalWrite(ScaleCodecWriter wrt) throws IOException {
        wrt.write(ScaleCodecWriter.BOOL, keepAlive);
    }

}
