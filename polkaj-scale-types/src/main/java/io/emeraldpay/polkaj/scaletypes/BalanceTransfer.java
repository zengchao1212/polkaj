package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.UnionValue;
import io.emeraldpay.polkaj.types.Address;

import java.io.IOException;

public abstract class BalanceTransfer extends ExtrinsicCall{

    protected UnionValue<MultiAddress> destination;

    public BalanceTransfer(int callIndex, Address destination){
        super(5,callIndex);
        this.destination=MultiAddress.AccountID.from(destination);
    }

    public UnionValue<MultiAddress> getDestination() {
        return destination;
    }

    public void write(ScaleCodecWriter wrt) throws IOException {
        wrt.writeByte(getModuleIndex());
        wrt.writeByte(getCallIndex());
        wrt.write(new MultiAddressWriter(), destination);
        internalWrite(wrt);
    }

    public abstract void internalWrite(ScaleCodecWriter wrt) throws IOException;
}
