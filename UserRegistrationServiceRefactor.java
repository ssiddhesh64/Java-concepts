import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDate;
import java.util.HexFormat;
import java.nio.charset.StandardCharsets;
import java.time.Period;

interface PasswordHasher {
    String hash(String password);
}

class SHA256PasswordHasher implements PasswordHasher {
    @Override
    public String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

interface UserRepository {
    void save(User user);
}

class SqlUserRepository implements UserRepository {

    @Override
    public void save(User user) {
        System.out.println("Saving user to SQL database: " + user.userName());
    }
}

interface EmailService {
    void sendWelcomeEmail(String email);
}

class ConsoleEmailService implements EmailService {
    @Override
    public void sendWelcomeEmail(String email) {
        System.out.println("Sending welcome email to: " + email);
    }
}

class UserValidator {

    void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username cannot be empty");
        }
    }

    void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("email is invalid");
        }
    }

    void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("password length should be at least 8");
        }

        boolean hadDigit = password.chars().anyMatch(Character::isDigit);
        if (!hadDigit) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
    }

    void validateAdult(LocalDate dob) {
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 18) {
            throw new IllegalArgumentException("User must be 18 or older");
        }
    }
}

public class UserRegistrationServiceRefactor {

    // public void registerUser(String username, String password, String email,
    // String dobString) {

    // if (username != null && !username.trim().equals("")) {
    // if (password != null && password.length() >= 8) {
    // boolean hasNum = false;
    // for (char c : password.toCharArray()) {
    // if (Character.isDigit(c)) {
    // hasNum = true;
    // break;
    // }
    // }
    // if (hasNum) {
    // if (email != null && email.contains("@")) {
    // // Create User Object
    // User user = new User();
    // user.setUsername(username.trim());
    // user.setPassword(password); // PLAIN TEXT!
    // user.setEmail(email);

    // try {
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    // Date dob = sdf.parse(dobString);
    // user.setDob(dob);

    // // Check if adult (18+)
    // Date today = new Date();
    // long diff = today.getTime() - dob.getTime();
    // long age = diff / (1000L * 60 * 60 * 24 * 365);

    // if (age >= 18) {
    // // Save to DB
    // System.out.println("Saving user to SQL database: " + user.getUsername());

    // // Send Welcome Email
    // System.out.println("Sending welcome email to: " + user.getEmail());
    // } else {
    // throw new IllegalArgumentException("User must be 18 or older");
    // }
    // } catch (Exception e) {
    // throw new IllegalArgumentException("Invalid date format or processing
    // error");
    // }
    // } else {
    // throw new IllegalArgumentException("Invalid email");
    // }
    // } else {
    // throw new IllegalArgumentException("Password must contain at least one
    // digit");
    // }
    // } else {
    // throw new IllegalArgumentException("Password must be at least 8 characters");
    // }
    // } else {
    // throw new IllegalArgumentException("Username cannot be empty");
    // }
    // }

    PasswordHasher hasher;
    UserRepository repository;
    EmailService emailService;
    UserValidator validator;

    public UserRegistrationServiceRefactor(
            PasswordHasher hasher,
            UserRepository repository,
            EmailService emailService,
            UserValidator validator) {

        this.hasher = hasher;
        this.repository = repository;
        this.emailService = emailService;
        this.validator = validator;
    }

    public void registerUser(String username, String password, String email, String dobString) {

        validator.validateUsername(username);
        validator.validatePassword(password);
        validator.validateEmail(email);

        LocalDate dob = LocalDate.parse(dobString);
        validator.validateAdult(dob);

        User user = new User(username, hasher.hash(password), email, dob);

        repository.save(user);
        emailService.sendWelcomeEmail(email);
    }
}

// Legacy User class definition for reference
// class User {
// private String username;
// private String password;
// private String email;
// private Date dob;

// public String getUsername() { return username; }
// public void setUsername(String username) { this.username = username; }
// public String getPassword() { return password; }
// public void setPassword(String password) { this.password = password; }
// public String getEmail() { return email; }
// public void setEmail(String email) { this.email = email; }
// public Date getDob() { return dob; }
// public void setDob(Date dob) { this.dob = dob; }
// }

record User(String userName,
        String passwordHash,
        String email,
        LocalDate dob) {
}