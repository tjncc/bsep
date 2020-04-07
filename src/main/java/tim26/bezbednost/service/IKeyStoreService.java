package tim26.bezbednost.service;

import org.springframework.beans.factory.annotation.Autowired;
import tim26.bezbednost.model.enumeration.CertificateRole;

import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

public interface IKeyStoreService {

    public void saveCertificate(X509Certificate certificate, String alias, PrivateKey privateKey, CertificateRole role);
    public List<X509Certificate> findKeyStoreCertificates(CertificateRole role) throws FileNotFoundException;


}
