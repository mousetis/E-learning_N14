package iuh.fit.elearning.user.mapper;

import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.dto.response.UserSummaryResponse;
import iuh.fit.elearning.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final StorageService storageService;

    public UserMapper(StorageService storageService) {
        this.storageService = storageService;
    }

    public UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(),
                storageService.resolveUrl(user.getAvatarUrl()), user.getJobTitle());
    }
}
