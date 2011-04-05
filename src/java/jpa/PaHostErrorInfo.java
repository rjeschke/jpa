package jpa;

public class PaHostErrorInfo
{
    private int    hostApiType;
    private long   errorCode;
    private String errorText;
    
    private PaHostErrorInfo()
    {
        // Object creation from JNI
    }
    
    public PaHostApiType getHostApiType()
    {
        return PaHostApiType.fromValue(this.hostApiType);
    }
    
    public long getErrorCode()
    {
        return this.errorCode;
    }
    
    public String getErrorText()
    {
        return this.errorText;
    }
}
