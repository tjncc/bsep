package tim26.bezbednost.service;

import jdk.vm.ci.meta.Local;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.management.relation.Role;
import java.io.FileNotFoundException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CertificateService implements ICertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private KeyStoreReader keyStoreReader;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IKeyStoreService keyStoreService;

    @Autowired
    private KeyStoreRepository keyStoreRepository;

    @Autowired
    private CertificateGenerator certificateGenerator;


    @Override
    public List<CertificateDto> findAll() {
        List<Certificate> certificates = certificateRepository.findAll();

        List<CertificateDto> certificateDtos = new ArrayList<CertificateDto>();

        for (Certificate c : certificates) {
            certificateDtos.add(modelMapper.map(c, CertificateDto.class));
        }

        return certificateDtos;
    }

    @Override
    public CertificateX509NameDto[] getIssuerAndSubjectData(String serialNumber, CertificateRole certificateRole) throws FileNotFoundException {

        List<X509Certificate> x509certificates = keyStoreService.findKeyStoreCertificatesByRole(certificateRole);

        for(X509Certificate certificate : x509certificates){

            if(certificate.getSerialNumber().toString().equals(serialNumber)){

            }
        }


        return new CertificateX509NameDto[0];
    }

    @Override
    public SubjectData generateSubjectData(CertificateX509NameDto certificateDto) {

        SubjectData subjectData = new SubjectData();

        KeyPair keyPair;

        if(certificateDto.getCertificateRole().equals(CertificateRole.ENDENTITY)){
            keyPair = certificateGenerator.generateKeyPair(false);
        } else {
            keyPair = certificateGenerator.generateKeyPair(true);
        }

        subjectData.setPublicKey(keyPair.getPublic());

        LocalDate startDate = LocalDate.now();
        LocalDate endDate;
        if(certificateDto.getCertificateRole().equals(CertificateRole.ROOT)){
            endDate = startDate.plusYears(10);
        } else if (certificateDto.getCertificateRole().equals(CertificateRole.INTERMEDIATE)){
            endDate = startDate.plusYears(5);
        } else {
            endDate = startDate.plusYears(2);
        }

        subjectData.setStartDate(startDate);
        subjectData.setEndDate(endDate);

        SecureRandom randomNum = new SecureRandom();
        int serialNum = randomNum.nextInt();
        subjectData.setSerialNumber(String.valueOf(serialNum));

        X500NameBuilder nameBuilder = new X500NameBuilder();
        // o - organisation
        nameBuilder.addRDN(BCStyle.O, certificateDto.getOrganization());
        // e - email
        nameBuilder.addRDN(BCStyle.E, certificateDto.getEmail());
        // c - city
        nameBuilder.addRDN(BCStyle.C, certificateDto.getCity());
        // st - state
        nameBuilder.addRDN(BCStyle.ST, certificateDto.getState());
        // ou - organisation unit
        nameBuilder.addRDN(BCStyle.OU, certificateDto.getOrganizationUnit());
        // cn - common name
        nameBuilder.addRDN(BCStyle.CN, certificateDto.getCommonName());
        // L - location
        //nameBuilder.addRDN(BCStyle.L, certificateDto.getLocation());
        // ID korisnika ?

        subjectData.setX500name(nameBuilder.build());

        return subjectData;
    }

    @Override
    public IssuerData generateIssuerData(CertificateX509NameDto certificateDto, PrivateKey privateKey) {

        IssuerData issuerData = new IssuerData();
        issuerData.setPrivateKey(privateKey);

        X500NameBuilder nameBuilder = new X500NameBuilder();
        // o - organisation
        nameBuilder.addRDN(BCStyle.O, certificateDto.getOrganization());
        // e - email
        nameBuilder.addRDN(BCStyle.E, certificateDto.getEmail());
        // c - city
        nameBuilder.addRDN(BCStyle.C, certificateDto.getCity());
        // st - state
        nameBuilder.addRDN(BCStyle.ST, certificateDto.getState());
        // ou - organisation unit
        nameBuilder.addRDN(BCStyle.OU, certificateDto.getOrganizationUnit());
        // cn - common name
        nameBuilder.addRDN(BCStyle.CN, certificateDto.getCommonName());
        // L - location
        //nameBuilder.addRDN(BCStyle.L, certificateDto.getLocation());
        // ID korisnika ?
        issuerData.setX500name(nameBuilder.build());

        return issuerData;
    }



    public void generateSelfSignedCertificate(CertificateX509NameDto certificateX509NameDto) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ParseException {

        SubjectData subject = generateSubjectData(certificateX509NameDto);
        IssuerData issuer = generateIssuerData(certificateX509NameDto, subject.getPrivateKey());

        X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer);
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

                    X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer);

                    keyStoreService.saveCertificateToKeyStore(certificate, certificateX509NameDto.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);
                    certificateRepository.save(new Certificate(subject.getSerialNumber(), CertificateRole.INTERMEDIATE));
                }else {

                    IssuerData issuer = keyStoreReader.readIssuerFromStore("../../../../../jks/intermediate.jks",
                            c.getSerialNumber(),
                            "intermediate".toCharArray(),
                            "intermediate".toCharArray());

                    X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer);

                    keyStoreService.saveCertificateToKeyStore(certificate, certificateX509NameDto.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);
                    this.certificateRepository.save(new Certificate(subject.getSerialNumber(), CertificateRole.INTERMEDIATE));
                }
            }
        }

    }




}

