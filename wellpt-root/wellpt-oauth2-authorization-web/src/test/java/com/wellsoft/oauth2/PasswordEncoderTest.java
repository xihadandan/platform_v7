package com.wellsoft.oauth2;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/10    chenq		2019/9/10		Create
 * </pre>
 */
public class PasswordEncoderTest {

    @Test
    public void testEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("0"));
    }

    @Test
    public void testD() {
        System.out.println(DigestUtils.sha256Hex(UUID.randomUUID().toString()));
    }
}
