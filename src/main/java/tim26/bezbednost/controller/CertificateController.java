package tim26.bezbednost.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tim26.bezbednost.dto.CertificateDto;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.service.ICertificateService;

import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
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


}
