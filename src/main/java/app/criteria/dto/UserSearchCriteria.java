package app.criteria.dto;

import java.util.List;
import lombok.Data;

/**
 * DTO chứa các tham số tìm kiếm động cho User.
 */
@Data
public class UserSearchCriteria {

    private String id;

    private String login;

    private String email;

    private String firstName;

    private String lastName;

    private Boolean activated;

    private List<String> ids;

    private List<String> logins;

    private List<String> emails;

    private String phoneNumber;

    private String addressCity;

    private String addressDistrict;

    private String hometownCity;

    private String hometownDistrict;
}
