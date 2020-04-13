package tim26.bezbednost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tim26.bezbednost.model.Certificate;
import tim26.bezbednost.model.enumeration.CertificateRole;
import tim26.bezbednost.model.enumeration.CertificateStatus;
import tim26.bezbednost.model.enumeration.CertificateType;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findAllByType(CertificateType type);
    List<Certificate> findAllByRole(CertificateRole role);
    Certificate findBySerialNumber(String serialNumber);




}
