package dev.hemraj.jwtauthentication.Service.Utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
public class ImageUtil {
    public static byte[] compressImage(byte[] data){
        Deflater deflator = new Deflater();
        deflator.setLevel(Deflater.BEST_COMPRESSION);
        deflator.setInput(data);
        deflator.finish();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] temp = new byte[4*1024];
        while(!deflator.finished()) {
            int size = deflator.deflate(temp);
            byteArrayOutputStream.write(temp, 0, size);

        }
        try{
            byteArrayOutputStream.close();
        }catch(Exception err){
            log.error("Problem in compressing the image! ",err );
        }
        return byteArrayOutputStream.toByteArray();
    }
    public static byte[] decompressImage(byte[] data){
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] temp = new byte[4*1024];
        try {
            while(!inflater.finished()){
                int count = inflater.inflate(temp);
                byteArrayOutputStream.write(temp,0,count);
            }
        }catch(Exception err){
            log.error("Problem in decompressing the image ",err);
        }
        return byteArrayOutputStream.toByteArray();
    }

}
