package com.example.course_learning_be.service;

import com.example.course_learning_be.Util.SecurityUtil;
import com.example.course_learning_be.dto.request.ContactRequestDTO;
import com.example.course_learning_be.dto.response.BaseResponseList;
import com.example.course_learning_be.dto.response.ContactResponseDTO;
import com.example.course_learning_be.dto.response.TestimonialResponseDTO;
import com.example.course_learning_be.entity.Contact;
import com.example.course_learning_be.entity.User;
import com.example.course_learning_be.repository.ContactRepository;
import com.example.course_learning_be.repository.PurchaseRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@RequiredArgsConstructor
public class ContactService {

  private final SecurityUtil securityUtil;
  private final ContactRepository contactRepository;
  private final PurchaseRepository purchaseRepository;

  public void sendMessage(ContactRequestDTO contactRequestDTO) {
    User user = securityUtil.getCurrentUser();
    String userId = user.getId();           // id của người mua
    String courseName = contactRequestDTO.getTitleCourse();

    // 🔒 Check xem user đã mua khóa học chưa
    purchaseRepository.findByBuyerIdAndCourseName(userId, courseName)
        .orElseThrow(() -> new RuntimeException("Bạn chưa mua khóa học này nên không thể phản hồi."));
    Contact contact = Contact.builder()
        .fullName(user.getFullName())  // Lấy tên từ token user
        .email(user.getEmail())
        .phoneNumber(contactRequestDTO.getPhone())
        .titleCourse(contactRequestDTO.getTitleCourse())
        .message(contactRequestDTO.getMessage())
        .build();

    // Save vào MongoDB
    contactRepository.save(contact);
  }

  public BaseResponseList<ContactResponseDTO> getAllReview() {
    List<Contact> contacts = contactRepository.findAll(
        Sort.by(Sort.Direction.DESC, "id")  // mới nhất trước
    );

    // Map sang DTO FE cần
    List<ContactResponseDTO> dtos = contacts.stream()
        .map(contact -> ContactResponseDTO.builder()
            .fullName(contact.getFullName())
            .email(contact.getEmail())
            .course(contact.getTitleCourse())
            .comment(contact.getMessage())
            .phone(contact.getPhoneNumber())
            .build())
        .toList();

    // Trả về BaseResponseList
    return BaseResponseList.<ContactResponseDTO>builder()
        .data(dtos)
        .pageInfo(null) // không phân trang
        .build();
  }

  public List<TestimonialResponseDTO> getAllReviewClient() {
    List<Contact> contacts = contactRepository.findAll();

    return contacts.stream()
        .map(contact -> TestimonialResponseDTO.builder()
            .comment(contact.getMessage())       // FE -> data.comment
            .userFullName(contact.getFullName())
            .courseName(contact.getTitleCourse())// FE -> data.userFullName
            .build())
        .toList();
  }


}


