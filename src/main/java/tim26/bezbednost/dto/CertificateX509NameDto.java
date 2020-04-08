package tim26.bezbednost.dto;

import tim26.bezbednost.model.enumeration.CertificateRole;
import tim26.bezbednost.model.enumeration.CertificateType;

public class CertificateX509NameDto {

    private String commonName;
    private String state;
    private String city;
    private String email;
    private String organization;
    private String organizationUnit;
    private String serialNumber;
    private CertificateRole certificateRole;
    private String IssuerSerialNumber;
    private CertificateType subjectType;

    public CertificateX509NameDto() {

    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
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

    public String getIssuerId() {
        return IssuerSerialNumber;
    }

    public void setIssuerId(String issuerSerialNumber) {
        this.IssuerSerialNumber = issuerSerialNumber;
    }

    public CertificateType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(CertificateType subjectType) {
        this.subjectType = subjectType;
    }
}
