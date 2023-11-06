package com.encryption.com.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.encryption.com.Repo.CustomerRepo;
import com.encryption.com.pojo.Customer;

import reactor.core.publisher.Mono;

@Service
public class CustomerHandler {

	@Autowired
	private CustomerRepo customerRepo;

	// private key for both encryption and decryption
	private static final String secretKey = "eswar123eswar123";

	// save customer
	public Mono<ServerResponse> saveCustomerEncryption(ServerRequest serverRequest) {
		Mono<Customer> monoCustomer = serverRequest.bodyToMono(Customer.class);
		return monoCustomer.flatMap(customer -> {
			try {
				encryptCustomerDetails(customer);
				return ServerResponse.ok().body(customerRepo.save(customer), Customer.class); // Call the encryption
																								// method before saving
			} catch (Exception e) {
				return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

		}).switchIfEmpty(ServerResponse.notFound().build());
	}

	// get customer details by using customer id
	public Mono<ServerResponse> getCustomerDecryption(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		Mono<Customer> customerMono = customerRepo.findById(id);
		return customerMono.flatMap(customer -> {
			try {
				decryptCustomerDetails(customer);
				return ServerResponse.ok().bodyValue(customer);
			} catch (Exception e) {
				return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}).switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
				.bodyValue("User with the provided ID : " + id + " does not exist"));
	}

	// customer details Encryption using AES Algo
	private void encryptCustomerDetails(Customer customer) throws Exception {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);

			String encryptedName = Base64.getEncoder()
					.encodeToString(cipher.doFinal(customer.getCustomerName().getBytes(StandardCharsets.UTF_8)));
			String encryptedAge = Base64.getEncoder()
					.encodeToString(cipher.doFinal(customer.getAge().getBytes(StandardCharsets.UTF_8)));

			customer.setCustomerName(encryptedName);
			customer.setAge(encryptedAge);

		} catch (Exception e) {
			System.err.println("Error occurred during encryption: " + e.getMessage());
			throw new Exception("Error occurred during encryption", e);
		}
	}

	private void decryptCustomerDetails(Customer customer) throws Exception {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);

			String decryptedName = new String(cipher.doFinal(Base64.getDecoder().decode(customer.getCustomerName())),
					StandardCharsets.UTF_8);
			String decryptedAge = new String(cipher.doFinal(Base64.getDecoder().decode(customer.getAge())),
					StandardCharsets.UTF_8);

			customer.setCustomerName(decryptedName);
			customer.setAge(decryptedAge);

		} catch (Exception e) {
			System.err.println("Private Key Miss Match! Check Once ");
			throw new Exception("Error occurred during decryption", e);
		}
	}
}
