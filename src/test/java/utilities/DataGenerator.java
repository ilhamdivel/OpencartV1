package utilities;

import java.util.Locale;

import com.github.javafaker.Faker;

public class DataGenerator {
	
	private static Faker faker = new Faker(new Locale("en"));
	
	public static String getFirstName() {
		return faker.name().firstName();
	}
	
	public static String getLastName() {
		return faker.name().lastName();
	}
	
	public static String getEmail() {
		return faker.internet().emailAddress();
	}
	
	public static String getPassword() {
		return faker.internet().password();
	}
}
