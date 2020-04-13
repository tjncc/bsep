package tim26.bezbednost.dto;

import org.bouncycastle.asn1.x509.KeyUsage;

import java.util.ArrayList;
import java.util.List;


public class KeyUsageDto {

    private String digitalSignarute;
    private String keyEncipherment;
    private String dataEnicipherment;
    private String keyAgreement;
    private String keyCertSign;
    private String crlSign;
    private String nonRepudiation;
    private String encipherOnly;
    private String decipherOnly;
    private String isCriticalKeyUsage;

    public KeyUsageDto(String digitalSignarute, String keyEncipherment, String dataEnicipherment, String keyAgreement, String keyCertSign, String crlSign, String nonRepudiation, String encipherOnly, String decipherOnly) {
        this.digitalSignarute = digitalSignarute;
        this.keyEncipherment = keyEncipherment;
        this.dataEnicipherment = dataEnicipherment;
        this.keyAgreement = keyAgreement;
        this.keyCertSign = keyCertSign;
        this.crlSign = crlSign;
        this.nonRepudiation = nonRepudiation;
        this.encipherOnly = encipherOnly;
        this.decipherOnly = decipherOnly;
    }

    public KeyUsageDto(String digitalSignarute, String keyEncipherment, String dataEnicipherment, String keyAgreement, String keyCertSign, String crlSign, String nonRepudiation, String encipherOnly, String decipherOnly, String isCriticalKeyUsage) {
        this.digitalSignarute = digitalSignarute;
        this.keyEncipherment = keyEncipherment;
        this.dataEnicipherment = dataEnicipherment;
        this.keyAgreement = keyAgreement;
        this.keyCertSign = keyCertSign;
        this.crlSign = crlSign;
        this.nonRepudiation = nonRepudiation;
        this.encipherOnly = encipherOnly;
        this.decipherOnly = decipherOnly;
        this.isCriticalKeyUsage = isCriticalKeyUsage;
    }

    public KeyUsageDto(){

    }

    public String getIsCriticalKeyUsage() {
        return this.isCriticalKeyUsage;
    }

    public void setIsCriticalKeyUsage(String isCriticalKeyUsage) {
        this.isCriticalKeyUsage = isCriticalKeyUsage;
    }

    public String getDigitalSignarute() {
        return digitalSignarute;
    }

    public void setDigitalSignarute(String digitalSignarute) {
        this.digitalSignarute = digitalSignarute;
    }

    public String getKeyEncipherment() {
        return keyEncipherment;
    }

    public void setKeyEncipherment(String keyEncipherment) {
        this.keyEncipherment = keyEncipherment;
    }

    public String getDataEnicipherment() {
        return dataEnicipherment;
    }

    public void setDataEnicipherment(String dataEnicipherment) {
        this.dataEnicipherment = dataEnicipherment;
    }

    public String getKeyAgreement() {
        return keyAgreement;
    }

    public void setKeyAgreement(String keyAgreement) {
        this.keyAgreement = keyAgreement;
    }

    public String getKeyCertSign() {
        return keyCertSign;
    }

    public void setKeyCertSign(String keyCertSign) {
        this.keyCertSign = keyCertSign;
    }

    public String getCrlSign() {
        return crlSign;
    }

    public void setCrlSign(String crlSign) {
        this.crlSign = crlSign;
    }

    public String getNonRepudiation() {
        return nonRepudiation;
    }

    public void setNonRepudiation(String nonRepudiation) {
        this.nonRepudiation = nonRepudiation;
    }

    public String getEncipherOnly() {
        return encipherOnly;
    }

    public void setEncipherOnly(String encipherOnly) {
        this.encipherOnly = encipherOnly;
    }

    public String getDecipherOnly() {
        return decipherOnly;
    }

    public void setDecipherOnly(String decipherOnly) {
        this.decipherOnly = decipherOnly;
    }

    public List<Integer> getAllNotNullValues(){

        List<Integer> returnlist = new ArrayList<>();


        if(this.digitalSignarute.equals("on")){

            returnlist.add(KeyUsage.digitalSignature);
        }

        if(this.dataEnicipherment.equals("on")){
            returnlist.add(KeyUsage.dataEncipherment);
        }

        if(this.keyEncipherment.equals("on")){
            returnlist.add(KeyUsage.keyEncipherment);
        }

        if(this.keyAgreement.equals("on")){
            returnlist.add(KeyUsage.keyAgreement);
        }

        if(this.keyCertSign.equals("on")){
            returnlist.add(KeyUsage.keyCertSign);
        }

        if(this.crlSign.equals("on")){
            returnlist.add(KeyUsage.cRLSign);
        }

        if(this.nonRepudiation.equals("on")){
            returnlist.add(KeyUsage.nonRepudiation);
        }

        if(this.encipherOnly.equals("on")){
            returnlist.add(KeyUsage.encipherOnly);
        }

        if(this.decipherOnly.equals("on")){
            returnlist.add(KeyUsage.decipherOnly);
        }

        return  returnlist;

    }




}
