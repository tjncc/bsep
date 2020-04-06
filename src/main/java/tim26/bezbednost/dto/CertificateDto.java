package tim26.bezbednost.dto;

import tim26.bezbednost.model.enumeration.CertificateRole;

public class CertificateDto {

    private Long id;
    private String serialNumber;
    private CertificateRole certificateRole;

    public CertificateDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public CertificateRole getCertificateRole() {
        return certificateRole;
    }

    public void setCertificateRole(CertificateRole certificateRole) {
        this.certificateRole = certificateRole;
    }
}
