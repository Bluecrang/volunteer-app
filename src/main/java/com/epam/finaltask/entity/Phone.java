package com.epam.finaltask.entity;

public class Phone extends Entity {
    private long phoneId;
    private long phoneNumber;
    private long accountId;

    public Phone() {
    }

    public Phone(long phoneId, long phoneNumber, long accountId) {
        this.phoneId = phoneId;
        this.phoneNumber = phoneNumber;
        this.accountId = accountId;
    }

    public long getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(long phoneId) {
        this.phoneId = phoneId;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
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

        Phone phone1 = (Phone) o;

        if (phoneId != phone1.phoneId) return false;
        if (phoneNumber != phone1.phoneNumber) return false;
        return accountId == phone1.accountId;
    }

    @Override
    public int hashCode() {
        int result = (int) (phoneId ^ (phoneId >>> 32));
        result = 31 * result + (int) (phoneNumber ^ (phoneNumber >>> 32));
        result = 31 * result + (int) (accountId ^ (accountId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Phone{");
        sb.append("phoneId=").append(phoneId);
        sb.append(", phoneNumber=").append(phoneNumber);
        sb.append(", accountId=").append(accountId);
        sb.append('}');
        return sb.toString();
    }
}
