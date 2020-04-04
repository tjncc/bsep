package tim26.bezbednost.model;

import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Certificate  implements Serializable {

    @Enumerated(EnumType.STRING)
    @Column(name="certificateRole", nullable = false)
    private CertificateRole role;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="serialNumber", unique = true)
    private String serialNumber;

    public Certificate() {
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
}
