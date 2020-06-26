package tim26.bezbednost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.krb5.internal.crypto.KeyUsage;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.keystore.KeyStoreReader;
import tim26.bezbednost.keystore.KeyStoreWriter;
import tim26.bezbednost.keystore.KeystoreConfig;
import tim26.bezbednost.model.enumeration.CertificateRole;

import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class KeyStoreService implements IKeyStoreService {


    @Autowired
    private KeyStoreWriter keyStoreWriter;

    @Autowired
    private KeyStoreReader keyStoreReader;

    @Autowired
    private CertificateService certificateService;


    public void saveCertificateToKeyStore(X509Certificate certificate, String alias, PrivateKey privateKey, CertificateRole role,String password) {

        if( role.equals(CertificateRole.ROOT)) {
            keyStoreWriter.loadKeyStore("./jks/root.jks", password.toCharArray());
            keyStoreWriter.write(alias, privateKey, password.toCharArray(), certificate);
            keyStoreWriter.saveKeyStore("./jks/root.jks", password.toCharArray());

        } else if(role.equals(CertificateRole.INTERMEDIATE)) {

            keyStoreWriter.loadKeyStore("./jks/intermediate.jks" ,password.toCharArray());
            keyStoreWriter.write(alias, privateKey,password.toCharArray(), certificate);
            keyStoreWriter.saveKeyStore("./jks/intermediate.jks", password.toCharArray());

        } else if(role.equals(CertificateRole.ENDENTITY)){
            keyStoreWriter.loadKeyStore("./jks/end-entity.jks", password.toCharArray());
            keyStoreWriter.write(alias, privateKey, password.toCharArray(), certificate);
            keyStoreWriter.saveKeyStore("./jks/end-entity.jks", password.toCharArray());
        }
    }

    @Override
    public void saveWhenKeyStoreIsGenerating(X509Certificate certificate, String alias, PrivateKey privateKey, CertificateRole role,String password) {

            if( role.equals(CertificateRole.ROOT)) {
                keyStoreWriter.loadKeyStore(null , password.toCharArray());
                keyStoreWriter.write(alias, privateKey, password.toCharArray(), certificate);
                keyStoreWriter.saveKeyStore("./jks/root.jks", password.toCharArray());

            } else if(role.equals(CertificateRole.INTERMEDIATE)) {

                keyStoreWriter.loadKeyStore(null ,password.toCharArray());
                keyStoreWriter.write(alias, privateKey,password.toCharArray(), certificate);
                keyStoreWriter.saveKeyStore("./jks/intermediate.jks", password.toCharArray());

            } else if(role.equals(CertificateRole.ENDENTITY)){
                keyStoreWriter.loadKeyStore(null, password.toCharArray());
                keyStoreWriter.write(alias, privateKey, password.toCharArray(), certificate);
                keyStoreWriter.saveKeyStore("./jks/end-entity.jks", password.toCharArray());
            }


    }


    public List<X509Certificate> findKeyStoreCertificatesByRole(CertificateRole role) {

        List<X509Certificate> returnlist = new ArrayList<>();

        if(role.equals(CertificateRole.ROOT)){

            List<Certificate> certificates =  this.keyStoreReader.readAllCertificates("./jks/root.jks", java.util.Base64.getEncoder().encodeToString(KeystoreConfig.ROOT_PASS).toCharArray());

            for(Certificate c : certificates){
                X509Certificate cert = (X509Certificate)c;
                returnlist.add(cert);
            }
            return returnlist;

        } else if(role.equals(CertificateRole.INTERMEDIATE)){

            List<Certificate> certificates =  this.keyStoreReader.readAllCertificates("./jks/intermediate.jks", java.util.Base64.getEncoder().encodeToString(KeystoreConfig.INTERMEDIATE_PASS).toCharArray());

            for(Certificate c : certificates){
                X509Certificate cert = (X509Certificate)c;
                returnlist.add(cert);
            }
            return returnlist;


        } else if (role.equals(CertificateRole.ENDENTITY)){

            List<Certificate> certificates =  this.keyStoreReader.readAllCertificates("./jks/end-entity.jks",  java.util.Base64.getEncoder().encodeToString(KeystoreConfig.END_ENTITY_PASS).toCharArray());

            for(Certificate c : certificates){
                X509Certificate cert = (X509Certificate)c;
                returnlist.add(cert);
            }
            return returnlist;
        }

        return returnlist;
    }


    public void generateRootKeyStore() throws CertificateException, ParseException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException {

        CertificateX509NameDto certificatedto = new CertificateX509NameDto();
        certificatedto.setCommonName("Triof");
        certificatedto.setOrganization("TrioF organization");
        certificatedto.setOrganizationUnit("Software development unit");
        certificatedto.setCity("Novi Sad");
        certificatedto.setState("Vojvodina");
        certificatedto.setCertificateRole(CertificateRole.ROOT);
        certificatedto.setEmail("trioF@gmail.com");
        certificatedto.setPassword(KeystoreConfig.ROOT_PASS);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(10);
        certificatedto.setStartDate(startDate);
        certificatedto.setEndDate(endDate);

        certificateService.generateSelfSignedCertificate(certificatedto,true);
    }

    @Override
    public List<Certificate> readAll() {
       List<Certificate> returnlist =  keyStoreReader.readAllCertificates("./jks/intermediate.jks", java.util.Base64.getEncoder().encodeToString(KeystoreConfig.INTERMEDIATE_PASS).toCharArray());
       for(Certificate c :  returnlist){
           X509Certificate certificate = (X509Certificate)c;
           boolean[] k = certificate.getKeyUsage();
       }
       return returnlist;
    }


}
