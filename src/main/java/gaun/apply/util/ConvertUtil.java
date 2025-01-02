package gaun.apply.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
@Component
public class ConvertUtil {

    private static final RestTemplate restTemplate = new RestTemplate();
    private PasswordEncoder passwordEncoder;
    private static String baseUrl="https://www.gantep.edu.tr/mobil/usercheck_o_json.php/";

    public static UserDto convertEntityToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setAd(user.getName());
        userDto.setSoyad(user.getLastname());
        userDto.setTcKimlikNo(user.getIdentityNumber());
        return userDto;
    }

    public static StudentDto convertJsonToStudentDto(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            StudentDto studentDto = mapper.readValue(jsonResponse, StudentDto.class);
            return studentDto;
        } catch (Exception e) {
            throw new RuntimeException("JSON dönüşüm hatası: " + e.getMessage());
        }
    }

    public static String convertPasswordToMD5(String param) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest  = md.digest(param.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String password = no.toString(16).toUpperCase();
        return password;
    }

    public static String convertMD5ToPassword(String param) {
        return param;
    }

    public static StudentDto getStudentFromObs(StudentDto studentDto) throws NoSuchAlgorithmException {
        String pass = ConvertUtil.convertPasswordToMD5(studentDto.getPassword());
        String url = baseUrl + "?check=gaun_mobil&u=" + studentDto.getOgrenciNo() + "&p=" + pass;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            StudentDto studentFromService = ConvertUtil.convertJsonToStudentDto(response.getBody());
            return studentFromService;
        }else {
            return null;
        }
    }

}
