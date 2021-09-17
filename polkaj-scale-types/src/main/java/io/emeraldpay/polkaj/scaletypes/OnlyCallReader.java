package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;

public class OnlyCallReader implements ScaleReader<ExtrinsicCall> {

    @Override
    public ExtrinsicCall read(ScaleCodecReader rdr) {
        ExtrinsicCall result = new ExtrinsicCall();
        result.setModuleIndex(rdr.readUByte());
        result.setCallIndex(rdr.readUByte());
        return result;
    }
}