package com.My_Car_Rental_Application.Security;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig
//        implements WebMvcConfigurer {

//    @Override
//    public void addResourceHandlers(
//            ResourceHandlerRegistry registry) {
//
//        registry
//                .addResourceHandler(
//                        "/car-images/**"
//                )
//                .addResourceLocations(
//                        "file:///D:/CarRentalUploads/cars/"
//                );
//    }
	
//	@Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/car-images/**")
//                .addResourceLocations("file:uploads/cars/");
//    }
	
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//	    registry.addResourceHandler("/car-images/**")
//	            .addResourceLocations("classpath:/uploads/");
//	}
	
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//	    // Points to an 'uploads' folder in the root working directory of the container
//	    registry.addResourceHandler("/car-images/**")
//	            .addResourceLocations("file:uploads/");
//	}
//	@Value("${app.car-images-dir}")
//	private String uploadPath;
//	
//	@Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // Use absolute file protocol path pointing to the working directory folder
////        String uploadPath = System.getProperty("user.dir") + "/uploads/";
//		
//        registry.addResourceHandler("/car-images/**")
//                .addResourceLocations("file:" + uploadPath);
//    }
//}


import java.nio.file.Paths;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 
/**
 * Serves saved car images at /car-images/<fileName>.
 * Uses the SAME property as AdminServiceImpl (app.car-images-dir), so the
 * folder where images are saved is always the folder they are served from.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
 
    @Value("${app.car-images-dir}")
    private String carImagesDir;
 
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
 
        String location = Paths.get(carImagesDir)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();
 
        // The trailing slash is required, otherwise nothing is served.
        if (!location.endsWith("/")) {
            location += "/";
        }
 
        System.out.println("Serving /car-images/** from: " + location);
 
        registry.addResourceHandler("/car-images/**")
                .addResourceLocations(location);
    }
}
