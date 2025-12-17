package ru.reminder.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import ru.reminder.app.REST.DTO.PagingResult;
import ru.reminder.app.REST.DTO.ReminderDto;
import org.springframework.stereotype.Service;
import ru.reminder.app.REST.DTO.ReminderResponse;
import ru.reminder.app.exception.BusinessException;
import ru.reminder.app.model.Reminder;
import ru.reminder.app.repository.ReminderRepository;
import ru.reminder.app.util.PaginationUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserService userService;

    public ReminderService(ReminderRepository reminderRepository, UserService userService) {
        this.reminderRepository = reminderRepository;
        this.userService = userService;
    }

    public ReminderResponse createReminder(ReminderDto reminderDto) {


        Reminder reminder = new Reminder(reminderDto.getTitle(), reminderDto.getDescription(),reminderDto.getRemind());
        reminder.setUser(userService.getUserById(reminderDto.getUserId())); // временно, пока не добавлена ауентификация

        return new ReminderResponse(reminderRepository.save(reminder).getId(),
                                                            reminder.getTitle(),
                                                            reminder.getDescription(),
                                                            reminder.getRemind(),
                                                            reminder.getUser().getId());
    }

    public ReminderResponse getReminderById(Long id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow( BusinessException.of(
                        HttpStatus.NOT_FOUND,
                        "Reminder with id " + id + " not found"));

        return new ReminderResponse(
                reminder.getId(),
                reminder.getTitle(),
                reminder.getDescription(),
                reminder.getRemind(),
                reminder.getUser().getId()
        );
    }

    public void  deleteById(Long id) {
        ReminderResponse reminder =  getReminderById(id);
        reminderRepository.deleteById(reminder.getId());
    }



    public PagingResult<ReminderDto> findAll(Integer page, Integer size, Long userId) {
        final Pageable pageable = PaginationUtils.getPageable(page-1,size);
        final Page<Reminder> entities = reminderRepository.findByUserId(userId,pageable);
        final List<ReminderDto> entitiesDto = entities.stream()
                                .map(entity ->{
                                        ReminderDto dto = new ReminderDto();
                                        dto.setTitle(entity.getTitle());
                                        dto.setDescription(entity.getDescription());
                                        dto.setRemind(entity.getRemind());
                                        dto.setUserId(userId);
                                    return dto;}
                                ).collect(Collectors.toList());
        return new PagingResult<>(
                entitiesDto,
                entities.getTotalPages(),
                entities.getTotalElements(),
                entities.getSize(),
                entities.getNumber(),
                entities.isEmpty()
        );
    }


}
