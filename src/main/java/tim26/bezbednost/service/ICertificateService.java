package tim26.bezbednost.service;

import tim26.bezbednost.dto.CertificateDto;

import java.util.List;

public interface ICertificateService {

    List<CertificateDto> findAll();

}
