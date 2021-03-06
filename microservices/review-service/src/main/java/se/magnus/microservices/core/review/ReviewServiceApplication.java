package se.magnus.microservices.core.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan("se.magnus")
public class ReviewServiceApplication {
	private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceApplication.class);
	private final Integer connectionPoolSize;

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(ReviewServiceApplication.class, args);
		String mysqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
		LOG.info("Connected to MySQL: " + mysqlUri);
	}

	//	pool size default: 10
	@Autowired
	public ReviewServiceApplication(@Value("${spring.datasource.maximum-pool-size:10}") Integer connectionPoolSize) {
		this.connectionPoolSize = connectionPoolSize;
	}

	//	Limit the pool size for threads to be used by non-blocking service. (review service)
	@Bean
	public Scheduler jdbcScheduler() {
		LOG.info("Creates a jdbcScheduler with connectionPoolSize = " + connectionPoolSize);
		return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
	}
}
