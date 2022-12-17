package com.felix.msauth;

import java.util.Base64;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.SecureRandomFactoryBean;
import org.springframework.security.core.token.Token;


@SpringBootTest
class MsauthApplicationTests {

	@Test
	void contextLoads() throws Exception {
		
		KeyBasedPersistenceTokenService service = new KeyBasedPersistenceTokenService();
		service.setServerSecret("SECRET123");
		service.setServerInteger(16);
		service.setSecureRandom(new SecureRandomFactoryBean().getObject());
		
		Token token = service.allocateToken("felixpessoa90@gmail.com");
		
		System.out.println(token.getExtendedInformation());
		System.out.println(new Date(token.getKeyCreationTime()));
		System.out.println(token.getKey());
		
//		MTY3MTE5MzgwNDc0OTowYWNhMTIzZjM4NGFkNTNkMWY4NWY3MDg0ZmU0Nzg1MzY0YWExMjlhODk4NDdhODY5ODAzMThmZDdlNWJmNzE2OmZlbGl4cGVzc29hOTBAZ21haWwuY29tOjkwMmViODdjZDVhNTg5MjBkMzc4ZTY5Y2E2ODc2OTdjYzkyOWE4YjViZTBiOTc1MTgwNjBkNDJkMTMzN2YzYTFlODI2NTgwMzdjM2E0YjgyNmE1MWM2YjE1NzVlMDlhOGU0NzY3NWI3ZjE5NjBiMTMyMWY3YTIwNGQ5NWMxMTkw
//		Fri Dec 16 09:30:04 BRT 2022
		
		
	}
	
	@Test
	void readToken() throws Exception {
		
		KeyBasedPersistenceTokenService service = new KeyBasedPersistenceTokenService();
		service.setServerSecret("SECRET123");
		service.setServerInteger(16);
		service.setSecureRandom(new SecureRandomFactoryBean().getObject());
		
		String rawToken = "MTY3MTE5MzgwNDc0OTowYWNhMTIzZjM4NGFkNTNkMWY4NWY3MDg0ZmU0Nzg1MzY0YWExMjlhODk4NDdhODY5ODAzMThmZDdlNWJmNzE2OmZlbGl4cGVzc29hOTBAZ21haWwuY29tOjkwMmViODdjZDVhNTg5MjBkMzc4ZTY5Y2E2ODc2OTdjYzkyOWE4YjViZTBiOTc1MTgwNjBkNDJkMTMzN2YzYTFlODI2NTgwMzdjM2E0YjgyNmE1MWM2YjE1NzVlMDlhOGU0NzY3NWI3ZjE5NjBiMTMyMWY3YTIwNGQ5NWMxMTkw";
		
		Token token = service.verifyToken(rawToken);
		
		System.out.println(token.getExtendedInformation());
		System.out.println(new Date(token.getKeyCreationTime()));
		System.out.println(token.getKey());
	}
	
	@Test
	public void readPublicTokenInfo() throws Exception {
		
		String rawToken = "MTY3MTE5MzgwNDc0OTowYWNhMTIzZjM4NGFkNTNkMWY4NWY3MDg0ZmU0Nzg1MzY0YWExMjlhODk4NDdhODY5ODAzMThmZDdlNWJmNzE2OmZlbGl4cGVzc29hOTBAZ21haWwuY29tOjkwMmViODdjZDVhNTg5MjBkMzc4ZTY5Y2E2ODc2OTdjYzkyOWE4YjViZTBiOTc1MTgwNjBkNDJkMTMzN2YzYTFlODI2NTgwMzdjM2E0YjgyNmE1MWM2YjE1NzVlMDlhOGU0NzY3NWI3ZjE5NjBiMTMyMWY3YTIwNGQ5NWMxMTkw";
		
		byte[] bytes = Base64.getDecoder().decode(rawToken);
		String rawTokenDecoded = new String(bytes);
		
		System.out.println(rawTokenDecoded);
		
		String[] tokenParts = rawTokenDecoded.split(":");
		
		Long timestamp = Long.parseLong(tokenParts[0]);
		System.out.println(new Date(timestamp));
		System.out.println(tokenParts[2]);
		 
	}

}
