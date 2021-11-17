package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.DotAmount;

import java.io.IOException;

/**
 * Call to transfer [part of] balance to another address
 */
public class Transfer extends BalanceTransfer {

    /**
     * Balance to transfer
     */
    private DotAmount balance;

    public Transfer(DotAmount balance, Address destination) {
        this(0,balance,destination);
    }

    protected Transfer(int callIndex,DotAmount balance, Address destination) {
        super(callIndex,destination);
        this.balance=balance;
    }

    @Override
    public void internalWrite(ScaleCodecWriter wrt) throws IOException {
        wrt.write(ScaleCodecWriter.COMPACT_BIGINT, balance.getValue());
    }
}
