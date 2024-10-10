package lunatech.application.server;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;
import lunatech.domain.auth.AuthServicePort;
import lunatech.domain.auth.models.AuthModel;

import java.util.Optional;

/**
 * This class is responsible for the authentication logic of the application
 * Note: It compares a given username + password specified in the HTTP query to
 * the credentials registered in the mongodb user collection
 */
@ApplicationScoped
@JBossLog
public class HttpServerIdentityProvider implements io.quarkus.security.identity.IdentityProvider<UsernamePasswordAuthenticationRequest> {
    @Inject
    AuthServicePort authPortServicePort;

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(
            UsernamePasswordAuthenticationRequest request,
            AuthenticationRequestContext authenticationRequestContext
    ) {
        String requestName = request.getUsername();
        String requestPwd = String.valueOf(request.getPassword().getPassword());
        Optional<AuthModel> maybeAuthModel = authPortServicePort.login(requestName, requestPwd);

        return maybeAuthModel.map(authModel -> {
            SecurityIdentity si = QuarkusSecurityIdentity.builder()
                    .setPrincipal(new QuarkusPrincipal(authModel.username()))
                    .addRole(authModel.role().toString())
                    .addCredential(request.getPassword())
                    .setAnonymous(false)
                    .build();
            return Uni.createFrom().item(si);
        }).orElseThrow(() -> {
            log.warnf("Invalid auth for username=[%s] and password=[%s]", requestName, requestPwd);
            return new AuthenticationFailedException("password invalid or user not found");
        });
    }
}