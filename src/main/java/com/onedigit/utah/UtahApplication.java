package com.onedigit.utah;

import com.onedigit.utah.rest.api.ExchangeAdapter;
import com.onedigit.utah.ws.KucoinAdapterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UtahApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(UtahApplication.class, args);
	}

}
