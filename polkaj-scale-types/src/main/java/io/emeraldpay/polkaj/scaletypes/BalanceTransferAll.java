package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.UnionValue;
import io.emeraldpay.polkaj.types.Address;

public class BalanceTransferAll extends ExtrinsicCall{

    private UnionValue<MultiAddress> destination;
    private Boolean keepAlive;

    public BalanceTransferAll(){
        super(5,4);
        this.keepAlive=false;
    }

    public UnionValue<MultiAddress> getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = MultiAddress.AccountID.from(destination);
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }
}
