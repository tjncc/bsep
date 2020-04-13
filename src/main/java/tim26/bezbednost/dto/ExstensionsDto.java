package tim26.bezbednost.dto;

public class ExstensionsDto {

    private KeyUsageDto keyUsageDto;
    private ExtendedKeyUsageDto extendedKeyUsageDto;

    public ExstensionsDto(KeyUsageDto keyUsageDto, ExtendedKeyUsageDto extendedKeyUsageDto) {
        this.keyUsageDto = keyUsageDto;
        this.extendedKeyUsageDto = extendedKeyUsageDto;
    }

    public ExstensionsDto() {
    }


    public KeyUsageDto getKeyUsageDto() {
        return keyUsageDto;
    }

    public void setKeyUsageDto(KeyUsageDto keyUsageDto) {
        this.keyUsageDto = keyUsageDto;
    }

    public ExtendedKeyUsageDto getExtendedKeyUsageDto() {
        return extendedKeyUsageDto;
    }

    public void setExtendedKeyUsageDto(ExtendedKeyUsageDto extendedKeyUsageDto) {
        this.extendedKeyUsageDto = extendedKeyUsageDto;
    }
}
