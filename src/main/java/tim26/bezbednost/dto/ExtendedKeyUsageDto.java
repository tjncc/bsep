package tim26.bezbednost.dto;

import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;

import java.util.ArrayList;
import java.util.List;

public class ExtendedKeyUsageDto {

    private String serverAuth;
    private String clientAuth;
    private String codeSigning;
    private String emailProtection;
    private String timeStamping;

    public ExtendedKeyUsageDto(String serverAuth, String clientAuth, String codeSigning, String emailProtection, String timeStamping) {
        this.serverAuth = serverAuth;
        this.clientAuth = clientAuth;
        this.codeSigning = codeSigning;
        this.emailProtection = emailProtection;
        this.timeStamping = timeStamping;
    }

    public String getServerAuth() {
        return serverAuth;
    }

    public void setServerAuth(String serverAuth) {
        this.serverAuth = serverAuth;
    }

    public String getClientAuth() {
        return clientAuth;
    }

    public void setClientAuth(String clientAuth) {
        this.clientAuth = clientAuth;
    }

    public String getCodeSigning() {
        return codeSigning;
    }

    public void setCodeSigning(String codeSigning) {
        this.codeSigning = codeSigning;
    }

    public String getEmailProtection() {
        return emailProtection;
    }

    public void setEmailProtection(String emailProtection) {
        this.emailProtection = emailProtection;
    }

    public String getTimeStamping() {
        return timeStamping;
    }

    public void setTimeStamping(String timeStamping) {
        this.timeStamping = timeStamping;
    }

    public KeyPurposeId[] getKeyUsages(){

        List<KeyPurposeId> returnlist = new ArrayList<>();

        if(this.clientAuth.equals("on")){

            returnlist.add(KeyPurposeId.id_kp_clientAuth);
        }

        if(this.serverAuth.equals("on")){
            returnlist.add(KeyPurposeId.id_kp_serverAuth);
        }

        if(this.codeSigning.equals("on")){
            returnlist.add(KeyPurposeId.id_kp_codeSigning);
        }

        if(this.emailProtection.equals("on")){
            returnlist.add(KeyPurposeId.id_kp_emailProtection);
        }

        if(this.timeStamping.equals("on")){
            returnlist.add(KeyPurposeId.id_kp_timeStamping);
        }

        KeyPurposeId[] ret = new KeyPurposeId[returnlist.size()];
        returnlist.toArray(ret);
        return ret;

    }
}
