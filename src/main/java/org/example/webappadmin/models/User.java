package org.example.webappadmin.models;

import java.time.LocalDate;

/**
 * Represents a user in the system with their associated properties and authentication details.
 *
 * @author Juan Carlos
 */
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
    private LocalDate createdDate;
    private LocalDate updatedDate;
    private LocalDate lastLoginDate;
    private boolean verified;
    private String token;
    private LocalDate tokenExpiryDate;

    /**
     * Gets the user's unique identifier.
     *
     * @return the user's ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier.
     *
     * @param id the user's ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user's username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the user's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's role.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the date when the user was created.
     *
     * @return the creation date
     */
    public LocalDate getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the date when the user was created.
     *
     * @param createdDate the creation date to set
     */
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Gets the date when the user was last updated.
     *
     * @return the last update date
     */
    public LocalDate getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Sets the date when the user was last updated.
     *
     * @param updatedDate the last update date to set
     */
    public void setUpdatedDate(LocalDate updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * Gets the date of the user's last login.
     *
     * @return the last login date
     */
    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * Sets the date of the user's last login.
     *
     * @param lastLoginDate the last login date to set
     */
    public void setLastLoginDate(LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * Checks if the user is verified.
     *
     * @return true if the user is verified, false otherwise
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * Sets the user's verification status.
     *
     * @param verified the verification status to set
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    /**
     * Gets the user's authentication token.
     *
     * @return the authentication token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the user's authentication token.
     *
     * @param token the authentication token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets the token expiry date.
     *
     * @return the token expiry date
     */
    public LocalDate getTokenExpiryDate() {
        return tokenExpiryDate;
    }

    /**
     * Sets the token expiry date.
     *
     * @param tokenExpiryDate the token expiry date to set
     */
    public void setTokenExpiryDate(LocalDate tokenExpiryDate) {
        this.tokenExpiryDate = tokenExpiryDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", lastLoginDate=" + lastLoginDate +
                ", verified=" + verified +
                ", token='" + token + '\'' +
                ", tokenExpiryDate=" + tokenExpiryDate +
                '}';
    }
} 