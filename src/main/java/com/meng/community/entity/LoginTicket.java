package com.meng.community.entity;

/**
 * Description: community
 * Created by MenG on 2022/5/21 20:27
 */
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName login_ticket
 */
public class LoginTicket implements Serializable {
    /**
     *
     */
    private Integer id;

    /**
     *
     */
    private Integer userId;

    /**
     *
     */
    private String ticket;

    /**
     * 0-有效; 1-无效;
     */
    private Integer status;

    /**
     *
     */
    private Date expired;

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     *
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     *
     */
    public String getTicket() {
        return ticket;
    }

    /**
     *
     */
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * 0-有效; 1-无效;
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 0-有效; 1-无效;
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     *
     */
    public Date getExpired() {
        return expired;
    }

    /**
     *
     */
    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        LoginTicket other = (LoginTicket) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getTicket() == null ? other.getTicket() == null : this.getTicket().equals(other.getTicket()))
                && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
                && (this.getExpired() == null ? other.getExpired() == null : this.getExpired().equals(other.getExpired()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTicket() == null) ? 0 : getTicket().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getExpired() == null) ? 0 : getExpired().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", ticket=").append(ticket);
        sb.append(", status=").append(status);
        sb.append(", expired=").append(expired);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}