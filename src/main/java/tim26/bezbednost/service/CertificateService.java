package tim26.bezbednost.service;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tim26.bezbednost.dto.CertificateDto;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.keystore.KeyStoreReader;
import tim26.bezbednost.model.certificates.CertificateGenerator;
import tim26.bezbednost.model.certificates.IssuerData;
import tim26.bezbednost.model.certificates.SubjectData;
import tim26.bezbednost.model.Certificate;
import tim26.bezbednost.model.enumeration.CertificateRole;
import tim26.bezbednost.model.enumeration.CertificateStatus;
import tim26.bezbednost.model.enumeration.CertificateType;
import tim26.bezbednost.repository.CertificateRepository;


import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.time.LocalDate;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.*;

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

            if(c.getCertificateStatus().equals(CertificateStatus.VALID)) {
                certificateDtos.add(modelMapper.map(c, CertificateDto.class));
            }
        }

        //keyStoreService.readAll();

        return certificateDtos;
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
        subjectData.setPrivateKey(keyPair.getPrivate());

        Certificate issuerCert = null;
        if(!certificateDto.getCertificateRole().equals(CertificateRole.ROOT)) {
            issuerCert = certificateRepository.findBySerialNumber(certificateDto.getIssuerSerialNumber());
        }
        LocalDate startDate = LocalDate.now();
        LocalDate endDate;

        if(certificateDto.getCertificateRole().equals(CertificateRole.ROOT)){
            endDate = startDate.plusYears(10);

        } else if (certificateDto.getCertificateRole().equals(CertificateRole.INTERMEDIATE)){

            if(issuerCert.getValidTo().compareTo(startDate.plusYears(5)) < 0) {
                endDate = issuerCert.getValidTo();
            } else {
                endDate = startDate.plusYears(5);
            }
        } else {

            if(issuerCert.getValidTo().compareTo(startDate.plusYears(2)) < 0) {
                endDate = issuerCert.getValidTo();
            } else {
                endDate = startDate.plusYears(2);
            }
        }

        subjectData.setStartDate(startDate);
        subjectData.setEndDate(endDate);

        String serialNum = generateSerialNumber();
        subjectData.setSerialNumber(serialNum);

        X500NameBuilder nameBuilder = new X500NameBuilder();
        nameBuilder.addRDN(BCStyle.O, certificateDto.getOrganization());
        nameBuilder.addRDN(BCStyle.E, certificateDto.getEmail());
        nameBuilder.addRDN(BCStyle.C, certificateDto.getCity());
        nameBuilder.addRDN(BCStyle.ST, certificateDto.getState());
        nameBuilder.addRDN(BCStyle.OU, certificateDto.getOrganizationUnit());
        nameBuilder.addRDN(BCStyle.CN, certificateDto.getCommonName());

        subjectData.setX500name(nameBuilder.build());

        return subjectData;
    }

    @Override
    public IssuerData generateIssuerData(CertificateX509NameDto certificateDto, PrivateKey privateKey) {

        IssuerData issuerData = new IssuerData();
        issuerData.setPrivateKey(privateKey);

        X500NameBuilder nameBuilder = new X500NameBuilder();
        nameBuilder.addRDN(BCStyle.O, certificateDto.getOrganization());
        nameBuilder.addRDN(BCStyle.E, certificateDto.getEmail());
        nameBuilder.addRDN(BCStyle.C, certificateDto.getCity());
        nameBuilder.addRDN(BCStyle.ST, certificateDto.getState());
        nameBuilder.addRDN(BCStyle.OU, certificateDto.getOrganizationUnit());
        nameBuilder.addRDN(BCStyle.CN, certificateDto.getCommonName());

        issuerData.setX500name(nameBuilder.build());

        return issuerData;
    }



    public void generateSelfSignedCertificate(CertificateX509NameDto certificateX509NameDto,boolean isFirstTime) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ParseException {

        SubjectData subject = generateSubjectData(certificateX509NameDto);
        IssuerData issuer = generateIssuerData(certificateX509NameDto, subject.getPrivateKey());

        List<Certificate> certs = getAllRoots();
        if(certs.size() != 0){
            return;
        }


        X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer,true,certificateX509NameDto.getExstensionsDto());
        certificate.verify(subject.getPublicKey());

        if(isFirstTime) {
            keyStoreService.saveWhenKeyStoreIsGenerating(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.ROOT);
        } else {
            keyStoreService.saveCertificateToKeyStore(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.ROOT);
        }

        int roots = 1;
        List<Certificate> allC = certificateRepository.findAllByRole(CertificateRole.ROOT);
        if(allC.isEmpty()){
            roots = 1;
        } else {
            roots = allC.size() + 1;
        }

        Certificate certificate1 = new  Certificate(subject.getSerialNumber(), CertificateRole.ROOT,CertificateType.CA,certificateX509NameDto.getCommonName(), certificateX509NameDto.getStartDate(), certificateX509NameDto.getEndDate(), CertificateStatus.VALID,0, String.valueOf(roots));
        certificateRepository.save(certificate1);
    }

    @Override
    public boolean revoke(CertificateX509NameDto certificateX509NameDto) {

        Certificate certDb = certificateRepository.findBySerialNumber(certificateX509NameDto.getSerialNumber());
        certDb.setCertificateStatus(CertificateStatus.REVOKED);
        certificateRepository.save(certDb);

        //ako je end-entity unistava se samo on i ne ide dalje
        if(certificateX509NameDto.getCertificateRole().equals(CertificateRole.ENDENTITY)) {
            return true;
        }

        List<Certificate> all = certificateRepository.findAll();

        if(certificateX509NameDto.getCertificateRole() == CertificateRole.ROOT) {
            //unistavaju se svi
            for(Certificate c : all){
                Certificate certificate1 = certificateRepository.findBySerialNumber(c.getSerialNumber());
                certificate1.setCertificateStatus(CertificateStatus.REVOKED);
                certificateRepository.save(certificate1);
            }
            return true;

        } else if(certificateX509NameDto.getCertificateRole() == CertificateRole.INTERMEDIATE) {

            for(Certificate c : all) {
                //Certificate certificate1 = certificateRepository.findBySerialNumber(c.getSerialNumber());
                if(c.getCode().contains(certDb.getCode())){
                    c.setCertificateStatus(CertificateStatus.REVOKED);
                    certificateRepository.save(c);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<CertificateDto> findAllRevoked() {
        List<Certificate> certificates = certificateRepository.findAll();

        List<CertificateDto> certificateDtos = new ArrayList<CertificateDto>();

        for (Certificate c : certificates) {
            if(c.getCertificateStatus() == CertificateStatus.REVOKED)
            certificateDtos.add(modelMapper.map(c, CertificateDto.class));
        }

        return certificateDtos;
    }

    public List<Certificate> getAllRoots() {

        List<Certificate> certificates = certificateRepository.findAllByRole(CertificateRole.ROOT);
        List<Certificate> returnlist = new ArrayList<>();

        if(certificates.size() != 0){

            for(Certificate c : certificates){
                if(c.getCertificateStatus().equals(CertificateStatus.VALID)){
                    returnlist.add(c);
                }
            }
        }
        return returnlist;
    }


    public void generateCACertificate(CertificateX509NameDto certificateX509NameDto, String alias, boolean isFirstTime) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ParseException {

        SubjectData subject = generateSubjectData(certificateX509NameDto);
        List<Certificate> certificateDtoList = certificateRepository.findAll();

        for (Certificate c : certificateDtoList) {
            if (c.getSerialNumber().equals(alias) && c.getCertificateStatus().equals(CertificateStatus.VALID)) {

                c.setChildren(c.getChildren()+1);

                if (c.getRole() == CertificateRole.ROOT) {

                    IssuerData issuer = keyStoreReader.readIssuerFromStore("./jks/root.jks",
                            c.getSerialNumber(), "root".toCharArray(),
                            "root".toCharArray());

                    X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer, true,certificateX509NameDto.getExstensionsDto());


                    if(isFirstTime){
                        keyStoreService.saveWhenKeyStoreIsGenerating(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);
                    } else{
                        keyStoreService.saveCertificateToKeyStore(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);
                    }

                    if(certificateX509NameDto.getEndDate() == null){
                        certificateX509NameDto.setEndDate(certificateX509NameDto.getStartDate().plusYears(5));
                    }
                    certificateRepository.save(new Certificate(subject.getSerialNumber(), CertificateRole.INTERMEDIATE, CertificateType.CA,certificateX509NameDto.getCommonName(), subject.getStartDate(), subject.getEndDate(), CertificateStatus.VALID, 0,c.getCode() + c.getChildren()));

                } else {

                    IssuerData issuer = keyStoreReader.readIssuerFromStore("./jks/intermediate.jks",
                            c.getSerialNumber(),
                            "intermediate".toCharArray(),
                            "intermediate".toCharArray());

                    X509Certificate certificate = certificateGenerator.generateCertificate(subject, issuer, true,certificateX509NameDto.getExstensionsDto());

                    if(isFirstTime){
                        keyStoreService.saveWhenKeyStoreIsGenerating(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);
                    } else {
                        keyStoreService.saveCertificateToKeyStore(certificate, subject.getSerialNumber(), issuer.getPrivateKey(), CertificateRole.INTERMEDIATE);
                    }

                    certificateRepository.save(new Certificate(subject.getSerialNumber(), CertificateRole.INTERMEDIATE, CertificateType.CA,certificateX509NameDto.getCommonName(), subject.getStartDate(), subject.getEndDate(), CertificateStatus.VALID,0,c.getCode() + c.getChildren()));
                }
            }
        }
    }


    public void generateCertificateNotCA(CertificateX509NameDto certificatedto,String alias,boolean isFristTime) throws FileNotFoundException {

        SubjectData subject = generateSubjectData(certificatedto);
        IssuerData issuer =  null;
        X509Certificate returnc = null;

        List<Certificate> certsRoots = certificateRepository.findAllByRole(CertificateRole.ROOT);
        List<Certificate> certIntermediates =  certificateRepository.findAllByRole(CertificateRole.INTERMEDIATE);
        List<Certificate> all = new ArrayList<>();
        all.addAll(certsRoots);
        all.addAll(certIntermediates);

        for(Certificate c : all) {

            if(c.getSerialNumber().equals(alias) && c.getCertificateStatus().equals(CertificateStatus.VALID)) {

                c.setChildren(c.getChildren()+1);
                try
                {
                    issuer = keyStoreReader.readIssuerFromStore("./jks/root.jks",
                            String.valueOf(c.getSerialNumber()), "root".toCharArray(),
                            "root".toCharArray());
                }
                catch (Exception e)
                {
                    issuer = keyStoreReader.readIssuerFromStore("./jks/intermediate.jks",
                            String.valueOf(c.getSerialNumber()), "intermediate".toCharArray(),
                            "intermediate".toCharArray());
                }

                returnc = certificateGenerator.generateCertificate(subject, issuer,false,certificatedto.getExstensionsDto());


                if(isFristTime == false){
                    keyStoreService.saveCertificateToKeyStore(returnc, subject.getSerialNumber(), issuer.getPrivateKey(), certificatedto.getCertificateRole());
                } else {
                    keyStoreService.saveWhenKeyStoreIsGenerating(returnc, subject.getSerialNumber(), issuer.getPrivateKey(), certificatedto.getCertificateRole());
                }

                certificateRepository.save(new Certificate(subject.getSerialNumber(), certificatedto.getCertificateRole(), CertificateType.ENDENTITY,certificatedto.getCommonName(), subject.getStartDate(), subject.getEndDate(), CertificateStatus.VALID, 0, c.getCode() + c.getChildren()));
            }
        }
    }


    public List<CertificateX509NameDto> getAllCACertificates() {

        List<Certificate> allCA = certificateRepository.findAllByType(CertificateType.CA);
        List<CertificateX509NameDto> returns =  new ArrayList<>();

        for(Certificate c : allCA){
            if(c.getCertificateStatus().equals(CertificateStatus.VALID)){
                CertificateX509NameDto dto = modelMapper.map(c,CertificateX509NameDto.class);
                returns.add(dto);
            }

        }

        return returns;
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
    public boolean downloadCertificate(CertificateX509NameDto certificateX509NameDto) throws IOException, CertificateEncodingException {

        java.security.cert.Certificate certificate;

        if(certificateX509NameDto.getCertificateRole().equals(CertificateRole.ROOT)){
            certificate = keyStoreReader.readCertificate("./jks/root.jks", "root", certificateX509NameDto.getSerialNumber());
        } else if (certificateX509NameDto.getCertificateRole().equals(CertificateRole.INTERMEDIATE)) {
            certificate = keyStoreReader.readCertificate("./jks/intermediate.jks", "intermediate", certificateX509NameDto.getSerialNumber());
        } else if (certificateX509NameDto.getCertificateRole().equals(CertificateRole.ENDENTITY)) {
            certificate = keyStoreReader.readCertificate("./jks/end-entity.jks", "end-entity", certificateX509NameDto.getSerialNumber());
        } else {
            return false;
        }

        X509Certificate x509Certificate = (X509Certificate) certificate;

        FileOutputStream os = new FileOutputStream("./downloads/" + x509Certificate.getSerialNumber() + ".crt");
        os.write("-----BEGIN CERTIFICATE-----\n".getBytes());
        os.write(x509Certificate.toString().getBytes());
        os.write("\n-----END CERTIFICATE-----\n".getBytes());
        os.close();

        return true;
    }


    @Override
    public String generateSerialNumber() {

        Random rand = new Random();
        int serialNumber = rand.nextInt(1000000);
        String stringSerialNumber = String.valueOf(serialNumber);

        while(certificateRepository.findBySerialNumber(stringSerialNumber) != null) {
            serialNumber = rand.nextInt(1000000);
            stringSerialNumber = String.valueOf(serialNumber);
        }

        return  stringSerialNumber;
    }


    @Override
    public void checkEndDate(Certificate certificate) {
        LocalDate today = LocalDate.now();

        if(certificate.getValidTo().compareTo(today) > 0 || certificate.getValidTo().compareTo(today) == 0){
            CertificateX509NameDto certificateX509NameDto = modelMapper.map(certificate, CertificateX509NameDto.class);
            revoke(certificateX509NameDto);
        }
    }



}
