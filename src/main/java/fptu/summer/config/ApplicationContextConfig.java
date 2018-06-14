/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.config;

import fptu.summer.dao.RoleDAO;
import fptu.summer.dao.UserDAO;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 *
 * @author ADMIN
 */
@Configuration
@ComponentScan("fptu.summer.*")
public class ApplicationContextConfig {

    public ApplicationContextConfig() {
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource rb = new ResourceBundleMessageSource();
        rb.setBasenames(new String[]{"messages/validator"});
        return rb;
    }

    @Bean(name = "viewResolver")
    public InternalResourceViewResolver getViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Autowired
    @Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory() throws Exception {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration.configure("hibernate.cfg.xml");
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        return configuration.buildSessionFactory(ssrb.build());
    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new PasswordEncoder() {
//            @Override
//            public String encode(CharSequence cs) {
//                return cs.toString();
//            }
//
//            @Override
//            public boolean matches(CharSequence cs, String string) {
//                return cs.toString().equals(string);
//            }
//        };
//    }

    @Bean(name = "userDAO")
    public UserDAO getUserDAO() {
        return new UserDAO();
    }

    @Bean(name = "roleDAO")
    public RoleDAO getRoleDAO() {
        return new RoleDAO();
    }

}
