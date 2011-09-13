package org.mule.galaxy.security.ldap;

import java.util.Collection;

import org.mule.galaxy.impl.jcr.UserDetailsWrapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * {@link Authentication} wrapper using {@link UserDetailsWrapper} as a {@link #getPrincipal()}.
 */
public final class AuthenticationWrapper implements Authentication {

	private static final long serialVersionUID = 1L;

	private final Authentication delegate;
	private final UserDetailsWrapper userDetails;

	public AuthenticationWrapper(final Authentication delegate, final UserDetailsWrapper userDetails) {
		this.delegate = delegate;
		this.userDetails = userDetails;
	}

	public String getName() {
		return this.delegate.getName();
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.userDetails.getAuthorities();
	}

	public Object getCredentials() {
		return this.delegate.getCredentials();
	}

	public Object getDetails() {
		return this.delegate.getDetails();
	}

	public Object getPrincipal() {
		return this.userDetails;
	}

	public boolean isAuthenticated() {
		return this.delegate.isAuthenticated();
	}

	public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
		this.delegate.setAuthenticated(isAuthenticated);
	}

}