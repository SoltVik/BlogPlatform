package platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Timestamp;
import java.util.Date;

import static org.apache.tomcat.jni.Time.now;

@SpringBootApplication
public class SimpleWebApp {

	public static void main(String[] args) {
		SpringApplication.run(SimpleWebApp.class, args);
	}
}
