package com.ikea.assignment.product_information_loader;

import com.ikea.assignment.product_information_loader.connection.DataStaxAstraProperties;
import com.ikea.assignment.product_information_loader.model.Country;
import com.ikea.assignment.product_information_loader.model.Product;
import com.ikea.assignment.product_information_loader.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Random;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class ProductInformationLoaderApplication {

	@Autowired
	private ProductRepository productRepository;

	private  WebClient webClient;

	@Value("${products.datadump.location.url}")
	private String dataDumpUrl;
	@Value("${products.datadump.records}")
	private String numberOfRecords;

	public static void main(String[] args) {
		SpringApplication.run(ProductInformationLoaderApplication.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@PostConstruct
	public void start() {
		initProducts();
	}

	private void initProducts() {
		webClient = WebClient.builder().baseUrl(dataDumpUrl).build();
		String response = webClient.get().uri("?limit={numberOfRecords}", numberOfRecords).retrieve().bodyToMono(String.class).block();
		Random random = new Random();
		try {
			JSONObject productsResponse = new JSONObject(response);
			JSONArray products = productsResponse.optJSONArray("products");
			for (int i = 0 ; i < products.length(); i++) {
				String productString = products.get(i).toString();
				JSONObject productJSON = new JSONObject(productString);
				Product product = new Product();
				product.setSku(productJSON.optString("sku"));
				product.setTitle(productJSON.optString("title"));
				product.setCountry(Country.values()[random.nextInt(Country.values().length)]);
				product.setPrice(new BigDecimal(productJSON.optString("price")));
				product.setDiscountPercentage(Double.valueOf(productJSON.optString("discountPercentage")));
				product.setImageUrl(productJSON.optJSONArray("images").get(0).toString());
				productRepository.save(product);
				System.out.println("Product" + product.getTitle() + "was saved");
			}

		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

}
