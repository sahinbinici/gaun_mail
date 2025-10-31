package gaun.apply.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                       HttpServletResponse response,
                                       AuthenticationException exception) throws IOException, ServletException {
        
        if (exception instanceof SmsVerificationRequiredException) {
            // SMS doğrulaması gerekiyor
            SmsVerificationRequiredException smsException = (SmsVerificationRequiredException) exception;
            
            // Session'a bilgileri kaydet
            HttpSession session = request.getSession();
            session.setAttribute("studentDto", smsException.getStudentDto());
            session.setAttribute("verificationCode", smsException.getVerificationCode());
            session.setAttribute("fromLogin", true); // Login'den geldiğini belirt
            
            // SMS doğrulama sayfasına yönlendir
            response.sendRedirect(request.getContextPath() + "/login/verify-sms");
        } else {
            // Diğer authentication hataları için login sayfasına yönlendir
            response.sendRedirect(request.getContextPath() + "/login?error=true");
        }
    }
}
