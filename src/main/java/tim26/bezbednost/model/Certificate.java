package tim26.bezbednost.model;

import tim26.bezbednost.model.enumeration.CertificateRole;
import tim26.bezbednost.model.enumeration.CertificateStatus;
import tim26.bezbednost.model.enumeration.CertificateType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Certificate  implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name="certificateRole", nullable = false)
    private CertificateRole role;

    @Enumerated(EnumType.STRING)
    @Column(name="certificateType", nullable = false)
    private CertificateType type;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="serialNumber", unique = true)
    private String serialNumber;

    @Column(name="commonName", unique = true)
    private String commonName;

    @Column(name = "validto")
    private LocalDate validTo;

    @Column(name = "validfrom")
    private LocalDate validFrom;

    @Column(name = "status")
    private CertificateStatus certificateStatus;

    @Column(name = "code")
    private String code;

    @Column(name = "children")
    private int children;

    public Certificate() {
    }

    public Certificate(String serialNumber, CertificateRole certificateRole,CertificateType type,String commonName) {
        this.serialNumber = serialNumber;
        this.role = certificateRole;
        this.type = type;
        this.commonName = commonName;
    }

    public Certificate(String serialNumber, CertificateRole certificateRole,CertificateType type,String commonName, LocalDate validFrom, LocalDate validTo, CertificateStatus certificateStatus,int children, String code) {
        this.serialNumber = serialNumber;
        this.role = certificateRole;
        this.type = type;
        this.commonName = commonName;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.certificateStatus = certificateStatus;
        this.children = children;
        this.code = code;
    }


    public CertificateRole getRole() {
        return role;
    }

    public void setRole(CertificateRole role) {
        this.role = role;
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

    public CertificateType getType() {
        return type;
    }

    public void setType(CertificateType type) {
        this.type = type;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public CertificateStatus getCertificateStatus() {
        return certificateStatus;
    }

    public void setCertificateStatus(CertificateStatus certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }


}
