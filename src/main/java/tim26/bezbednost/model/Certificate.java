package tim26.bezbednost.model;

import tim26.bezbednost.model.enumeration.CertificateRole;
import tim26.bezbednost.model.enumeration.CertificateType;

import javax.persistence.*;
import java.io.Serializable;

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

    public Certificate() {
    }

    public Certificate(String serialNumber, CertificateRole certificateRole,CertificateType type,String commonName) {
        this.serialNumber = serialNumber;
        this.role = certificateRole;
        this.type = type;
        this.commonName = commonName;
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
}
