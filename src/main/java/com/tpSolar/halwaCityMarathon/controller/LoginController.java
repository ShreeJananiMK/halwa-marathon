package com.tpSolar.halwaCityMarathon.controller;

import com.tpSolar.halwaCityMarathon.config.WebConfig;
import com.tpSolar.halwaCityMarathon.dto.LoginCred;
import com.tpSolar.halwaCityMarathon.dto.RegistrationRequestDto;
import com.tpSolar.halwaCityMarathon.dto.RegistrationResponseDto;
import com.tpSolar.halwaCityMarathon.model.RegistrationDetails;
import com.tpSolar.halwaCityMarathon.repository.RegistrationDetailsRepository;
import com.tpSolar.halwaCityMarathon.service.RegistrationDetailsService;
import com.tpSolar.halwaCityMarathon.util.ApiResponse;
import com.tpSolar.halwaCityMarathon.util.CsvFile;
import com.tpSolar.halwaCityMarathon.util.ImageCompressUtil;
import com.tpSolar.halwaCityMarathon.util.JwtTokenGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE })
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

    @Value("${app.Password}")
    private String Password;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody LoginCred loginCred) throws Exception{
        logger.info("The Input  : ---- : {}",loginCred);

            /*var key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));*/

        // If a token is provided, validate it and return user details
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
            String username = jwtTokenGeneration.extractUsername(token);

            if (username != null && username.equals(User_Name) && jwtTokenGeneration.validateToken(token, username)) {
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, "Token is valid", token, "Admin"));
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(HttpStatus.UNAUTHORIZED, "Invalid or expired token"));
            }
        }

        logger.info("The Input username : ---- : {}",loginCred.getUserName());
        logger.info("The Input password : ---- : {}",loginCred.getPassWord());
        if(loginCred.getUserName().equals(User_Name) && loginCred.getPassWord().equals(Password)){
            token = jwtTokenGeneration.generateToken(User_Name);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.ACCEPTED,"You are successfully logged in", token, "Admin"), HttpStatus.ACCEPTED);
        }
        else if (!loginCred.getUserName().equals(User_Name) && loginCred.getPassWord().equals(Password)){
            return new ResponseEntity<>(new ApiResponse(HttpStatus.UNAUTHORIZED,"Incorrect Username"),HttpStatus.UNAUTHORIZED);}
        else return new ResponseEntity<>(new ApiResponse(HttpStatus.UNAUTHORIZED,"Incorrect password"),HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrationDetails(@ModelAttribute RegistrationRequestDto registrationDto, @RequestParam("imageFile") MultipartFile imageFile) throws Exception{

        String contentType = imageFile.getContentType();
        byte[] compressedImage;
        BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());
        if (contentType == null || !(contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/png"))) {
            return ResponseEntity.badRequest().body("Only JPEG images are allowed.");
        }
        else if (originalImage == null) {
            return ResponseEntity.badRequest().body("Invalid image content.");
        }
        else{
            compressedImage = imageCompressUtil.getCompressedImage(imageFile);
        }
        RegistrationDetails registrationDetails = new RegistrationDetails();
        if(registrationDto!=null){
            if(registrationDetailsRepo.alreadyRegisteredParticipant(registrationDto.getAadhar()) != 1){
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
            registrationDetails.setImage(compressedImage);
            registrationDetails.setTsize(registrationDto.getTsize());
            registrationDetailsRepo.save(registrationDetails);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.OK,"Data inserted successfully"),HttpStatus.OK);
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
    public ResponseEntity<?> getRegistrationDetails(Map<String, String> requestParams, Pageable pageable){
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
