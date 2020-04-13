package tim26.bezbednost.dto;

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
}
