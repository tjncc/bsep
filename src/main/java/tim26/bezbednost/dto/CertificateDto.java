package tim26.bezbednost.dto;

import tim26.bezbednost.model.enumeration.CertificateRole;
import tim26.bezbednost.model.enumeration.CertificateStatus;

import java.time.LocalDate;

public class CertificateDto {

    private Long id;
    private String serialNumber;
    private CertificateRole certificateRole;
    private CertificateStatus certificateStatus;
    private LocalDate validFrom;
    private LocalDate validTo;

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

    public CertificateStatus getCertificateStatus() {
        return certificateStatus;
    }

    public void setCertificateStatus(CertificateStatus certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }
}
