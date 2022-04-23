package microservices.book.authentication.controllers;

import lombok.extern.slf4j.Slf4j;
import microservices.book.authentication.configuration.JwtTokenUtil;
import microservices.book.authentication.services.JwtUserDetailsService;
import microservices.book.authentication.user.JwtResponse;
import microservices.book.authentication.user.User;
import microservices.book.authentication.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j

@RestController
@RequestMapping("/auth")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserDTO authenticationRequest) throws Exception {

        userDetailsService.setPassword(authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getAlias());
        if(userDetails.getUsername().equals("Invalid Username")){
            return ResponseEntity.status(401).body("Invalid Credentials");
        }

        final String token = jwtTokenUtil.generateToken(userDetails);
        JwtResponse response = new JwtResponse(token);
        log.info("The token contains: {}", token);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
        User user1 = userDetailsService.save(user);
        if(user1 == null) {
            log.info("User {} exist already",user.getAlias());
            return ResponseEntity.status(401).body(user);
        }
        //log.info(user1.toString());
        return ResponseEntity.ok(user1);
    }
    @GetMapping("/users")
    public Iterable<User> allRegisteredUsers(){
        log.info(userDetailsService.allUsers().toString());
        return userDetailsService.allUsers();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@RequestBody UserDTO user) throws Exception {
        User user1 = userDetailsService.deleteUser(user);
        if(user1 == null)
            return  ResponseEntity.status(404).body(user1);
        return ResponseEntity.ok(user1);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
