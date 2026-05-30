package com.task.bookingsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bookingSystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Global Class Offering Booking System")
                        .description("""
                                Backend API for a global live-learning platform.
                                Teachers create offerings and sessions; parents browse and book them.
                                All session times are stored in UTC and returned in the caller's timezone.
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("Booking System Team")))
                .tags(List.of(
                        new Tag().name("Courses").description("Course catalog — creation requires a valid teacherId (teacher-only action)"),
                        new Tag().name("Teachers").description("Teacher registration and their offerings"),
                        new Tag().name("Parents").description("Parent registration and their bookings"),
                        new Tag().name("Offerings").description("Offering creation, session management, and browsing"),
                        new Tag().name("Bookings").description("Booking creation and conflict enforcement")
                ));
    }
}
