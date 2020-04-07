package tim26.bezbednost.service;

import tim26.bezbednost.dto.CertificateDto;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.model.certificates.IssuerData;
import tim26.bezbednost.model.certificates.SubjectData;
import tim26.bezbednost.model.enumeration.CertificateRole;

import javax.management.relation.Role;
import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.util.List;

public interface ICertificateService {

    public List<CertificateDto> findAll();
    public CertificateX509NameDto[] getIssuerAndSubjectData(String serialNumber, CertificateRole certificateRole) throws FileNotFoundException;
    public SubjectData generateSubjectData(CertificateX509NameDto certificateDto);
    public IssuerData generateIssuerData(CertificateX509NameDto certificateDto, PrivateKey privateKey);
}
