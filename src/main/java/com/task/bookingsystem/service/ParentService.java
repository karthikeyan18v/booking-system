package com.task.bookingsystem.service;

import com.task.bookingsystem.dto.request.CreateParentRequest;
import com.task.bookingsystem.dto.response.ParentResponse;
import com.task.bookingsystem.entity.Parent;
import com.task.bookingsystem.repository.ParentRepository;
import com.task.bookingsystem.util.TimezoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;

    @Transactional
    public ParentResponse createParent(CreateParentRequest request) {
        TimezoneUtil.validateAndGet(request.getTimezone());

        Parent parent = new Parent();
        parent.setName(request.getName());
        parent.setEmail(request.getEmail());
        parent.setTimezone(request.getTimezone());
        parent = parentRepository.save(parent);
        return toResponse(parent);
    }

    private ParentResponse toResponse(Parent parent) {
        return ParentResponse.builder()
                .id(parent.getId())
                .name(parent.getName())
                .email(parent.getEmail())
                .timezone(parent.getTimezone())
                .build();
    }
}
