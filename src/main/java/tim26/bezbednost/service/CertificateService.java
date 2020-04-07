package tim26.bezbednost.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tim26.bezbednost.dto.CertificateDto;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.keystore.KeyStoreReader;
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

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private KeyStoreReader keyStoreReader;

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

        SubjectData subject = generateSubjectData(certificateX509NameDto);
        IssuerData issuer = generateIssuerData(certificateX509NameDto, subject.getPrivateKey());

        X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer,true);
        certificate.verify(subject.getPublicKey());

        String certificateIssuer = certificate.getIssuerX500Principal().getName();
        String certificateOwner = certificate.getSubjectX500Principal().getName();

        keyStoreService.saveCertificateToKeyStore(certificate, certificateX509NameDto.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.ROOT);

        Certificate certificate1 = new Certificate(subject.getSerialNumber(), CertificateRole.ROOT);

        certificateRepository.save(certificate1);
    }

    public void generateCACertificate(CertificateX509NameDto certificateX509NameDto, String serialNumber) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ParseException {

        SubjectData subject = generateSubjectData(certificateX509NameDto);
        List<CertificateDto> certificateDtoList = findAll();
        for(CertificateDto c : certificateDtoList) {
            if(c.getSerialNumber().equals(serialNumber)) {
                if(c.getCertificateRole() == CertificateRole.ROOT) {

                    IssuerData issuer = keyStoreReader.readIssuerFromStore("../../../../../jks/root.jks",
                            c.getSerialNumber(), "root".toCharArray(),
                            "root".toCharArray());

                    X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer,true);

                    keyStoreService.saveCertificateToKeyStore(certificate, certificateX509NameDto.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);
                    certificateRepository.save(new Certificate(subject.getSerialNumber(), CertificateRole.INTERMEDIATE));
                }else {

                    IssuerData issuer = keyStoreReader.readIssuerFromStore("../../../../../jks/intermediate.jks",
                            c.getSerialNumber(),
                            "intermediate".toCharArray(),
                            "intermediate".toCharArray());

                    X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer,true);

                    keyStoreService.saveCertificateToKeyStore(certificate, certificateX509NameDto.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);
                    this.certificateRepository.save(new Certificate(subject.getSerialNumber(), CertificateRole.INTERMEDIATE));
                }
            }
        }

    }




}

