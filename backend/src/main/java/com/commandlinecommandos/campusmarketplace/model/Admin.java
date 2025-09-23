package com.commandlinecommandos.campusmarketplace.model;

import jakarta.persistence.*;
// Removed Lombok dependencies - using manual getters/setters
import java.util.Set;
import java.util.HashSet;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_level")
    private AdminLevel adminLevel = AdminLevel.ADMIN;
    
    @ElementCollection(targetClass = Permission.class)
    @CollectionTable(name = "admin_permissions", joinColumns = @JoinColumn(name = "admin_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private Set<Permission> permissions = new HashSet<>();
    
    // Constructors
    public Admin() {
        super();
        this.setRole(UserRole.ADMIN);
        this.permissions = new HashSet<>();
        // Set default permissions for regular admin
        this.permissions.add(Permission.READ_USER);
        this.permissions.add(Permission.READ_LISTING);
        this.permissions.add(Permission.MODERATE_LISTING);
        this.permissions.add(Permission.VIEW_REPORTS);
    }
    
    public Admin(String username, String email, String password, AdminLevel adminLevel, Set<Permission> permissions) {
        super(username, email, password, UserRole.ADMIN);
        this.adminLevel = adminLevel;
        this.permissions = permissions != null ? new HashSet<>(permissions) : new HashSet<>();
    }
    
    // Getters and Setters
    public AdminLevel getAdminLevel() {
        return adminLevel;
    }
    
    public void setAdminLevel(AdminLevel adminLevel) {
        this.adminLevel = adminLevel;
    }
    
    public Set<Permission> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    
    // Business methods from UML diagram
    public void moderateContent(Long contentId, String action) {
        // Implementation for content moderation
        // This would interact with listing/report services
    }
    
    public void generateReports() {
        // Implementation for report generation
        // This would compile system statistics and user activity
    }
    
    public boolean hasPermission(Permission permission) {
        return this.permissions.contains(permission);
    }
    
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }
    
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }
}
