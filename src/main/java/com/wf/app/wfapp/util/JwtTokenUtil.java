package com.wf.app.wfapp.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wf.app.wfapp.common.Constants;
import com.wf.app.wfapp.dto.vo.user.JWTInfo;
import com.wf.app.wfapp.dto.vo.user.LoginResultCacheVO;
import com.wf.app.wfapp.service.common.RedisService;
import com.wf.common.constants.AuthenticationKey;
import com.wf.common.constants.CommonConstants;
import com.wf.common.constants.ResultCode;
import com.wf.common.exception.WFException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
@Slf4j
public class JwtTokenUtil {

    @Value("${jwt.expire}")
    private int jwtTokenExpire;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private RedisService redisService;

    public String generateToken(JWTInfo jwtInfo) {
        return generateToken(jwtInfo, secret, jwtTokenExpire);
    }


    /**
     * 密钥加密token
     *
     * @param jwtInfo
     * @param encryptSecret
     * @param expire        单位分钟
     * @return
     * @
     */
    public String generateToken(JWTInfo jwtInfo, String encryptSecret, int expire) {
        Map<String, Object> data = new HashMap();
        data.put(Constants.JWT.JWT_KEY_USER_ID, jwtInfo.getUserId());
        data.put(Constants.JWT.JWT_KEY_ACCOUNT,jwtInfo.getAccount());
        data.put(Constants.JWT.JWT_KEY_LOGIN_TIME, jwtInfo.getLoginTime());
        Claims claims = Jwts.claims(data);
        return Jwts.builder().setClaims(claims)
                .setExpiration(getExpireDate(expire))
                .signWith(SignatureAlgorithm.HS512, encryptSecret)
                .compact();
    }

    /**
     * 公钥解析token
     *
     * @param token
     * @return
     * @
     */
    public Jws<Claims> parserToken(String token, String encryptSecret) {
        return Jwts.parser().setSigningKey(encryptSecret).parseClaimsJws(token);
    }

    public JWTInfo getInfoFromToken(String token) {
        return getInfoFromToken(token, secret);
    }

    /**
     * 获取token中的用户信息
     *
     * @param token
     * @param encryptSecret
     * @return
     * @
     */
    public JWTInfo getInfoFromToken(String token, String encryptSecret) {
        log.info("login token:[{}]",token);
        Jws<Claims> claimsJws = parserToken(token, encryptSecret);
        Claims body = claimsJws.getBody();
        JWTInfo jwtInfo = new JWTInfo(com.wf.common.utils.StringUtils.getObjectValue(body.get(Constants.JWT.JWT_KEY_USER_ID)),
                com.wf.common.utils.StringUtils.getObjectValue(body.get(Constants.JWT.JWT_KEY_ACCOUNT)),
                com.wf.common.utils.StringUtils.getObjectValue(body.get(Constants.JWT.JWT_KEY_LOGIN_TIME)));
        log.info("login token parser result,enterpriseId: [{}],userId:[{}],loginTime: [{}]", jwtInfo.getUserId(), DateFormatUtils.format(Long.parseLong(jwtInfo.getLoginTime()), "yyyy-MM-dd HH:mm:ss"));
        return jwtInfo;
    }

    private Date getExpireDate(int seconds) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR_OF_DAY, seconds);
        return cal.getTime();
    }

    public int getJwtTokenExpire() {
        return jwtTokenExpire;
    }

    public String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(AuthenticationKey.AUTHENTICATION_KEY);
        if (StringUtils.isEmpty(token)) {
            Cookie[] cookies = request.getCookies();
            if (null != cookies && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if (AuthenticationKey.AUTHENTICATION_KEY.equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return token;
    }

    /**
     * 根据token从redis中获取登录信息
     *
     * @return
     */
    public LoginResultCacheVO getLoginUserFromToken() {
        String token = getToken();
        if (StringUtils.isEmpty(token)) {
            throw new WFException(ResultCode.LOGIN_TOKEN_IS_NULL.getMessage(),ResultCode.LOGIN_TOKEN_IS_NULL.getCode());
        }
        getInfoFromToken(token, secret);
        String json = (String) redisService.get(token);
        if (StringUtils.isEmpty(json)) {
            throw new WFException(ResultCode.LOGIN_TOKEN_EXPIRE.getMessage(),ResultCode.LOGIN_TOKEN_EXPIRE.getCode());
        }
        return JSON.parseObject(json, new TypeReference<LoginResultCacheVO>() {});
    }

}
