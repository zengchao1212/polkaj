package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.DotAmount;

/**
 * Call to transfer [part of] balance to another address
 */
public class TransferKeepAlive extends Transfer {

    public TransferKeepAlive(DotAmount balance, Address destination) {
        super(3,balance,destination);
    }

}
