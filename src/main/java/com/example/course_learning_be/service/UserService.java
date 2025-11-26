package com.example.course_learning_be.service;

import com.example.course_learning_be.Util.SecurityUtil;
import com.example.course_learning_be.dto.request.UserRegisterRequest;
import com.example.course_learning_be.dto.request.UserUpdateRequestDTO;
import com.example.course_learning_be.dto.response.AuthenticationResponse;
import com.example.course_learning_be.dto.response.UserLearningCourseResponseDTO;
import com.example.course_learning_be.dto.response.UserResponseDTO;
import com.example.course_learning_be.entity.Course;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.entity.UserSession;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.mapper.UserMapper;
import com.example.course_learning_be.repository.MongoService;
import com.example.course_learning_be.repository.UserRepository;
import com.example.course_learning_be.repository.UserSessionRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import io.micrometer.common.util.StringUtils;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
  private final MongoService mongoService;
  private final CourseService courseService;
  private final SecurityUtil securityUtil;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final FileClient fileClient;
  private final UserSessionRepository userSessionRepository;
//  @NonFinal
//  protected static final String SIGNER_KEY = "0Fo5yxReydyX2rCmgvtI5nELTqjS+o9XPuS4r9G7NzTZ2fSoMnxu8J40VDCLgc8A";
   @Value("${jwt.signer-key}")
   private String signerKey;

  public boolean userRegister(UserRegisterRequest userRegisterRequest) {
    if (StringUtils.isBlank(userRegisterRequest.getFullName())) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    User user = new User();
    user.setFullName(userRegisterRequest.getFullName());
    user.setEmail(userRegisterRequest.getEmail());
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
    user.setRole("ADMIN");
    user.setPurchasedCourseIds(new ArrayList<>());
    User savedUser = mongoService.save(user);
    return savedUser != null && savedUser.getId() != null;
  }

  public AuthenticationResponse userLogin(String fullname, String password) {
    if (StringUtils.isBlank(fullname)) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    Query query = new Query(Criteria.where("fullName").is(fullname));
    User existingUser = mongoService.findOne(query, User.class)
        .orElseThrow(() -> new AppException(ErrorCode.AUTHENTICATION));

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    boolean authenticated = passwordEncoder.matches(password, existingUser.getPassword());
    if (!authenticated) {
      throw new AppException(ErrorCode.AUTHENTICATION);
    }

    var token = generateToken(fullname);

    return AuthenticationResponse.builder()
        .accessToken(token)
        .countryCode(null)   // FE cần có field này, nên để null
        .userId(true)        // FE định nghĩa userId: boolean
        .build();
  }

  public AuthenticationResponse adminLogin(String email, String password) {
    if (StringUtils.isBlank(email)) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    Query query = new Query(Criteria.where("email").is(email));
    User existingUser = mongoService.findOne(query, User.class)
        .orElseThrow(() -> new AppException(ErrorCode.AUTHENTICATION));

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    boolean authenticated = passwordEncoder.matches(password, existingUser.getPassword());
    if (!authenticated) {
      throw new AppException(ErrorCode.AUTHENTICATION);
    }

    var token = generateToken(email);

    return AuthenticationResponse.builder()
        .accessToken(token)
        .countryCode(null)   // FE cần có field này, nên để null
        .userId(true)        // FE định nghĩa userId: boolean
        .build();
  }


  public String generateToken(String email){
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
    User user = mongoService.findOne(
        new Query(Criteria.where("email").is(email)), User.class
    ).orElseThrow(() -> new AppException(ErrorCode.AUTHENTICATION));
    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .subject(email)
        .issuer("phuocbiu")
        .issueTime(new Date())
        .expirationTime(new Date(
            Instant.now().plus(31, ChronoUnit.DAYS).toEpochMilli()
        ))
        .claim("role", user.getRole())
        .claim("purchasedCourseIds", user.getPurchasedCourseIds())
        .build();
    Payload payload = new Payload(jwtClaimsSet.toJSONObject());
    JWSObject jwsObject = new JWSObject(header, payload);
    try {
      jwsObject.sign(new MACSigner(signerKey.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
  }

  public UserResponseDTO getUserProfile() {
    Query query = new Query(Criteria.where("email").is(SecurityUtil.getCurrentUserId()));
    User user = mongoService.findOne(query, User.class)
        .orElseThrow(() -> new AppException(ErrorCode.AUTHENTICATION));
    return UserMapper.mapToUserDto(user);
  }

  public UserLearningCourseResponseDTO getUserLearningCourse() {
    List<Course> courseList = courseService.getAllCourseOfCurrentUser();

    List<UserLearningCourseResponseDTO.CourseDTO> courseDtos = courseList.stream().map(c -> UserLearningCourseResponseDTO.CourseDTO.builder()
        .id(c.getId())
        .title(c.getTitle())
        .level(c.getLevel())
        .pinImageUrl(c.getPinImageUrl())
        .authorName(c.getOwner().getFullName())
        .duration(c.getDuration())
        .description(c.getDescription())
        .build()).toList();

    return UserLearningCourseResponseDTO.builder()
        .totalCourse(courseDtos.size())
        .courses(courseDtos)
        .build();
  }

  public UserResponseDTO updateUserProfile(UserUpdateRequestDTO requestDTO) {
    String avatarUrl = requestDTO.getNewAvatarFile() != null ?
        fileClient.uploadImageFile(requestDTO.getNewAvatarFile()) :
        null;

    requestDTO.setNewAvatarUrl(avatarUrl);

    User user = securityUtil.getCurrentUser();

    userMapper.updateEntityFromRequestDTO(user, requestDTO);
    userRepository.save(user);

    return userMapper.fromEntityToResponseDTO(user);
  }

  public String updateUserAvatar(MultipartFile file) {
    User user = securityUtil.getCurrentUser();
    String fileUrl = fileClient.uploadImageFile(file);

    User fromDB = userRepository.findById(user.getId()).get();
    fromDB.setAvatarUrl(fileUrl);
    userRepository.save(fromDB);

    return fileUrl;
  }

  public void invalidateOldSessions(String userId) {
    List<UserSession> oldSessions = userSessionRepository.findByUserIdAndValidTrue(userId);
    for (UserSession s : oldSessions) {
      s.setValid(false);
    }
    userSessionRepository.saveAll(oldSessions);
  }

  // 4️⃣ Tạo BE token mới cho thiết bị hiện tại
  public String generateToken(String userId, String deviceId) {
    // Dùng UUID làm token đơn giản, có thể thay bằng JWT nếu muốn
    String token = UUID.randomUUID().toString();

    UserSession session = new UserSession();
    session.setUserId(userId);
    session.setDeviceId(deviceId);
    session.setToken(token);
    session.setValid(true);
    session.setCreatedAt(LocalDateTime.now());

    userSessionRepository.save(session);
    return token;
  }

  // Optional: kiểm tra token BE có hợp lệ không
  public boolean validateToken(String token) {
    UserSession session = userSessionRepository.findByToken(token);
    return session != null && session.isValid();
  }


}
