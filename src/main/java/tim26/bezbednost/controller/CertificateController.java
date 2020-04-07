package tim26.bezbednost.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tim26.bezbednost.dto.CertificateX509NameDto;
import tim26.bezbednost.service.ICertificateService;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping(value = "api/certificates")
public class CertificateController {

    @Autowired
    private ICertificateService certificateService;


    @RequestMapping(method = RequestMethod.GET)
    public void findAll(){

            


    }
}
