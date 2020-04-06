package tim26.bezbednost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tim26.bezbednost.model.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

}
