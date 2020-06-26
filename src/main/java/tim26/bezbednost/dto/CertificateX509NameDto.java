package tim26.bezbednost.dto;

import tim26.bezbednost.model.enumeration.CertificateRole;
import tim26.bezbednost.model.enumeration.CertificateType;

import java.time.LocalDate;

public class CertificateX509NameDto {

    private String commonName;
    private String state;
    private String city;
    private String email;
    private String organization;
    private String organizationUnit;
    private String serialNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private CertificateRole certificateRole;
    private String issuerSerialNumber;
    private CertificateType subjectType;
    private String code;
    private ExstensionsDto exstensionsDto;
    private byte[] password;

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

    public CertificateType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(CertificateType subjectType) {
        this.subjectType = subjectType;
    }

    public String getIssuerSerialNumber() {
        return issuerSerialNumber;
    }

    public void setIssuerSerialNumber(String issuerSerialNumber) {
        this.issuerSerialNumber = issuerSerialNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ExstensionsDto getExstensionsDto() {
        return exstensionsDto;
    }

    public void setExstensionsDto(ExstensionsDto exstensionsDto) {
        this.exstensionsDto = exstensionsDto;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }
}
