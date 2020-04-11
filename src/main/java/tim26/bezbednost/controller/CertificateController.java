package tim26.bezbednost.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tim26.bezbednost.dto.CertificateDto;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.model.Certificate;
import tim26.bezbednost.model.enumeration.CertificateRole;
import tim26.bezbednost.model.enumeration.CertificateType;
import tim26.bezbednost.service.ICertificateService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(value = "api/certificates")
public class CertificateController {

    @Autowired
    private ICertificateService certificateService;

    @RequestMapping(method = RequestMethod.GET, value = "/CA")
    public ResponseEntity<List<CertificateX509NameDto>> getAllCACertificates() throws FileNotFoundException {

        List<CertificateX509NameDto> allCA = certificateService.getAllCACertificates();
        return new ResponseEntity<>(allCA, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/save")
    public ResponseEntity<CertificateX509NameDto> create(@RequestBody CertificateX509NameDto certificateX509NameDto) throws CertificateException, ParseException, NoSuchAlgorithmException, FileNotFoundException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(certificateService.save(certificateX509NameDto))
            return new ResponseEntity<>(certificateX509NameDto, HttpStatus.OK);
        else
            return new ResponseEntity<>(certificateX509NameDto, HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(method = RequestMethod.GET, value="/all")
    public ResponseEntity<List<CertificateDto>> getAll() {
        return new ResponseEntity<>(certificateService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value="/checkroot")
    public ResponseEntity<?> getRoots() {
        List<Certificate> certificates = certificateService.getAllRoots();
        if(certificates.size() != 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        else {
            return new ResponseEntity<>(HttpStatus.OK);
          }
        }

    @RequestMapping(method = RequestMethod.POST, value="/download")
    public ResponseEntity download(@RequestBody CertificateX509NameDto certificateX509NameDto) throws IOException, CertificateEncodingException {
        boolean isTrue = certificateService.downloadCertificate(certificateX509NameDto);

        if(isTrue) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value="/revoke")
    public ResponseEntity<CertificateX509NameDto> revoke(@RequestBody CertificateX509NameDto certificateX509NameDto) {
        return new ResponseEntity<>(certificateX509NameDto, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value="/saveroot")
    public ResponseEntity<?> saveRoot(@RequestBody CertificateX509NameDto certificateX509NameDto) throws CertificateException, ParseException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException {
        certificateX509NameDto.setSubjectType(CertificateType.CA);
        certificateX509NameDto.setCertificateRole(CertificateRole.ROOT);
        certificateService.generateSelfSignedCertificate(certificateX509NameDto,false);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
