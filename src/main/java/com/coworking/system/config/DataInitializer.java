package com.coworking.system.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.coworking.system.entity.OpenSpace;
import com.coworking.system.entity.Permission;
import com.coworking.system.entity.PrivateSpace;
import com.coworking.system.entity.Role;
import com.coworking.system.entity.User;
import com.coworking.system.entity.User.UserType;
import com.coworking.system.repository.OpenSpaceRepository;
import com.coworking.system.repository.PermissionRepository;
import com.coworking.system.repository.PrivateSpaceRepository;
import com.coworking.system.repository.RoleRepository;
import com.coworking.system.repository.UserRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PrivateSpaceRepository spaceRepository;
    private final OpenSpaceRepository openSpaceRepository;

    public DataInitializer(RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           PrivateSpaceRepository spaceRepository,
                           OpenSpaceRepository openSpaceRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.spaceRepository = spaceRepository;
        this.openSpaceRepository = openSpaceRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        initPermissions();
        initRoles();
        initSystemUsers();
        initTestData();  
    }
    
    @Transactional
    protected void initTestData() {
        if (openSpaceRepository.count() == 0) {
            OpenSpace openSpace = new OpenSpace();
            openSpace.setName("Espace Principal");
            openSpace.setCapacity(50);
            openSpace.setLocation("Rez-de-chaussée");
            openSpace.setIsActive(true);
            openSpaceRepository.save(openSpace);
        }
        
        if (spaceRepository.count() == 0) {
            PrivateSpace privateSpace = new PrivateSpace();
            privateSpace.setName("Salle de réunion privée");
            privateSpace.setCapacity(10);
            privateSpace.setLocation("1er étage");
            privateSpace.setPricePerHour(20.0);
            privateSpace.setPricePerDay(100.0);
            privateSpace.setIsActive(true);
            spaceRepository.save(privateSpace);
        }
    }

    @Transactional
    protected void initPermissions() {
        if (permissionRepository.count() == 0) {
            List<Permission> permissions = Arrays.asList(
                // ADMIN permissions
                createPermission("USER_READ", "Read user information"),
                createPermission("USER_WRITE", "Create/update users"),
                createPermission("USER_DELETE", "Delete users"),
                createPermission("SPACE_READ", "Read space information"),
                createPermission("SPACE_WRITE", "Create/update spaces"),
                createPermission("SPACE_DELETE", "Delete spaces"),
                createPermission("EVENT_READ", "Read event information"),
                createPermission("EVENT_WRITE", "Create/update events"),
                createPermission("EVENT_DELETE", "Delete events"),
                createPermission("SUBSCRIPTION_READ", "Read subscription information"),
                createPermission("SUBSCRIPTION_WRITE", "Create/update subscriptions"),
                createPermission("SUBSCRIPTION_DELETE", "Delete subscriptions"),
                createPermission("BOOKING_READ", "Read booking information"),
                createPermission("BOOKING_WRITE", "Create/update bookings"),
                createPermission("BOOKING_DELETE", "Delete bookings"),
                createPermission("UNAVAILABILITY_MANAGE", "Manage unavailable periods"),
                createPermission("PROFILE_MANAGE", "Manage own profile"),
                createPermission("REVIEW_READ", "Read reviews"),
                createPermission("REVIEW_WRITE", "Write reviews"),
                createPermission("PAYMENT_READ", "Read payment information"),
                createPermission("PAYMENT_VALIDATE", "Validate payments on-site"),
                createPermission("INVOICE_GENERATE", "Generate invoices"),
                createPermission("INVOICE_SEND", "Send invoices by email"),
                createPermission("FINANCIAL_FOLLOW_UP", "Follow unpaid invoices")
            );
            permissionRepository.saveAll(permissions);
        }
    }

    @Transactional
    protected void initRoles() {
        if (roleRepository.count() == 0) {

            // ADMIN role
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setPermissions(new HashSet<>(permissionRepository.findByNameIn(
                Arrays.asList(
                    "USER_READ", "USER_WRITE", "USER_DELETE",
                    "SPACE_READ", "SPACE_WRITE", "SPACE_DELETE",
                    "EVENT_READ", "EVENT_WRITE", "EVENT_DELETE",
                    "SUBSCRIPTION_READ", "SUBSCRIPTION_WRITE", "SUBSCRIPTION_DELETE",
                    "BOOKING_READ", "BOOKING_WRITE", "BOOKING_DELETE",
                    "UNAVAILABILITY_MANAGE", "PROFILE_MANAGE"
                )
            )));
            roleRepository.save(adminRole);

            // COWORKER role
            Role coworkerRole = new Role();
            coworkerRole.setName("ROLE_COWORKER");
            coworkerRole.setPermissions(new HashSet<>(permissionRepository.findByNameIn(
                Arrays.asList(
                    "SPACE_READ", "BOOKING_READ", "BOOKING_WRITE", "BOOKING_DELETE",
                    "EVENT_READ", "REVIEW_WRITE", "SUBSCRIPTION_READ",
                    "PAYMENT_READ", "PROFILE_MANAGE"
                )
            )));
            roleRepository.save(coworkerRole);

            // RECEPTIONIST role
            Role receptionistRole = new Role();
            receptionistRole.setName("ROLE_RECEPTIONIST");
            receptionistRole.setPermissions(new HashSet<>(permissionRepository.findByNameIn(
                Arrays.asList(
                    "PAYMENT_READ", "PAYMENT_VALIDATE", "INVOICE_GENERATE",
                    "INVOICE_SEND", "FINANCIAL_FOLLOW_UP", "PROFILE_MANAGE"
                )
            )));
            roleRepository.save(receptionistRole);
        }
    }

    @Transactional
    protected void initSystemUsers() {
        // ADMIN
        if (!userRepository.existsByUsername("nidhal.gharbi")) {
            User admin = new User();
            admin.setUsername("nidhal.gharbi");
            admin.setEmail("nidhalgharbi5@gmail.com");
            admin.setPassword(passwordEncoder.encode("Nidhal@admin"));
            admin.setType(UserType.ADMIN);
            admin.setEnabled(true);
            admin.setFirstName("Nidhal");
            admin.setLastName("Gharbi");
            admin.setPhone("99078443");
            admin.setProfileImagePath("/uploads/profil-img-admin.png");

            Role adminRole = roleRepository.findByNameWithPermissions("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);
        }

        // RECEPTIONIST
        if (!userRepository.existsByUsername("ayoub.akermi")) {
            User receptionist = new User();
            receptionist.setUsername("ayoub.akermi");
            receptionist.setEmail("akermiayoub20@gmail.com");
            receptionist.setPassword(passwordEncoder.encode("Ayoub@receptionist"));
            receptionist.setType(UserType.RECEPTIONIST);
            receptionist.setEnabled(true);
            receptionist.setFirstName("Ayoub");
            receptionist.setLastName("Akermi");
            receptionist.setPhone("23666875");
            receptionist.setProfileImagePath("/uploads/receptionist.png");

            Role receptionistRole = roleRepository.findByNameWithPermissions("ROLE_RECEPTIONIST")
                    .orElseThrow(() -> new RuntimeException("Receptionist role not found"));
            receptionist.setRoles(Set.of(receptionistRole));

            userRepository.save(receptionist);
        }
    }

    private Permission createPermission(String name, String description) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        return permission;
    }
    
    
}
