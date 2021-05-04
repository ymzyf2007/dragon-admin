package com.dragon.modules.security.rest;

import cn.hutool.core.util.IdUtil;
import com.dragon.modules.common.annotation.rest.AnonymousDeleteMapping;
import com.dragon.modules.common.annotation.rest.AnonymousGetMapping;
import com.dragon.modules.common.annotation.rest.AnonymousPostMapping;
import com.dragon.modules.common.config.RsaProperties;
import com.dragon.modules.common.exception.BadRequestException;
import com.dragon.modules.common.utils.RedisUtils;
import com.dragon.modules.common.utils.RsaUtils;
import com.dragon.modules.common.utils.SecurityUtils;
import com.dragon.modules.security.TokenProvider;
import com.dragon.modules.security.config.bean.LoginCodeEnum;
import com.dragon.modules.security.config.bean.LoginProperties;
import com.dragon.modules.security.config.bean.SecurityProperties;
import com.dragon.modules.security.dto.AuthUserDto;
import com.dragon.modules.security.dto.JwtUserDto;
import com.dragon.modules.security.service.OnlineUserService;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "系统: 系统授权接口")
@Slf4j
@RequestMapping("/auth")
@RestController
public class AuthorizationController {

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private LoginProperties loginProperties;
    @Autowired
    private SecurityProperties properties;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private OnlineUserService onlineUserService;

    @ApiOperation("获取验证码")
    @AnonymousGetMapping(value = "/code")
    public ResponseEntity<Object> getCode() {
        // 获取运算的结果
        Captcha captcha = loginProperties.getCaptcha();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeEnum.arithmetic.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        redisUtils.set(uuid, captchaValue, loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return ResponseEntity.ok(imgResult);
    }

    /**
     * 认证过程由三项构成的。在Spring Security中是这样处理这三部分的：
     *
     * 1.username和password被获得后封装到一个UsernamePasswordAuthenticationToken（Authentication接口的实例）的实例中
     *
     * 2.这个token被传递给AuthenticationManager进行验证
     *
     * 3.成功认证后AuthenticationManager将返回一个得到完整填充的Authentication实例
     *
     * 4.通过调用SecurityContextHolder.getContext().setAuthentication(...)，参数传递authentication对象，来建立安全上下文（security context）
     * @param authUser
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation("登录授权")
    @AnonymousPostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
        // 密码解密
        String password = "123456"/*RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword())*/;
        // 查询验证码
        String code = (String) redisUtils.get(authUser.getUuid());
        // 清除验证码
//        redisUtils.del(authUser.getUuid());
//        if (StringUtils.isBlank(code)) {
//            throw new BadRequestException("验证码不存在或已过期");
//        }
//        if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
//            throw new BadRequestException("验证码错误");
//        }
        // 封装用户名密码的基石
        // 通过UsernamePasswordAuthenticationToken实例化了Authentication接口，
        // 继而按照流程，将其传递给AuthenticationMananger调用身份验证核心完成相关工作
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);

        // AuthenticationManagerBuilder用于创建AuthenticationManager
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成令牌
        String token = tokenProvider.createToken(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        // 保存在线信息
        onlineUserService.save(jwtUserDto, token, request);
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
        if (loginProperties.isSingleLogin()) {
            // 踢掉之前已经登录的token
            onlineUserService.checkLoginOnUser(authUser.getUsername(), token);
        }
        return ResponseEntity.ok(authInfo);
    }

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/info")
    public ResponseEntity<Object> getUserInfo() {
        return ResponseEntity.ok(SecurityUtils.getCurrentUser());
    }

    @ApiOperation("退出登录")
    @AnonymousDeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        onlineUserService.logout(tokenProvider.getToken(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

}