package com.nrp;

import com.nrp.util.LoggingInterceptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SpringBootBuildPipelineApplication implements WebMvcConfigurer
{

    public static void main(String[] args)
    {
        SpringApplication.run(SpringBootBuildPipelineApplication.class, args);
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(new LoggingInterceptor()).addPathPatterns("/**");
    }


    /*@Bean
    public Docket docket()
    {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage(getClass().getPackage().getName()))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(generateApiInfo());
    }*/

    @Bean
    public OpenAPI registrationOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SpringBoot DEVSECOPS SAMPLE PROJECT")
                        .description("This is a sample project for Devsecops CICD pipeline.")
                        .version("Version 1.0 - mw"));
    }


    /*private ApiInfo generateApiInfo()
    {
        return new ApiInfo("SpringBoot DEVSECOPS SAMPLE PROJECT", "This is a sample project for Devsecops CICD pipeline.", "Version 1.0 - mw",
            "urn:tos", "nrp", "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0");
    }*/
}
