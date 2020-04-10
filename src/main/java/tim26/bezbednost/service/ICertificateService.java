package tim26.bezbednost.service;

import tim26.bezbednost.dto.CertificateDto;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.model.certificates.IssuerData;
import tim26.bezbednost.model.certificates.SubjectData;
import tim26.bezbednost.model.enumeration.CertificateRole;

import javax.management.relation.Role;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.List;

public interface ICertificateService {

    public List<CertificateDto> findAll();

    public SubjectData generateSubjectData(CertificateX509NameDto certificateDto);

    public IssuerData generateIssuerData(CertificateX509NameDto certificateDto, PrivateKey privateKey);

    public List<CertificateX509NameDto> getAllCACertificates() throws FileNotFoundException;

    public boolean save(CertificateX509NameDto certificateX509NameDto) throws CertificateException, ParseException, NoSuchAlgorithmException, FileNotFoundException, SignatureException, NoSuchProviderException, InvalidKeyException;

    public String generateSerialNumber();

    public void generateCACertificate(CertificateX509NameDto certificateX509NameDto, String serialNumber, boolean isFristTime) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ParseException;

    public void generateCertificateNotCA(CertificateX509NameDto certificateX509NameDto, String serialNumber, boolean isFirstTime) throws FileNotFoundException;

    public void generateSelfSignedCertificate(CertificateX509NameDto certificateX509NameDto,boolean isFristTime) throws NoSuchProviderException, CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, ParseException;

    public boolean downloadCertificate(CertificateX509NameDto certificateX509NameDto) throws IOException, CertificateEncodingException;

}
