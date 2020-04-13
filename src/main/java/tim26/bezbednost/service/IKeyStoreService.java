package tim26.bezbednost.service;

import org.springframework.beans.factory.annotation.Autowired;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.model.enumeration.CertificateRole;

import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

import java.io.FileNotFoundException;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.List;

public interface IKeyStoreService {

    public List<X509Certificate> findKeyStoreCertificatesByRole(CertificateRole role);
    public void saveCertificateToKeyStore(X509Certificate certificate, String alias, PrivateKey privateKey, CertificateRole role);
    public void saveWhenKeyStoreIsGenerating(X509Certificate certificate, String alias, PrivateKey privateKey, CertificateRole role);
    public void generateRootKeyStore() throws CertificateException, ParseException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException;
    public List<Certificate> readAll();
}
