package com.tpSolar.halwaCityMarathon.controller;

import com.tpSolar.halwaCityMarathon.config.WebConfig;
import com.tpSolar.halwaCityMarathon.dto.LoginCred;
import com.tpSolar.halwaCityMarathon.dto.RegistrationRequestDto;
import com.tpSolar.halwaCityMarathon.dto.RegistrationResponseDto;
import com.tpSolar.halwaCityMarathon.model.RegistrationDetails;
import com.tpSolar.halwaCityMarathon.repository.RegistrationDetailsRepository;
import com.tpSolar.halwaCityMarathon.service.RegistrationDetailsService;
import com.tpSolar.halwaCityMarathon.util.*;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS })
@RestController
@RequestMapping("/halwaCityMarathon")
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    RegistrationDetailsRepository registrationDetailsRepo;

    @Autowired
    RegistrationDetailsService registrationDetailsService;

    @Autowired
    WebConfig webConfig;

    @Autowired
    CsvFile csvFile;

    @Autowired
    ImageCompressUtil imageCompressUtil;

    @Autowired
    JwtTokenGeneration jwtTokenGeneration;

    @Value("${app.User_Name}")
    private String User_Name;

    @Value("${app.Tata_User_Name}")
    private String Tata_User;

    @Value("${app.Password}")
    private String Password;

    @Value("${app.Tata_Password}")
    private String Tata_Password;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Autowired
    MailHandler mailHandler;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody LoginCred loginCred) throws Exception{
        logger.info("The Input  : ---- : {}",loginCred);

            /*var key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));*/

        // If a token is provided, validate it and return user details
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
            String username = jwtTokenGeneration.extractUsername(token, jwtSecret);

            if (username != null && username.equals(User_Name) && jwtTokenGeneration.validateToken(token, username, jwtSecret)){
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, "Token is valid", token, "Admin"));
            } else if (username != null && username.equals(Tata_User) && jwtTokenGeneration.validateToken(token, username, jwtSecret)) {
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, "Token is valid", token, "Tata Admin"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(HttpStatus.UNAUTHORIZED, "Invalid or expired token"));
            }
        }

        logger.info("The Input username : ---- : {}",loginCred.getUserName());
        logger.info("The Input password : ---- : {}",loginCred.getPassWord());
        if(loginCred.getUserName().equals(User_Name) && loginCred.getPassWord().equals(Password)){
            token = jwtTokenGeneration.generateToken(User_Name, jwtSecret);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.ACCEPTED,"You are successfully logged in", token, "Admin"), HttpStatus.ACCEPTED);
        } else if (loginCred.getUserName().equals(Tata_User) && loginCred.getPassWord().equals(Tata_Password)) {
            token = jwtTokenGeneration.generateToken(Tata_User, jwtSecret);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.ACCEPTED,"You are successfully logged in", token, "Tata Admin"), HttpStatus.ACCEPTED);
        } else if (!loginCred.getUserName().equals(User_Name) || !loginCred.getUserName().equals(Tata_User)){
            return new ResponseEntity<>(new ApiResponse(HttpStatus.UNAUTHORIZED,"Incorrect Username"),HttpStatus.UNAUTHORIZED);}
        else return new ResponseEntity<>(new ApiResponse(HttpStatus.UNAUTHORIZED,"Incorrect password"),HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrationDetails(@ModelAttribute RegistrationRequestDto registrationDto, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws Exception{

        RegistrationDetails registrationDetails = new RegistrationDetails();
        if(imageFile != null) logger.info("Original file size: {} KB", imageFile.getSize() / 1024);
        if (imageFile != null && !imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();

            if (contentType == null ||
                    !(contentType.equalsIgnoreCase("image/jpeg") ||
                            contentType.equalsIgnoreCase("image/jpg") ||
                            contentType.equalsIgnoreCase("image/png"))) {
                return ResponseEntity.badRequest().body("Only JPEG, JPG, or PNG images are allowed.");
            }

            BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());

            if (originalImage == null) {
                return ResponseEntity.badRequest().body("Invalid image content.");
            }

            // Compress the image
            byte[] compressedImage = imageCompressUtil.getCompressedImage(imageFile);

            // You can now store compressedImage in your RegistrationDetails or wherever needed
            registrationDetails.setImage(compressedImage); // example setter
        }

        if(registrationDto!=null){
            logger.info("exists count --{}", registrationDetailsRepo.alreadyRegisteredParticipant(registrationDto.getAadhar()));
            logger.info("Aadhar value --{}", registrationDto.getAadhar());
            if(registrationDetailsRepo.alreadyRegisteredParticipant(registrationDto.getAadhar()) == 0){
                registrationDetails.setEventName(registrationDto.getEventName());
                registrationDetails.setParticipantName(registrationDto.getParticipantName());
                registrationDetails.setAadhar(registrationDto.getAadhar());
                registrationDetails.setAge(registrationDto.getAge());
                registrationDetails.setDob(registrationDto.getDob());
                registrationDetails.setBloodGroup(registrationDto.getBloodGroup());
                registrationDetails.setEmail(registrationDto.getEmail());
                registrationDetails.setGender(registrationDto.getGender());
                registrationDetails.setContactNumber(registrationDto.getContactNumber());
                registrationDetails.setEmergencyContact(registrationDto.getEmergencyContact());
                registrationDetails.setTsize(registrationDto.getTsize());
                registrationDetailsRepo.save(registrationDetails);
                logger.info("Check --{}", registrationDetails);
                String participantNumber = registrationDetailsRepo.getParticipantNumber(registrationDetails.getAadhar());
                mailHandler.sendRegistrationConfirmationEmail(registrationDetails);
                return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"Data inserted successfully and the paticipant number is: " + participantNumber),HttpStatus.OK);
        }
            else{
                return new ResponseEntity<>(new ApiResponse(HttpStatus.CONFLICT,"Participant already registered"),HttpStatus.CONFLICT);
            }
        }
        else{
            return new ResponseEntity<>(new ApiResponse(HttpStatus.CONFLICT,"Data insufficient"),HttpStatus.CONFLICT);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/registrations")
    public ResponseEntity<?> getRegistrationDetails(@RequestParam Map<String, String> requestParams, @PageableDefault(page = 0, size = 20) Pageable pageable){
        Page<RegistrationResponseDto> result = registrationDetailsService.getRegistrationDetails(requestParams, pageable);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, result), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/uploadExcel")
    public ResponseEntity<?> uploadRegistrations (@RequestParam("file") MultipartFile file) throws Exception{
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        else{
            String result = csvFile.uploadExcel(file);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, result),HttpStatus.OK);
        }
    }
}
