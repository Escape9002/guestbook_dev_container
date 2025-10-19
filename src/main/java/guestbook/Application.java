/*
 * Copyright 2014-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guestbook;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The core class to bootstrap our application. It triggers Spring Boot's auto-configuration, component scanning and
 * configuration properties scanning using the {@link SpringBootApplication} convenience annotation. At the same time,
 * this class acts as configuration class to configure additional components (see {@link #init(GuestbookRepository)})
 * that the Spring container will take into account when bootstrapping.
 *
 * @author Paul Henke
 * @author Oliver Drotbohm
 */
@SpringBootApplication
public class Application {

	/**
	 * The main application method, bootstraps the Spring container.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Some initializing code to populate our database with some {@link GuestbookEntry}s. Beans of type
	 * {@link CommandLineRunner} will be executed on application startup which makes them a convenient way to run
	 * initialization code.
	 */
	@Bean
	CommandLineRunner init(GuestbookRepository guestbook) {

		return args -> {

			Stream.of( //
					new GuestbookEntry("H4xx0r", "first!!!"), //
					new GuestbookEntry("Arni", "Hasta la vista, baby"), //
					new GuestbookEntry("Duke Nukem",
							"It's time to kick ass and chew bubble gum. And I'm all out of gum."), //
					new GuestbookEntry("Gump1337",
							"Mama always said life was like a box of chocolates. You never know what you're gonna get.")) //
					.forEach(guestbook::save);
		};
	}

	/**
	 * This class customizes the web and web security configuration through callback methods provided by the
	 * {@link WebMvcConfigurer} interface.
	 */
	@Configuration
	@EnableMethodSecurity(prePostEnabled = true)
	static class SecurityConfiguration implements WebMvcConfigurer {

		/**
		 * https://stackoverflow.com/questions/19414734/understanding-spring-autowired-usage
		 * 
		 * this is the dependency injection at work. Spring has a little base container where it stores stuff.
		 * we can add things to this container by anotating our stuff with the little tags (@component, service, repo, config, etc)
		 * We can ask for stuff by using these little tags (autowired, Bean, etc.).
		 * Spring will then search for any existing objects with that name and give us an instance 
		 * (// QUESTION is it a new instance, or a shared instance?)
		 * 
		 * This way, we dont need to care about initialization and / or passing around instances in our program.
		 * Spring simply ensures we get what we ask for.
		 * 
		 */
		@Autowired
		private UserRepository userRepository;

		/*
		 * (non-Javadoc)
		 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)
		 */
		@Override
		public void addViewControllers(ViewControllerRegistry registry) {

			// Route requests to /login to the login view (a default one provided by Spring Security)
			registry.addViewController("/login").setViewName("login");
		}

		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

			// Allow all requests on the URI level, configure form login.
			http.csrf(it -> it.disable())
					.authorizeHttpRequests(it -> it.anyRequest().permitAll())
					.formLogin(it -> {})
					.logout(it -> it.logoutSuccessUrl("/").clearAuthentication(true));

			return http.build();
		}

		/**
		 * https://www.baeldung.com/spring-bean
		 * 
		 * ! The UserDetailsService is an interface !
		 * 
		 * The Bean can be understood as a factory:
		 * You want a UserDetailsService? Here is how to build one!
		 * This function is then added to the spring storage and will be used by anything 
		 * asking for a UserDetailsService.
		 * 
		 * 
		 */
		@Bean
		public UserDetailsService userDetailsService() {
			/**
			 * this is a lambda. The UserDetailsService has a single function:
			 * > 	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
			 * 
			 * we basicly come along, grab the parameter (username) and hand it off to our implementation.
			 * This implementes the interface.
			 * 
			 * //TODO how to write this without the lambda
			 */
			return username -> {
				User user = userRepository.findByUsername(username);

				if(user == null){
					throw new UsernameNotFoundException("User not found");
				}

				/**
				 * since we created our own User-class we have to specify the fullpath
				 * of the springframework-security native User. 
				 * we should probably use another name for our own user class...
				 * 
				 * Why create our own user?
				 * we need to be able to store our users via the jpa. 
				 * This is not possible using the spring-security-user.
				 * The userDetailsService is thus needed, to translate from our storable user
				 * to a user we can run our authentication with.
				 */
				return org.springframework.security.core.userdetails.User.builder()
					.username(user.getUsername())
					.password(user.getPassword())
					.roles(user.getRole())
					.build();
			};
		}

		/**
		 * creates a password encoder instance, where the encode is used.
		 * happens via the Bean "replacement" magic
		 */
		@Bean
		public PasswordEncoder passwordEncoder(){
			return new BCryptPasswordEncoder();
		}

	}
}
