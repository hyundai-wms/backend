package com.myme.mywarehome.infrastructure.config.resolver;

import com.myme.mywarehome.infrastructure.common.request.SelectedDateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDate;
import java.util.Set;

@Component
public class SelectedDateArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SelectedDate.class) &&
                parameter.getParameterType().equals(LocalDate.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String selectedDate = webRequest.getParameter("selectedDate");

        SelectedDateRequest dateRequest = new SelectedDateRequest(selectedDate);

        Set<ConstraintViolation<SelectedDateRequest>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(dateRequest);

        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(violations.iterator().next().getMessage());
        }

        return SelectedDateRequest.toLocalDate(dateRequest);
    }
}
