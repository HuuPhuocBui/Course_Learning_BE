package com.example.course_learning_be.service;

import com.example.course_learning_be.dto.request.BenefitRequestDTO;
import com.example.course_learning_be.dto.response.BaseResponseList;
import com.example.course_learning_be.dto.response.BenefitResponseDTO;
import com.example.course_learning_be.dto.response.PageResponse.PageInfo;
import com.example.course_learning_be.entity.Benefit;
import com.example.course_learning_be.enums.Language;
import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import com.example.course_learning_be.mapper.BenefitMapper;
import com.example.course_learning_be.repository.BenefitRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BenefitService {
  private final BenefitRepository benefitRepository;
  private final BenefitMapper benefitMapper;
  public BaseResponseList<BenefitResponseDTO<Map<Language, String>>> getAll() {
    List<Benefit> list = benefitRepository.findAll(Sort.by(Sort.Direction.ASC, "nO"));

    List<BenefitResponseDTO<Map<Language, String>>> data = list.stream()
        .map(benefitMapper::fromEntityToResponse)
        .toList();

    int totalItems = data.size();
    int pageSize = totalItems; // nếu ko phân trang
    int currentPage = 1;

    PageInfo pageInfo = PageInfo.builder()
        .currentPage(currentPage)
        .totalItems(totalItems)
        .totalPages((int) Math.ceil((double) totalItems / pageSize))
        .hasPreviousPage(currentPage > 1)
        .hasNextPage(currentPage * pageSize < totalItems)
        .build();

    return BaseResponseList.<BenefitResponseDTO<Map<Language, String>>>builder()
        .data(data)
        .pageInfo(pageInfo)
        .build();
  }


  public BenefitResponseDTO<Map<Language, String>> create(BenefitRequestDTO benefitRequestDTO) {
    Benefit benefit = benefitMapper.fromCreateRequestToEntity(benefitRequestDTO);
    benefitRepository.save(benefit);

    return benefitMapper.fromEntityToResponse(benefit);

  }

  public BenefitResponseDTO<Map<Language, String>> update(String id, BenefitRequestDTO benefitRequestDTO) {
    Benefit benefit = benefitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.INVALID_INPUT));
    benefitMapper.updateEntityFromRequestDTO(benefit, benefitRequestDTO);
    benefitRepository.save(benefit);
    return benefitMapper.fromEntityToResponse(benefit);
  }
  public BenefitResponseDTO<Map<Language, String>> getById(String id) {
    Benefit benefit = benefitRepository.findById(id).orElseThrow(() -> new RuntimeException("Benefit not found"));

    return benefitMapper.fromEntityToResponse(benefit);
  }
  public void delete(String id) {
    benefitRepository.deleteById(id);
  }
  public List<BenefitResponseDTO<String>> getAllLocalized() {
    List<Benefit> list = benefitRepository.findAll(
        Sort.by(Sort.Direction.DESC, "nO")
    ).stream().limit(6).toList();

    return list.stream()
        .map(benefitMapper::fromEntityToLocalizeResponse)
        .toList();
  }

}
