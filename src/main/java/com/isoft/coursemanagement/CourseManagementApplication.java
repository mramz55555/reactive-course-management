package com.isoft.coursemanagement;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableWebFlux
public class CourseManagementApplication {
    public static void main(String[] args) {
        //ma az delay use kardim ke mishe elements emmit mishan ba takhir ke baes mishe thread main tamom she va subscription ham
        //end beshe vali elements kamel emmit nashodan.
//        Flux<Integer> longNumbersFlux=Flux.fromIterable(List.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20));
//        longNumbersFlux
//                .map(AtomicInteger::new)
//                //khod concatMap wait ta complete she operation va async nist ta order hefz she va delay ham wait dare ke kheili slow
//                // miseh va momkene nashe aslan kheili az elements print nashan
//                .concatMap(n->{
//                    System.out.println("The number is : "+n);
//                    return Mono.just(n);
//                })
////                .delayElements(Duration.ofSeconds(new Random().nextInt(3)))
//                .log().subscribe();
        SpringApplication.run(CourseManagementApplication.class, args);
    }

    public static <T> Mono<T> monoError(int courseId) {
        return Mono.error(new IllegalArgumentException("invalid input : " + courseId));
    }

    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
//        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        return initializer;
    }
}
