package BoardService.myproject.configuration;

import BoardService.myproject.handler.MyAccessDeniedHandler;
import BoardService.myproject.handler.MyAuthenticationEntryPoint;
import BoardService.myproject.handler.MyLoginSuccessHandler;
import BoardService.myproject.handler.MyLogoutSuccessHandler;
import BoardService.myproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
// antMatchers().anonymous() : 로그인하지 않은 유저들만 접근 가능
// antMatchers().authenticated() : 로그인한 유저들만 접근 가능
// antMatchers().hasAnyAuthority() : 설정한 등급의 유저들만 접근 가능
// 인증에 실패한 경우 MyAuthenticationEntryPoint
// 인가에 실패한 경우 MyAccessDeniedHandler
// 로그인에 성공한 경우 MyLoginSuccessHandler
// 로그아웃에 성공한 경우 MyLogoutSuccessHandler
public class SecurityConfig {

    private final UserRepository userRepository;

    // 로그인 하지 않은 유저들만 접근 가능한 URL
    private static final String[] anonymousUserUrl = {"/users/login", "/users/join"};

    // 로그인한 유저들만 접근 가능한 URL
    private static final String[] authenticatedUserUrl = {"/boards/**/**/edit", "/boards/**/**/delete", "/likes/**", "/users/myPage/**", "/users/edit", "/users/delete"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                    .csrf().disable()
                    .cors().and()
                    .authorizeHttpRequests()
                    .requestMatchers(anonymousUserUrl).anonymous()
                    .requestMatchers(authenticatedUserUrl).authenticated()
                    .requestMatchers("/boards/greeting/write").hasAnyAuthority("BRONZE", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/boards/greeting").hasAnyAuthority("BRONZE", "ADMIN")
                    .requestMatchers("/boards/free/write").hasAnyAuthority("SILVER", "GOLD", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/boards/free").hasAnyAuthority("SILVER", "GOLD", "ADMIN")
                    .requestMatchers("/boards/gold/**").hasAnyAuthority("GOLD", "ADMIN")
                    .requestMatchers("/users/admin/**").hasAuthority("ADMIN")
                    .requestMatchers("/comments/**").hasAnyAuthority("BRONZE", "SILVER", "GOLD", "ADMIN")
                    .anyRequest().permitAll()
                    .and()
                    .exceptionHandling()
                    .accessDeniedHandler(new MyAccessDeniedHandler(userRepository)) // 인가 실패
                    .authenticationEntryPoint(new MyAuthenticationEntryPoint())   // 인증 실패
                    .and()
                    // 폼 로그인
                    .formLogin()
                    .loginPage("/users/login")                 // 로그인 페이지
                    .usernameParameter("loginId")       // 로그인에 사용될 id
                    .passwordParameter("password")      // 로그인에 사용될 password
                    .failureUrl("/users/login?fail") // 로그인 실패 시 redirect 될 URL => 실패 메세지 출력
                    .successHandler(new MyLoginSuccessHandler(userRepository))    // 로그인 성공 시 실행 될 Handler
                    .and()
                    // 로그아웃
                    .logout()
                    .logoutUrl("/users/logout")     // 로그아웃 URL
                    .invalidateHttpSession(true).deleteCookies("JSESSIONID")
                    .logoutSuccessHandler(new MyLogoutSuccessHandler())
                    .and()
                    .build();
    }
}
