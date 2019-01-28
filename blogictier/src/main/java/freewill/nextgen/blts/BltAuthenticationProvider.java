package freewill.nextgen.blts;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.entities.UserEntity;

@Component
public class BltAuthenticationProvider implements AuthenticationProvider {
	
	@Autowired
	UserRepository repository;

	//@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		// After OAuth2Config has granted access to this class, now we will check
		// whether the provided user and password (encrypted) are valid
		// against the User table
		
		String username = auth.getName();
		String password = auth.getCredentials().toString();
		
		System.out.println("Checking User="+username);
		//System.out.println("Password="+password);
		
		UserEntity user = repository.findByLoginname(username);
		
		//if("foo".equals(username) && "foo".equals(password)){
		if(user!=null){
			System.out.println("The user exists, now checks password ");
			//System.out.println("User password="+user.getPassword());
			// the user exists, now checks password
			if(user.getPassword().equals(password)){
				System.out.println("Password Ok");
				return new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());
			}
			System.out.println("Invalid password");
		} 
		
		// the user or password is not valid
		System.out.println("The user does not exist");
		throw new BadCredentialsException("User authentication failed");
	}
		
	@Override
	public boolean supports(Class<?> auth) {
		return auth.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}
