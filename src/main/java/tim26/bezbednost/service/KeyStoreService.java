package tim26.bezbednost.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.keystore.KeyStoreReader;
import tim26.bezbednost.keystore.KeyStoreWriter;
import tim26.bezbednost.model.certificates.CertificateGenerator;
import tim26.bezbednost.model.certificates.IssuerData;
import tim26.bezbednost.model.certificates.SubjectData;
import tim26.bezbednost.model.enumeration.CertificateRole;

import java.io.FileNotFoundException;
import java.math.BigInteger;
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

    @Autowired
    private CertificateGenerator certificateGenerator;


    public void saveCertificateToKeyStore(X509Certificate certificate, String alias, PrivateKey privateKey, CertificateRole role) {

        if( role.equals(CertificateRole.ROOT)) {
            keyStoreWriter.loadKeyStore("./jks/root.jks", "root".toCharArray());
            keyStoreWriter.write(alias, privateKey, "root".toCharArray(), certificate);
            keyStoreWriter.saveKeyStore("./jks/root.jks", "root".toCharArray());

        } else if(role.equals(CertificateRole.INTERMEDIATE)) {

            keyStoreWriter.loadKeyStore("./jks/intermediate.jks" ,"intermediate".toCharArray());
            keyStoreWriter.write(alias, privateKey,"intermediate".toCharArray(), certificate);
            keyStoreWriter.saveKeyStore("./jks/intermediate.jks", "intermediate".toCharArray());

        } else if(role.equals(CertificateRole.ENDENTITY)){
            keyStoreWriter.loadKeyStore("./jks/end-entity.jks", "end-entity".toCharArray());
            keyStoreWriter.write(alias, privateKey, "end-entity".toCharArray(), certificate);
            keyStoreWriter.saveKeyStore("./jks/intermediate.jks", "end-entity".toCharArray());
        }
    }

    @Override
    public void saveWhenKeyStoreIsGenerating(X509Certificate certificate, String alias, PrivateKey privateKey, CertificateRole role) {

            if( role.equals(CertificateRole.ROOT)) {
                keyStoreWriter.loadKeyStore(null , "root".toCharArray());
                keyStoreWriter.write(alias, privateKey, "root".toCharArray(), certificate);
                keyStoreWriter.saveKeyStore("./jks/root.jks", "root".toCharArray());

            } else if(role.equals(CertificateRole.INTERMEDIATE)) {

                keyStoreWriter.loadKeyStore(null ,"intermediate".toCharArray());
                keyStoreWriter.write(alias, privateKey,"intermediate".toCharArray(), certificate);
                keyStoreWriter.saveKeyStore("./jks/intermediate.jks", "intermediate".toCharArray());

            } else if(role.equals(CertificateRole.ENDENTITY)){
                keyStoreWriter.loadKeyStore(null, "end-entity".toCharArray());
                keyStoreWriter.write(alias, privateKey, "end-entity".toCharArray(), certificate);
                keyStoreWriter.saveKeyStore("./jks/end-entity.jks", "end-entity".toCharArray());
            }


    }


    public List<X509Certificate> findKeyStoreCertificatesByRole(CertificateRole role) throws FileNotFoundException {

        List<X509Certificate> returnlist = new ArrayList<>();

        if(role.equals(CertificateRole.ROOT)){

            List<Certificate> certificates =  this.keyStoreReader.readAllCertificates("./jks/root.jks", "root".toCharArray());


            for(Certificate c : certificates){
                X509Certificate cert = (X509Certificate)c;
                returnlist.add(cert);
            }

            return returnlist;

        } else if(role.equals(CertificateRole.INTERMEDIATE)){

            List<Certificate> certificates =  this.keyStoreReader.readAllCertificates("./jks/intermediate.jks", "intermediate".toCharArray());


            for(Certificate c : certificates){
                X509Certificate cert = (X509Certificate)c;
                returnlist.add(cert);
            }
            return returnlist;


        } else if (role.equals(CertificateRole.ENDENTITY)){


            List<Certificate> certificates =  this.keyStoreReader.readAllCertificates("./jks/end-entity.jks", "end-entity".toCharArray());

            for(Certificate c : certificates){
                X509Certificate cert = (X509Certificate)c;
                returnlist.add(cert);
            }
            return returnlist;
        }

        return returnlist;

    }

    public void generateRootKeyStore() throws CertificateException, ParseException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException {

        //keyStoreWriter.loadKeyStore(null, "root".toCharArray());

        CertificateX509NameDto certificatedto = new CertificateX509NameDto();
        certificatedto.setCommonName("*.triof.org");
        certificatedto.setOrganization("TrioF organization");
        certificatedto.setOrganizationUnit("Software development unit");
        certificatedto.setCity("Novi Sad");
        certificatedto.setState("Vojvodina");
        certificatedto.setCertificateRole(CertificateRole.ROOT);
        certificatedto.setEmail("trioF@gmail.com");

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(10);
        certificatedto.setStartDate(startDate);
        certificatedto.setEndDate(endDate);

        //????
        //certificatedto.setSerialNumber("345");

        certificateService.generateSelfSignedCertificate(certificatedto,true);



        //keyStoreWriter.saveKeyStore("./jks/root.jks","root".toCharArray());

    }

    public void generateIntermediateKeyStore(String alias, CertificateX509NameDto certificatedto, boolean isCA) throws CertificateException, ParseException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, FileNotFoundException {

        //keyStoreWriter.loadKeyStore(null, "intermediate".toCharArray());
        if(isCA) {
            certificateService.generateCACertificate(certificatedto, alias,true);
        } else {
            certificateService.generateCertificateNotCA(certificatedto, alias,true);
        }

        //keyStoreWriter.saveKeyStore("./jks/intermediate.jks", "intermediate".toCharArray());

    }

    public void generateEndEntityKeyStore(String alias, CertificateX509NameDto certificatedto) throws FileNotFoundException {

        //keyStoreWriter.loadKeyStore(null, "end-entity".toCharArray());
        certificateService.generateCertificateNotCA(certificatedto,alias,true);
        //keyStoreWriter.saveKeyStore("./jks/end-entity.jks","end-entity".toCharArray());


    }


    }
