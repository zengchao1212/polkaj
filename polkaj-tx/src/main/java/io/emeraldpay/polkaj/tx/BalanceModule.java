package io.emeraldpay.polkaj.tx;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scaletypes.*;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.DotAmount;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Common requests and extrinsics specific to accounts.
 */
public class BalanceModule {

    /**
     * Transfer value from one account to another
     * @return builder for transfer
     */
    public static TransferBuilder transfer() {
        return new TransferBuilder();
    }

    /**
     * Transfer value from one account to another, but making sure that the balance of both accounts is above the existential
     * deposit
     *
     * @return builder for transfer-keep-alive
     */
    public static TransferKeepAliveBuilder transferKeepAlive() {
        return new TransferKeepAliveBuilder();
    }



    public static class Transfer implements ExtrinsicRequest {
        private static final ExtrinsicWriter<BalanceTransfer> CODEC = new ExtrinsicWriter<>(
                new BalanceTransferWriter()
        );

        private final Extrinsic<BalanceTransfer> extrinsic;

        public Transfer(Extrinsic<BalanceTransfer> extrinsic) {
            this.extrinsic = extrinsic;
        }

        public Extrinsic<BalanceTransfer> getExtrinsic() {
            return extrinsic;
        }

        @Override
        public ByteData encodeRequest() throws IOException {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ScaleCodecWriter writer = new ScaleCodecWriter(buf);
            writer.write(CODEC, extrinsic);
            return new ByteData(buf.toByteArray());
        }

        @Override
        public String toString() {
            return "Transfer{" +
                    "extrinsic=" + extrinsic +
                    '}';
        }
    }

    public static class TransferAll implements ExtrinsicRequest {
        private static final ExtrinsicWriter<BalanceTransferAll> CODEC = new ExtrinsicWriter<>(
                new BalanceTransferAllWriter()
        );

        private final Extrinsic<BalanceTransferAll> extrinsic;

        public TransferAll(Extrinsic<BalanceTransferAll> extrinsic) {
            this.extrinsic = extrinsic;
        }

        public Extrinsic<BalanceTransferAll> getExtrinsic() {
            return extrinsic;
        }

        @Override
        public ByteData encodeRequest() throws IOException {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ScaleCodecWriter writer = new ScaleCodecWriter(buf);
            writer.write(CODEC, extrinsic);
            return new ByteData(buf.toByteArray());
        }

        @Override
        public String toString() {
            return "Transfer{" +
                    "extrinsic=" + extrinsic +
                    '}';
        }
    }

    public static class TransferBuilder {
        private Address from;
        private Long nonce;
        private DotAmount tip;

        protected final BalanceTransfer call;

        public TransferBuilder(){
            this(0);
        }

        protected TransferBuilder(int callIndex){
            call = new BalanceTransfer(callIndex);
        }

        /**
         *
         * @param from sender address
         * @return builder
         */
        public TransferBuilder from(Address from) {
            this.from = from;
            if (this.tip == null) {
                // set default tip as well now that the network is known
                this.tip = new DotAmount(BigInteger.ZERO, from.getNetwork());
            }
            return this;
        }

        /**
         *
         * @param to recipient address
         * @return builder
         */
        public TransferBuilder to(Address to) {
            this.call.setDestination(to);
            return this;
        }

        /**
         *
         * @param amount amount to transfer
         * @return builder
         */
        public TransferBuilder amount(DotAmount amount) {
            this.call.setBalance(amount);
            return this;
        }

        /**
         * (optional) tip to include for the miner
         *
         * @param tip tip to use
         * @return builder
         */
        public TransferBuilder tip(DotAmount tip) {
            this.tip = tip;
            return this;
        }

        /**
         * (optional) Set once, if setting a predefined signature.
         *
         * @param nonce once to use
         * @return builder
         */
        public TransferBuilder nonce(Long nonce) {
            this.nonce = nonce;
            return this;
        }

        /**
         *
         * @return signed Transfer
         */
        public Transfer build() {
            Extrinsic.TransactionInfo tx = new Extrinsic.TransactionInfo();
            tx.setNonce(this.nonce);
            tx.setSender(this.from);
            tx.setTip(this.tip);

            Extrinsic<BalanceTransfer> extrinsic = new Extrinsic<>();
            extrinsic.setCall(this.call);
            extrinsic.setTx(tx);
            return new Transfer(extrinsic);
        }

    }

    public static final class TransferKeepAliveBuilder extends TransferBuilder {

        public TransferKeepAliveBuilder() {
            super(3);
        }
    }


    public static class TransferAllBuilder {
        private Address from;
        private Long nonce;
        private DotAmount tip;

        private final BalanceTransferAll call;

        public TransferAllBuilder(){
            call=new BalanceTransferAll();
            tip = new DotAmount(BigInteger.ZERO, from.getNetwork());
        }

        /**
         *
         * @param from sender address
         * @return builder
         */
        public TransferAllBuilder from(Address from) {
            this.from = from;
            if (this.tip == null) {
                // set default tip as well now that the network is known
                this.tip = new DotAmount(BigInteger.ZERO, from.getNetwork());
            }
            return this;
        }

        /**
         *
         * @param to recipient address
         * @return builder
         */
        public TransferAllBuilder to(Address to) {
            this.call.setDestination(to);
            return this;
        }

        public TransferAllBuilder keepAlive(Boolean keepAlive) {
            this.call.setKeepAlive(keepAlive);
            return this;
        }

        /**
         * (optional) tip to include for the miner
         *
         * @param tip tip to use
         * @return builder
         */
        public TransferAllBuilder tip(DotAmount tip) {
            this.tip = tip;
            return this;
        }

        /**
         * (optional) Set once, if setting a predefined signature.
         *
         * @param nonce once to use
         * @return builder
         */
        public TransferAllBuilder nonce(Long nonce) {
            this.nonce = nonce;
            return this;
        }

        /**
         *
         * @return signed Transfer
         */
        public TransferAll build() {
            Extrinsic.TransactionInfo tx = new Extrinsic.TransactionInfo();
            tx.setNonce(this.nonce);
            tx.setSender(this.from);
            tx.setTip(this.tip);

            Extrinsic<BalanceTransferAll> extrinsic = new Extrinsic<>();
            extrinsic.setCall(this.call);
            extrinsic.setTx(tx);
            return new TransferAll(extrinsic);
        }
    }

}
