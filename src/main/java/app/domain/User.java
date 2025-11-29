package app.domain;

import app.config.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Người dùng.
 */
@Table("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKey
    private String id; // ID người dùng

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login; // Tên đăng nhập

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    private String password; // Mật khẩu (đã mã hóa)

    @Size(max = 50)
    private String firstName; // Tên

    @Size(max = 50)
    private String lastName; // Họ

    @Email
    @Size(min = 5, max = 254)
    private String email; // Email

    @Size(max = 256)
    @Column("avatar_url")
    private String avatarUrl; // URL ảnh đại diện

    @Size(max = 1000)
    private String bio; // Giới thiệu bản thân

    @Size(max = 20)
    @Column("phone_number")
    private String phoneNumber; // Số điện thoại

    @Column("attributes")
    private java.util.Map<String, String> attributes; // Thông tin động (Key-Value)

    @Column("address")
    private Address address; // Địa chỉ hiện tại

    @Column("hometown")
    private Address hometown; // Quê quán

    private boolean activated = false; // Đã kích hoạt

    @Size(min = 2, max = 10)
    @Column("lang_key")
    private String langKey; // Ngôn ngữ

    @Size(max = 20)
    @Column("activation_key")
    @JsonIgnore
    private String activationKey; // Mã kích hoạt

    @Size(max = 20)
    @Column("reset_key")
    @JsonIgnore
    private String resetKey; // Mã đặt lại mật khẩu

    @Column("reset_date")
    private Instant resetDate = null; // Ngày đặt lại mật khẩu

    @JsonIgnore
    private Set<String> authorities = new HashSet<>(); // Quyền hạn

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    // Lowercase the login before saving it in database
    public void setLogin(String login) {
        this.login = StringUtils.lowerCase(login, Locale.ENGLISH);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public Instant getResetDate() {
        return resetDate;
    }

    public void setResetDate(Instant resetDate) {
        this.resetDate = resetDate;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "User{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", activated='" + activated + '\'' +
            ", langKey='" + langKey + '\'' +
            ", activationKey='" + activationKey + '\'' +
            "}";
    }
}
