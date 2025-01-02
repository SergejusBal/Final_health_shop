package wellness.shop.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wellness.shop.Repositories.DateRegistrationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DateRegistrationService {

    @Autowired
    DateRegistrationRepository dateRegistrationRepository;

    public List<LocalDateTime> getTimeSlotsWithinDateRange(LocalDate startDate, LocalDate endDate, String webSocketKey) {
        return dateRegistrationRepository.getTimeSlotsWithinDateRange(startDate,endDate,webSocketKey);

    }


}
