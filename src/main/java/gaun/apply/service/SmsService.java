package gaun.apply.service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SmsService {

    public void sendSms(String[] toPhoneNumber, String message) {

        try {
            URL url = new URL("https://api.vatansms.net/api/v1/otp");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = getString(toPhoneNumber,message);

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            System.out.println("Response Code: " + conn.getResponseCode());
            System.out.println("Response Message: " + conn.getResponseMessage());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static String getString(String[] toPhoneNumber,String message) throws JsonProcessingException {
        Map<String, Object> params = new HashMap<>();
        params.put("api_id", "29d463733f56db81be9eb355");
        params.put("api_key", "79259ea325e14e8603ab7cf7");
        params.put("sender", "G.ANTEP UNI");
        params.put("message_type", "normal");
        params.put("message", message);
        params.put("phones",toPhoneNumber );
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(params);
    }
} 
