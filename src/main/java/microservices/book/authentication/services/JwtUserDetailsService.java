package microservices.book.authentication.services;

import microservices.book.authentication.repository.UserRepository;
import microservices.book.authentication.user.Mail;
import microservices.book.authentication.user.User;
import microservices.book.authentication.user.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userDao;

    public static int noOfQuickServiceThreads = 20;
    private ScheduledExecutorService quickService = Executors.newScheduledThreadPool(noOfQuickServiceThreads);

    @Autowired
     private MailService mailService;

    private String password;


    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = userDao.findByAlias(username);//userDao.findByAliasAndPassword(username,bcryptEncoder.encode(password));
        Logger log = LoggerFactory.getLogger(JwtUserDetailsService.class);
        log.info("\n\n====== Greeting "+"\n\n");
        if (!user.isPresent()) {
            //System.out.println(user.toString());
            log.info("Invalid Credentials");
            //throw new UsernameNotFoundException("User not found with username: " + username);
            return new org.springframework.security.core.userdetails.User("Invalid Username","Invalid password", new ArrayList<>());


        }
        User user1 = user.get();
        log.info(" Hello:  {}", user1 );
        return org.springframework.security.core.userdetails.User.withUsername(user1.getAlias()).
                password(bcryptEncoder.encode(user1.getPassword())).roles("USER").build();
    }
    public void setPassword(String password){
        this.password = password;
    }


    public User save(UserDTO user) {
        Optional<User> oldUser = userDao.findByAlias(user.getAlias());
        if(oldUser.isPresent()){
            return null;
        }
        User newUser = new User(null, user.getAlias(),bcryptEncoder.encode(user.getPassword()));
       User saved = userDao.save(newUser);
        Mail mail = new Mail();
        mail.setMailFrom("fmfelbohemia@gmail.com");
        mail.setMailTo(user.getAlias());
        mail.setMailSubject("Registration Successful");
        mail.setMailContent("<div align=\"center\" style='font-size:30pt, font-weigth:bold,color:maroon'>\n" +
                "          <p>\n" +
                "            <img src='cid:images' />\n" +
                "            <br />Brain<span style='color:darkblue'>Trainer</span>\n" +
                "          </p>\n" +
                "\n" +
                "                                 \n" +
                "      </div>"
                +" <br /><p text-align='center'>Welcome to the braintrainer application<br /><br />Using this app, " +
                "will ensure your brain performance improve...</p>");
        //return  saved;

        quickService.submit(() -> {
            try{
                mailService.sendEmail(mail);
            }catch (Exception e){
                e.printStackTrace();
            }
            //mailSender.send(mimeMessageHelper.getMimeMessage());
        });

         /*
          mailService.sendEmail(mail);
          User saved = userDao.save(newUser);
        */
        return  saved;
    }

    public User deleteUser(UserDTO user){
        Optional<User> oldUser = userDao.findByAlias(user.getAlias());
        User user1 = null;
        if(oldUser.isPresent()){
            user1 = oldUser.get();
            userDao.delete(oldUser.get());
        }
        return user1;
    }
    public Iterable<User> allUsers(){
        return userDao.findAll();
    }
}

