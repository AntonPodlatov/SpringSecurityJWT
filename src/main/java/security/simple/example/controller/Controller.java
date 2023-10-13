package security.simple.example.controller;

import security.simple.example.model.User;
import security.simple.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final UserService userService;

    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "unsecured";
    }

    @GetMapping("/secured")
    public String secured() {
        return "secured";
    }

    @GetMapping("/admin")
    public String adminData() {
        return "secured";
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> adminGetUsers(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        Page<User> users = userService.getPaged(page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/roleHierarchy")
    public ResponseEntity<?> getRoleHierarchy(){
        return ResponseEntity.ok("USER < STAFF < ADMIN");
    }

    @GetMapping("/info")
    public String userData(Principal principal) {
        return principal.getName();
    }
}
