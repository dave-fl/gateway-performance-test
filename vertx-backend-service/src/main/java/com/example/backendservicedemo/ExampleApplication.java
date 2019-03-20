package com.example.backendservicedemo;

import com.example.backendservicedemo.verticles.StringGeneratingVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.VerticleFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ComponentScan()
public class ExampleApplication {

    public static void main(String[] args) {
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setMaxEventLoopExecuteTimeUnit(TimeUnit.SECONDS);
        vertxOptions.setMaxEventLoopExecuteTime(20);
        Vertx vertx = Vertx.vertx(vertxOptions);

        ApplicationContext context = new AnnotationConfigApplicationContext(ExampleApplication.class);

        VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);
        vertx.registerVerticleFactory(verticleFactory);

        // Scale the verticles on cores: create 8 instances during the deployment
        DeploymentOptions options = new DeploymentOptions().setInstances(8);
        options.setMaxWorkerExecuteTimeUnit(TimeUnit.SECONDS);
        options.setMaxWorkerExecuteTime(10);
        vertx.deployVerticle(verticleFactory.prefix() + ":" + StringGeneratingVerticle.class.getName(), options);
    }

}