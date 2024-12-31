package gaun.apply.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.User;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ConvertUtil {

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

}
