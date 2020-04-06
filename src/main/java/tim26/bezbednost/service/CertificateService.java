package tim26.bezbednost.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tim26.bezbednost.dto.CertificateDto;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.model.Certificate;
import tim26.bezbednost.model.certificates.CertificateGenerator;
import tim26.bezbednost.model.certificates.IssuerData;
import tim26.bezbednost.model.certificates.SubjectData;
import tim26.bezbednost.model.enumeration.CertificateRole;
import tim26.bezbednost.repository.CertificateRepository;
import tim26.bezbednost.repository.KeyStoreRepository;

import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CertificateService implements ICertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private KeyStoreRepository keyStoreRepository;

    @Autowired
    private CertificateGenerator certificateGenerator;

    @Autowired
    private KeyStoreService keyStoreService;

    @Override
    public List<CertificateDto> findAll() {
        List<Certificate> certificates = certificateRepository.findAll();

        List<CertificateDto> certificateDtos = new ArrayList<CertificateDto>();

        for (Certificate c : certificates) {
            certificateDtos.add(modelMapper.map(c, CertificateDto.class));
        }

        return certificateDtos;
    }

    public SubjectData generateSubjectData(CertificateX509NameDto certificateX509NameDto) {
        return null;
    }

    public IssuerData generateIssuerData(CertificateX509NameDto certificateX509NameDto, PrivateKey privateKey) {
        return null;
    }

    public void generateSelfSignedCertificate(CertificateX509NameDto certificateX509NameDto) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ParseException {

        //keypair za subjekta i issuera je isti, jer je self-signed sertifikat
        SubjectData subject = generateSubjectData(certificateX509NameDto);
        IssuerData issuer = generateIssuerData(certificateX509NameDto, subject.getPrivateKey());

        X509Certificate cert = certificateGenerator.generateCertificate(subject, issuer);
        cert.verify(subject.getPublicKey());

        System.out.println("\n===== Certificate issuer=====");
        System.out.println(cert.getIssuerX500Principal().getName());
        System.out.println("\n===== Certicate owner =====");
        System.out.println(cert.getSubjectX500Principal().getName());
        System.out.println("\n===== Certificate =====");
        System.out.println("-------------------------------------------------------");
        System.out.println(cert);
        System.out.println("-------------------------------------------------------");

        //save the cert in the keystore
        keyStoreService.saveCertificate(cert, certificateX509NameDto.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.ROOT);

        //sacuvaj u bazu
    }



}

