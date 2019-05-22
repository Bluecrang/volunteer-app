package com.epam.finaltask.entity;

/**
 * Account entity.
 */
public class Account extends Entity {
    private long accountId;
    private String username;
    private String passwordHash;
    private String email;
    private AccountType accountType;
    private int rating;
    private boolean verified;
    private boolean blocked;
    /**
     * Salt which is used for hashing.
     */
    private String salt;
    /**
     * Avatar encoded in base64.
     */
    private String avatarBase64;

    public Account() {
    }

    public Account(long accountId) {
        this.accountId = accountId;
    }

    public Account(String username, String passwordHash, String email, AccountType accountType,
                   int rating, boolean verified, boolean blocked, String salt, String avatarBase64) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.accountType = accountType;
        this.rating = rating;
        this.verified = verified;
        this.blocked = blocked;
        this.salt = salt;
        this.avatarBase64 = avatarBase64;
    }

    public Account(long accountId, String username, String passwordHash, String email, AccountType accountType,
                   int rating, boolean verified, boolean blocked, String salt, String avatarBase64) {
        this.accountId = accountId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.accountType = accountType;
        this.rating = rating;
        this.verified = verified;
        this.blocked = blocked;
        this.salt = salt;
        this.avatarBase64 = avatarBase64;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAvatarBase64() {
        return avatarBase64;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (accountId != account.accountId) return false;
        if (rating != account.rating) return false;
        if (verified != account.verified) return false;
        if (blocked != account.blocked) return false;
        if (username != null ? !username.equals(account.username) : account.username != null) return false;
        if (passwordHash != null ? !passwordHash.equals(account.passwordHash) : account.passwordHash != null)
            return false;
        if (email != null ? !email.equals(account.email) : account.email != null) return false;
        if (accountType != account.accountType) return false;
        if (salt != null ? !salt.equals(account.salt) : account.salt != null) return false;
        return avatarBase64 != null ? avatarBase64.equals(account.avatarBase64) : account.avatarBase64 == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (accountId ^ (accountId >>> 32));
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (passwordHash != null ? passwordHash.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (accountType != null ? accountType.hashCode() : 0);
        result = 31 * result + rating;
        result = 31 * result + (verified ? 1 : 0);
        result = 31 * result + (blocked ? 1 : 0);
        result = 31 * result + (salt != null ? salt.hashCode() : 0);
        result = 31 * result + (avatarBase64 != null ? avatarBase64.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Account{");
        sb.append("accountId=").append(accountId);
        sb.append(", username='").append(username).append('\'');
        sb.append(", passwordHash='").append(passwordHash).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", accountType=").append(accountType);
        sb.append(", rating=").append(rating);
        sb.append(", verified=").append(verified);
        sb.append(", blocked=").append(blocked);
        sb.append(", salt='").append(salt).append('\'');
        sb.append(", avatarBase64='").append(avatarBase64).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
