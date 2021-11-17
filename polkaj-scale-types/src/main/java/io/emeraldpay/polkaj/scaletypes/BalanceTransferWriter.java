package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.ScaleWriter;

import java.io.IOException;

public class BalanceTransferWriter implements ScaleWriter<BalanceTransfer> {
    @Override
    public void write(ScaleCodecWriter wrt, BalanceTransfer value) throws IOException {
        value.write(wrt);
    }
}
