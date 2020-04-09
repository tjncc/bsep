package tim26.bezbednost.service;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
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
import tim26.bezbednost.model.enumeration.CertificateStatus;
import tim26.bezbednost.model.enumeration.CertificateType;
import tim26.bezbednost.repository.CertificateRepository;
import tim26.bezbednost.repository.KeyStoreRepository;


import javax.annotation.PostConstruct;
import javax.management.relation.Role;
import java.io.FileNotFoundException;
import java.math.BigInteger;
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
import java.util.Random;

@Service
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
    private CertificateGenerator certificateGenerator;

    @PostConstruct
    public void init() throws CertificateException, ParseException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException {
        keyStoreService.generateRootKeyStore();
    }


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

        PublicKey pk = keyPair.getPublic();

        subjectData.setPublicKey(keyPair.getPublic());
        subjectData.setPrivateKey(keyPair.getPrivate());

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



    public void generateSelfSignedCertificate(CertificateX509NameDto certificateX509NameDto,boolean isFirstTime) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ParseException {

        SubjectData subject = generateSubjectData(certificateX509NameDto);
        IssuerData issuer = generateIssuerData(certificateX509NameDto, subject.getPrivateKey());

        X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer,true);
        certificate.verify(subject.getPublicKey());

        String certificateIssuer = certificate.getIssuerX500Principal().getName();
        String certificateOwner = certificate.getSubjectX500Principal().getName();

        if(isFirstTime) {
            keyStoreService.saveWhenKeyStoreIsGenerating(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.ROOT);

        } else {
            keyStoreService.saveCertificateToKeyStore(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.ROOT);

        }

        Certificate certificate1 = new Certificate(subject.getSerialNumber(), CertificateRole.ROOT,CertificateType.CA,certificateX509NameDto.getCommonName(), certificateX509NameDto.getStartDate(), certificateX509NameDto.getEndDate(), CertificateStatus.VALID);

        certificateRepository.save(certificate1);
    }

    public void generateCACertificate(CertificateX509NameDto certificateX509NameDto, String serialNumber,boolean isFirstTime) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ParseException {

        SubjectData subject = generateSubjectData(certificateX509NameDto);
        List<CertificateDto> certificateDtoList = findAll();
        for (CertificateDto c : certificateDtoList) {
            if (c.getSerialNumber().equals(serialNumber)) {
                if (c.getCertificateRole() == CertificateRole.ROOT) {

                    IssuerData issuer = keyStoreReader.readIssuerFromStore("./jks/root.jks",
                            c.getSerialNumber(), "root".toCharArray(),
                            "root".toCharArray());

                    X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer, true);
                    if(isFirstTime){
                        keyStoreService.saveWhenKeyStoreIsGenerating(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);

                    }else{
                        keyStoreService.saveCertificateToKeyStore(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);
                    }
                    if(certificateX509NameDto.getEndDate() == null){
                        certificateX509NameDto.setEndDate(certificateX509NameDto.getStartDate().plusYears(5));
                    }
                    certificateRepository.save(new Certificate(subject.getSerialNumber(), CertificateRole.INTERMEDIATE, CertificateType.CA,certificateX509NameDto.getCommonName(), certificateX509NameDto.getStartDate(), certificateX509NameDto.getEndDate(), CertificateStatus.VALID));
                } else {

                    IssuerData issuer = keyStoreReader.readIssuerFromStore("./jks/intermediate.jks",
                            c.getSerialNumber(),
                            "intermediate".toCharArray(),
                            "intermediate".toCharArray());

                    X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer, true);
                    if(isFirstTime){
                        keyStoreService.saveWhenKeyStoreIsGenerating(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);

                    }else {
                        keyStoreService.saveCertificateToKeyStore(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);
                        }
                    if(certificateX509NameDto.getEndDate() == null){
                        certificateX509NameDto.setEndDate(certificateX509NameDto.getStartDate().plusYears(5));
                    }
                    this.certificateRepository.save(new Certificate(subject.getSerialNumber(), CertificateRole.INTERMEDIATE, CertificateType.CA,certificateX509NameDto.getCommonName(), certificateX509NameDto.getStartDate(), certificateX509NameDto.getEndDate(), CertificateStatus.VALID));
                }
            }
        }
    }


        public X509Certificate generateCertificateNotCA(CertificateX509NameDto certificatedto,String alias,boolean isFristTime) throws FileNotFoundException {

            SubjectData subject = generateSubjectData(certificatedto);
            IssuerData issuer =  null;
            X509Certificate returnc = null;

            List<Certificate> certsRoots = certificateRepository.findAllByRole(CertificateRole.ROOT);
            List<Certificate> certIntermediates =  certificateRepository.findAllByRole(CertificateRole.INTERMEDIATE);
            List<Certificate> all = new ArrayList<>();
            all.addAll(certsRoots);
            all.addAll(certIntermediates);

            for(Certificate c : all) {

                if(c.getSerialNumber().equals(alias)) {

                        try
                       {
                           issuer = keyStoreReader.readIssuerFromStore("./jks/root.jks",
                                String.valueOf(c.getSerialNumber()), "root".toCharArray(),
                                "root".toCharArray());
                       }
                        catch (Exception e){

                            issuer = keyStoreReader.readIssuerFromStore("./jks/intermediate.jks",
                                    String.valueOf(c.getSerialNumber()), "intermediate".toCharArray(),
                                    "intermediate".toCharArray());
                        }

                    returnc = certificateGenerator.generateCertificate(subject, issuer,false);

                    if(isFristTime == false){
                        keyStoreService.saveCertificateToKeyStore(returnc, subject.getSerialNumber(), issuer.getPrivateKey(), certificatedto.getCertificateRole());
                    }else{
                        keyStoreService.saveWhenKeyStoreIsGenerating(returnc, subject.getSerialNumber(), issuer.getPrivateKey(), certificatedto.getCertificateRole());
                    }

                    if(certificatedto.getEndDate() == null){
                        certificatedto.setEndDate(certificatedto.getStartDate().plusYears(2));
                    }

                    certificateRepository.save(new Certificate(subject.getSerialNumber(), certificatedto.getCertificateRole(), CertificateType.ENDENTITY,certificatedto.getCommonName(), certificatedto.getStartDate(), certificatedto.getEndDate(), CertificateStatus.VALID));
                    return returnc;

                }
            }

            return  returnc;
        }


        public List<CertificateX509NameDto> getAllCACertificates() throws FileNotFoundException {

            List<Certificate> allCA = certificateRepository.findAllByType(CertificateType.CA);
            List<CertificateX509NameDto> returns =  new ArrayList<>();

            for(Certificate c : allCA){
                CertificateX509NameDto dto = modelMapper.map(c,CertificateX509NameDto.class);
                returns.add(dto);
            }

            return returns;
        }


        public void generateRoot() throws CertificateException, ParseException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException {
        keyStoreService.generateRootKeyStore();
        }

    @Override
    public boolean save(CertificateX509NameDto certificateX509NameDto) throws CertificateException, ParseException, NoSuchAlgorithmException, FileNotFoundException, SignatureException, NoSuchProviderException, InvalidKeyException {

        LocalDate start = LocalDate.now();
        certificateX509NameDto.setStartDate(start);

        String alias = certificateX509NameDto.getIssuerSerialNumber();

        if(certificateX509NameDto.getSubjectType() == CertificateType.CA) {

            certificateX509NameDto.setCertificateRole(CertificateRole.INTERMEDIATE);

            if (certificateRepository.findAllByRole(CertificateRole.INTERMEDIATE).size() == 0) {
                    generateCACertificate(certificateX509NameDto, alias,true);
                return true;
            } else {
                generateCACertificate(certificateX509NameDto, alias,false);
                return true;
            }

        } else if(certificateX509NameDto.getSubjectType() ==  CertificateType.ENDENTITY){

            certificateX509NameDto.setCertificateRole(CertificateRole.ENDENTITY);

            if (certificateRepository.findAllByRole(CertificateRole.ENDENTITY).size() == 0) {
                generateCertificateNotCA(certificateX509NameDto, alias,true);
                return true;
            }else {
                generateCertificateNotCA(certificateX509NameDto, alias,false);
                return true;
            }

        } else {
            return false;
        }
    }

    @Override
    public String generateSerialNumber() {

        Random rand = new Random();
        int serialNumber = rand.nextInt(10000);
        String StringSerialNumber = String.valueOf(serialNumber);

        while(certificateRepository.findBySerialNumber(StringSerialNumber) != null) {
            serialNumber = rand.nextInt(10000);
            StringSerialNumber = String.valueOf(serialNumber);
        }

        return  StringSerialNumber;
    }


}

