package lunatech.security;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;
import lombok.extern.jbosslog.JBossLog;
import lunatech.Startup;
import lunatech.entities.UserEntity;
import lunatech.requests.UserCredentialsRequest;
import org.jboss.logging.Logger;

import java.util.Optional;

/**
 * This class is responsible for the authentication logic of the application
 * Note: It compares a given username + password specified in the HTTP query to
 * the credentials registered in the mongodb user collection
 */
@ApplicationScoped
@JBossLog
public class MongoDbIdentityProvider implements IdentityProvider<UsernamePasswordAuthenticationRequest> {

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(
            UsernamePasswordAuthenticationRequest request,
            AuthenticationRequestContext authenticationRequestContext
    ) {

        // Extract request credentials
        var requestName = request.getUsername();
        var requestPwd = String.valueOf(request.getPassword().getPassword());

        // Fetch mongodb user
        Optional<UserCredentialsRequest> maybeUser = UserEntity.find("username", requestName)
                .project(UserCredentialsRequest.class)
                .firstResultOptional();


        // Throw an error if invalid credentials
        UserCredentialsRequest user = maybeUser
                .filter(u -> requestPwd.equals(u.password()))
                .orElseThrow( () -> {
                    log.warnf("Invalid auth for username=[%s] and password=[%s]", requestName, requestPwd);
                    return new AuthenticationFailedException("password invalid or user not found");
                });

        QuarkusSecurityIdentity identity = QuarkusSecurityIdentity.builder()
                .setPrincipal(new QuarkusPrincipal(user.username()))
                .addRole(user.role())
                .addCredential(request.getPassword())
                .setAnonymous(false)
                .build();

        return Uni.createFrom().item(identity);
    }
}