package com.epam.finaltask.entity;

import java.util.Arrays;

public class Account extends Entity {
    private long accountId;
    private String login;
    private String passwordHash;
    private String email;
    private AccessLevel accessLevel;
    private int rating;
    private boolean verified;
    private boolean blocked;
    private String salt;
    private byte[] avatar;

    public Account() {
    }

    public Account(long accountId) {
        this.accountId = accountId;
    }

    public Account(String login, String passwordHash, String email, AccessLevel accessLevel,
                   int rating, boolean verified, boolean blocked, String salt, byte[] avatar) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.email = email;
        this.accessLevel = accessLevel;
        this.rating = rating;
        this.verified = verified;
        this.blocked = blocked;
        this.salt = salt;
        this.avatar = avatar;
    }

    public Account(long accountId, String login, String passwordHash, String email, AccessLevel accessLevel,
                   int rating, boolean verified, boolean blocked, String salt, byte[] avatar) {
        this.accountId = accountId;
        this.login = login;
        this.passwordHash = passwordHash;
        this.email = email;
        this.accessLevel = accessLevel;
        this.rating = rating;
        this.verified = verified;
        this.blocked = blocked;
        this.salt = salt;
        this.avatar = avatar;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
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

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
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
        if (login != null ? !login.equals(account.login) : account.login != null) return false;
        if (passwordHash != null ? !passwordHash.equals(account.passwordHash) : account.passwordHash != null)
            return false;
        if (email != null ? !email.equals(account.email) : account.email != null) return false;
        if (accessLevel != account.accessLevel) return false;
        if (salt != null ? !salt.equals(account.salt) : account.salt != null) return false;
        return Arrays.equals(avatar, account.avatar);
    }

    @Override
    public int hashCode() {
        int result = (int) (accountId ^ (accountId >>> 32));
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (passwordHash != null ? passwordHash.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (accessLevel != null ? accessLevel.hashCode() : 0);
        result = 31 * result + rating;
        result = 31 * result + (verified ? 1 : 0);
        result = 31 * result + (blocked ? 1 : 0);
        result = 31 * result + (salt != null ? salt.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(avatar);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Account{");
        sb.append("accountId=").append(accountId);
        sb.append(", login='").append(login).append('\'');
        sb.append(", passwordHash='").append(passwordHash).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", accessLevel=").append(accessLevel);
        sb.append(", rating=").append(rating);
        sb.append(", verified=").append(verified);
        sb.append(", blocked=").append(blocked);
        sb.append(", salt='").append(salt).append('\'');
        sb.append(", avatar=").append(Arrays.toString(avatar));
        sb.append('}');
        return sb.toString();
    }
}
