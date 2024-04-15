package am.greenbank.config;

import am.greenbank.exceptions.exceptions.UnauthorizedException;
import am.greenbank.repositories.interfaces.TokenRepository;
import am.greenbank.repositories.interfaces.UserRepository;
import am.greenbank.responses.Response;
import am.greenbank.responses.ResponseStatus;
import am.greenbank.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @RequiredArgsConstructor
    @Getter
    private enum AuthConstants {
        BEARER_PREFIX("Bearer "),
        AUTHORIZATION_HEADER("Authorization"),
        REFRESH_TOKEN_COOKIE_NAME("refreshToken");

        private final String value;
    }


    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final List<String> endpointsForRefreshToken = List.of("/api/auth/logout", "/api/auth/refreshToken");
    private final List<String> withoutAuthEndpoints = List.of(
        "/",
        "/api/auth/**",
//        "/api/auth/register",
//        "/api/auth/login",
//        "/api/auth/verifyNumber",
//        "/api/auth/resetVerifyNumber/**"
//        "/ws/**",
        "/api/currencies",
        "/v1/api-docs/**",
        "/v2/api-docs/**",
        "/v3/api-docs/**",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui/**",
        "/webjars/**",
        "/swagger-ui.html"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("request to endpoint {} endpoint", requestURI);

        Enumeration<String> headerNames = request.getHeaderNames();

        Iterator<String> iterator = headerNames.asIterator();

        System.out.println("{");
        while (iterator.hasNext()) {
            var header = iterator.next();
            var val = request.getHeader(header);
            System.out.printf("%s: %s%n", header, val);
        }
        System.out.println("}");

        try {
            if (matchesWithoutAuthPattern(requestURI)) {
                System.out.println("Point 1");
                filterChain.doFilter(request, response);
                return;
            } else if (endpointsForRefreshToken.contains(requestURI)) {
                System.out.println("Point 2");
                processRefreshToken(request);
            } else if (requestURI.startsWith("/ws")) {
                System.out.println("Point 3");
                processSocketToken(request);
            } else {
                System.out.println("Point 4");
                processAccessToken(request);
            }
            System.out.println("Point 5");
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException e) {
            System.out.println("Point 6");
            errorReturn(response, e);
        }
    }

    private void processSocketToken(HttpServletRequest request) {
        String accessToken = request.getParameter("access_token");
        tokenValidation(request, accessToken);
    }

    private boolean matchesWithoutAuthPattern(String requestURI) {
        return withoutAuthEndpoints.stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    private void errorReturn(HttpServletResponse httpServletResponse, UnauthorizedException exception) throws IOException {
        final String status = ResponseStatus.ERROR.getValue();
        String message = exception.getMessage();
        System.out.println(message);
        Response response = Response
            .builder()
            .status(status)
            .message(message)
            .build();
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(response));
    }

    private void processRefreshToken(HttpServletRequest request) {
        String refreshToken;
        String platform = request.getHeader("X-platform");
        if (platform.equals("web")) {
            refreshToken = getRefreshTokenFromCookie(request);
        } else if (platform.equals("ios")) {
            refreshToken = getTokenFromAuth(request);
        } else {
            throw new UnauthorizedException("Unknown platform");
        }

        final String userEmail;

        try {
            userEmail = jwtService.extractUsername(refreshToken);
        } catch (JwtException jwtException) {
            throw new UnauthorizedException("Refresh token is not valid");
        }

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

        if (tokenRepository.findByToken(refreshToken).isPresent() || jwtService.isTokenValid(refreshToken, userDetails)) {
            authenticateUserWithValidToken(request, userDetails);
        } else {
            throw new UnauthorizedException("Refresh token is not valid");
        }
    }

    private String getTokenFromAuth(HttpServletRequest request) {
        final String authHeader = request.getHeader(AuthConstants.AUTHORIZATION_HEADER.getValue());
        if (authHeader == null || !authHeader.startsWith(AuthConstants.BEARER_PREFIX.getValue())) {
            throw new UnauthorizedException("Unauthorized");
        }
        return authHeader.substring(AuthConstants.BEARER_PREFIX.getValue().length()).trim();
    }

    private static String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                .filter(cookie -> AuthConstants.REFRESH_TOKEN_COOKIE_NAME.getValue().equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh token is not valid"));
        } else {
            throw new UnauthorizedException("Refresh token is not valid");
        }
    }

    private void processAccessToken(HttpServletRequest request) {
        final String jwt = getTokenFromAuth(request);

        tokenValidation(request, jwt);
    }

    private void tokenValidation(HttpServletRequest request, String jwt) {
        final String userEmail;
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (JwtException e) {
            throw new UnauthorizedException("Access token is not valid");
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            Boolean refreshTokenExists = userRepository.findByEmail(userEmail)
                .map(
                    user -> !tokenRepository
                        .findAllValidTokensByUserId(user.getId())
                        .isEmpty()
                ).orElse(false);

            if (refreshTokenExists) {
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    authenticateUserWithValidToken(request, userDetails);
                } else {
                    throw new UnauthorizedException("Access token is not valid");
                }
            } else {
                throw new UnauthorizedException("Refresh token is not valid");
            }
        }
    }

    private void authenticateUserWithValidToken(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
