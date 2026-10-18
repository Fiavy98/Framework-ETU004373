package models;

import java.util.Base64;

public class MappFile {
    private String name;
    private String extension;
    private byte[] bits;

    public MappFile() {
    }

    public MappFile(String name, String extension, byte[] bits) {
        this.name = name;
        this.extension = extension;
        this.bits = bits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getBits() {
        return bits;
    }

    public void setBits(byte[] bits) {
        this.bits = bits;
    }

    public String getDataUrl() {
        if (bits == null || bits.length == 0) {
            return "";
        }

        String mimeType = "image/jpeg";
        if ("png".equalsIgnoreCase(extension)) {
            mimeType = "image/png";
        } else if ("pdf".equalsIgnoreCase(extension)) {
            mimeType = "application/pdf";
        }

        String base64 = Base64.getEncoder().encodeToString(bits);
        return "data:" + mimeType + ";base64," + base64;
    }
}
