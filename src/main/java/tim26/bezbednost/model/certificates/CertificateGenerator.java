package tim26.bezbednost.model.certificates;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.security.web.savedrequest.Enumerator;
import org.springframework.stereotype.Component;
import tim26.bezbednost.dto.ExstensionsDto;
import tim26.bezbednost.dto.ExtendedKeyUsageDto;
import tim26.bezbednost.dto.KeyUsageDto;


import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Component
public class  CertificateGenerator {

    public CertificateGenerator() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, boolean isCA, ExstensionsDto exstensionsDto) {
        try {
            //Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
            //Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
            //Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            //Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider("BC");

            //Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            Date start = java.sql.Date.valueOf(subjectData.getStartDate());
            Date end = java.sql.Date.valueOf(subjectData.getEndDate());
            
            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    start,
                    end,
                    subjectData.getX500name(),
                    subjectData.getPublicKey());
            //Generise se sertifikat


            BasicConstraints basicConstraints = new BasicConstraints(isCA);
            //true ako je CA
            //basic constraint je critial
            certGen.addExtension(Extension.basicConstraints, true, basicConstraints);

            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            //key-usage & extended key-usage
            if(exstensionsDto != null) {
                if (exstensionsDto.getKeyUsageDto() != null) {

                    List<Integer> checkKeyUsages = exstensionsDto.getKeyUsageDto().getAllNotNullValues();
                    if(checkKeyUsages.size() != 0)

                    {
                        Optional<Integer> ret = checkKeyUsages.stream().reduce((a, b) -> a | b);
                    KeyUsage keyUsage = new KeyUsage(ret.get());
                    boolean isCritical = false;
                    if (exstensionsDto.getKeyUsageDto().getIsCriticalKeyUsage() != null) {
                        isCritical = true;
                    }

                    certGen.addExtension(Extension.keyUsage, isCritical, keyUsage);

                }

                if (exstensionsDto.getExtendedKeyUsageDto() != null) {

                    ExtendedKeyUsageDto extended = exstensionsDto.getExtendedKeyUsageDto();
                    boolean isCritical = false;
                    if (extended.getIsCriticalExtendedKeyUsage() != null) {
                        isCritical = true;
                    }
                    KeyPurposeId[] extendedUsages = extended.getKeyUsages();
                    if(extendedUsages.length != 0) {
                        ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(extendedUsages);
                        certGen.addExtension(Extension.extendedKeyUsage, isCritical, extendedKeyUsage);
                    }
                }
            }


            X509CertificateHolder certHolder = certGen.build(contentSigner);


            X509Certificate certificate = certConverter.getCertificate(certHolder);
            Certificate certificate1 =  (Certificate)certificate;
            System.out.println(certificate1);

            //Konvertuje objekat u sertifikat
            return certConverter.getCertificate(certHolder);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (CertIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public KeyPair generateKeyPair(boolean isCertificateAuthority) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            
            if(isCertificateAuthority) {
                keyGen.initialize(4096, random);
            } else {
                keyGen.initialize(2048, random);
            }
            return keyGen.generateKeyPair();

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }

        return null;
    }
}
