package gaun.apply.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import gaun.apply.dto.StudentDto;
import gaun.apply.dto.UserDto;
import gaun.apply.entity.User;

public class ConvertUtil {

    public static UserDto convertEntityToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getName());
        userDto.setLastName(user.getLastname());
        userDto.setIdentityNumber(user.getIdentityNumber());
        return userDto;
    }

    public static StudentDto convertJsonToStudentDto(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse, StudentDto.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON dönüşüm hatası: " + e.getMessage());
        }
    }

}
