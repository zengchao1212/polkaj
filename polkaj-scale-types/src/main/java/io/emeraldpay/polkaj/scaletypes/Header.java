package io.emeraldpay.polkaj.scaletypes;

public class Header {
    private String parentHash;
    private Long number;
    private String stateRoot;
    private String extrinsicsRoot;
    private String digest;

    public String getParentHash() {
        return parentHash;
    }

    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getStateRoot() {
        return stateRoot;
    }

    public void setStateRoot(String stateRoot) {
        this.stateRoot = stateRoot;
    }

    public String getExtrinsicsRoot() {
        return extrinsicsRoot;
    }

    public void setExtrinsicsRoot(String extrinsicsRoot) {
        this.extrinsicsRoot = extrinsicsRoot;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
