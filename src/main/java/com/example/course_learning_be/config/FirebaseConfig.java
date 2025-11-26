package com.example.course_learning_be.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

  @PostConstruct
  public void firebaseInit() throws IOException {
    InputStream serviceAccount = getClass().getClassLoader()
        .getResourceAsStream("serviceAccountKey.json");

    if (serviceAccount == null) {
      throw new RuntimeException("Firebase serviceAccountKey.json not found in resources!");
    }

    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

    if (FirebaseApp.getApps().isEmpty()) {
      FirebaseApp.initializeApp(options);
    }
  }
}
