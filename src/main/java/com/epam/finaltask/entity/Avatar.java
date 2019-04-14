package com.epam.finaltask.entity;

import java.sql.Blob;

public class Avatar extends Entity {
    private long avatarId;
    private Blob image;
    private long accountId;

    public Avatar() {
    }

    public Avatar(long avatarId, Blob image, long accountId) {
        this.avatarId = avatarId;
        this.image = image;
        this.accountId = accountId;
    }

    public long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(long avatarId) {
        this.avatarId = avatarId;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Avatar avatar = (Avatar) o;

        if (avatarId != avatar.avatarId) return false;
        if (accountId != avatar.accountId) return false;
        return image != null ? image.equals(avatar.image) : avatar.image == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (avatarId ^ (avatarId >>> 32));
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (int) (accountId ^ (accountId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Avatar{");
        sb.append("avatarId=").append(avatarId);
        sb.append(", image=").append(image);
        sb.append(", accountId=").append(accountId);
        sb.append('}');
        return sb.toString();
    }
}
