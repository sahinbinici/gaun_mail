package gaun.apply.security;

import gaun.apply.application.dto.StudentDto;
import org.springframework.security.core.AuthenticationException;

public class SmsVerificationRequiredException extends AuthenticationException {
    private final StudentDto studentDto;
    private final String verificationCode;

    public SmsVerificationRequiredException(String message, StudentDto studentDto, String verificationCode) {
        super(message);
        this.studentDto = studentDto;
        this.verificationCode = verificationCode;
    }

    public StudentDto getStudentDto() {
        return studentDto;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
