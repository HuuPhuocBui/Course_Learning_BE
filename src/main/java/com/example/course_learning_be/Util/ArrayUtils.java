package com.example.course_learning_be.Util;

import com.example.course_learning_be.exception.AppException;
import com.example.course_learning_be.exception.ErrorCode;
import java.util.List;

public class ArrayUtils {
  public static int getFree(List<?> list, int index) {

    if (index > list.size() + 2 || index < -1) {
      throw new AppException(ErrorCode.INVALID_INPUT);
    }

    try {
      return (int) list.get(index); // handle case mang rong
    } catch (IndexOutOfBoundsException e) {
      if (index - 1 < 0) {
        return 100;
      }
      return (int) list.get(index - 1) + 10;

    }
  }
}
