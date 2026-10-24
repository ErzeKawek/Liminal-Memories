package net.erzekawek.fondestmemories.misc;

public enum SkyVariants {
    DAY("siang"),
    NIGHT("evening");

    private final String textureName;

    SkyVariants(String textureName) {
        this.textureName = textureName;
    }

    public String textureName() {
        return textureName;
    }
}
