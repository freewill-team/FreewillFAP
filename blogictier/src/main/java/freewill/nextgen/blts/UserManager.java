package freewill.nextgen.blts;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.common.Messages;
import freewill.nextgen.common.entities.EmailEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   UserManager.java
 * Date:   19/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Users
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/UserEntity")
public class UserManager {
	
	@Autowired
	UserRepository repository;

	@RequestMapping("/create")
	public UserEntity add(@RequestBody UserEntity rec) throws Exception {
		if(rec!=null){
			UserEntity old = repository.findByLoginname(rec.getLoginname());
			if(old!=null)
				throw new IllegalArgumentException("LoginName already exists");
			
			// Creates a new (random) password for this user and send it by email
        	Random rnd = new Random();
        	String newpassword = "Ax8";
        	for(int i = 0; i<7; i++){
        		newpassword += Character.toString((char) (48+rnd.nextInt(65)));
        	}
        	// Digest the new password
        	String digestedPassword = getMD5Digest(newpassword);
            rec.setPassword(digestedPassword);
			
        	// Injects the new record
        	System.out.println("Saving User..."+rec.toString());
        	rec.setID(repository.getMaxId()+1);
        	rec.setTimestamp(new Date());
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = repository.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
        	
			UserEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			
			// Send notification by email
			String title = Messages.get().getKey("accountcreated", System.getProperty("LOCALE"));
			String message = String.format(
					Messages.get().getKey("accountcreatedmsg", System.getProperty("LOCALE")),
					rec.getLoginname(), newpassword);
			if(!rec.getEmail().equals(""))
				RtdbDataService.get().pushEmail(new EmailEntity(
					rec.getEmail(),
					title, message,
					/*"FreeWill new account created",
					"According to your administrator request, your user and password have been generated.\n"+
		            		"Your Login name is:   "+rec.getLoginname()+"\n"+	
		            		"And your password is: "+newpassword+"\n\n"+
		            		//"Please login ProposalStudio at https://137.74.195.144/freewill/"+"\n\n"+
		            		"Best regards.\n",*/
		    		0L // user.getCompany() Para que el correo salga aunque esta Company no tenga configurado ningun servidor
		    		));
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public UserEntity update(@RequestBody UserEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating User..."+rec);
			// TODO no debe permitir cambiar el nombre del usuario
			rec.setTimestamp(new Date());
			UserEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting User..."+recId);
			UserEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<UserEntity> getlist() throws Exception {
		System.out.println("Getting Entire User List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = repository.findByLoginname(auth.getName());
		if(user.getRole()==UserRoleEnum.SUPER)
			return (List<UserEntity>) repository.findAll();
		else
			return (List<UserEntity>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/get/{recId}")
	public UserEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving User..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/login/{login}")
	public UserEntity login(@PathVariable String login) throws Exception {
		if( login==null )
			return null;
		System.out.println("Loging User..."+login);
		return repository.findByLoginnameAndActive(login, true);
	}
	
	@RequestMapping("/resetPassword/{login}")
	public boolean resetpassword(@PathVariable String login) throws Exception {
		System.out.println("Entering resetPassword for..."+login);
		
		UserEntity rec = repository.findByLoginname(login);
		if(rec==null)
			throw new IllegalArgumentException("LoginName does not exists");
		if(rec.getEmail().equals(""))
			throw new IllegalArgumentException("No email address configured for you");
		
		// Creates a new (random) password for this user and send it by email
    	Random rnd = new Random();
    	String newpassword = "Ax9";
    	for(int i = 0; i<7; i++){
    		newpassword += Character.toString((char) (48+rnd.nextInt(64)));
    	}
    	// Digest the new password
    	String digestedpassword = getMD5Digest(newpassword);
    	
    	System.out.println("Updating User..."+rec);
    	rec.setPassword(digestedpassword);
		rec.setTimestamp(new Date());
		UserEntity res = repository.save(rec);
		System.out.println("Id = "+res.getID());
		
    	// Finally, sends the email to communicate the new password
		String title = Messages.get().getKey("accountnewpassword", System.getProperty("LOCALE"));
		String message = String.format(
				Messages.get().getKey("accountnewpasswordmsg", System.getProperty("LOCALE")),
				rec.getName(), newpassword);
		if(!rec.getEmail().equals(""))
	    	RtdbDataService.get().pushEmail(new EmailEntity(
	    		rec.getEmail(),
	    		title, message,
	    		/*"FreeWill change password request",
	    		"Dear "+rec.getName() +",\n\n"+
	    		"According to your request, a new password has been generated.\n"+
	    		"The new password is: "+newpassword+"\n\n"+
	    		"Best regards.\n",*/
	    		0L // rec.getCompany() Para que el correo salga aunque esta Company no tenga configurado ningun servidor
	    		));
			
		return true;
	}
	
	private String getMD5Digest(String str) { 
		try { 
			byte[] buffer = str.getBytes(); 
			byte[] result = null; 
			StringBuffer buf = null; 
			MessageDigest md5 = MessageDigest.getInstance("MD5"); 
			// allocate room for the hash 
			result = new byte[md5.getDigestLength()]; 
			// calculate hash 
			md5.reset(); 
			md5.update(buffer); 
			result = md5.digest(); 
			// System.out.println(result); 
			// create hex string from the 16-byte hash 
			buf = new StringBuffer(result.length * 2); 
			for (int i = 0; i < result.length; i++) { 
				int intVal = result[i] & 0xff; 
				if (intVal < 0x10) { 
					buf.append("0"); 
				} 
				buf.append(Integer.toHexString(intVal).toUpperCase()); 
			} 
			return buf.toString();
		} 
		catch (NoSuchAlgorithmException e) { 
			System.err.println("Exception caught: " + e); 
			e.printStackTrace();
		} 
		return null;
	}
	
	@RequestMapping("/countActiveUsers")
	public UserEntity countActiveUsers() throws Exception {
		System.out.println("Getting countActiveUsers...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = repository.findByLoginname(auth.getName());
		UserEntity rec = new UserEntity();
		rec.setID(repository.countByCompanyAndActive(user.getCompany(), true));
		return rec;
	}
	
	@RequestMapping("/countNoActiveUsers")
	public UserEntity countNoActiveUsers() throws Exception {
		System.out.println("Getting countNoActiveUsers...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = repository.findByLoginname(auth.getName());
		UserEntity rec = new UserEntity();
		rec.setID(repository.countByCompanyAndActive(user.getCompany(), false));
		return rec;
	}
	
}