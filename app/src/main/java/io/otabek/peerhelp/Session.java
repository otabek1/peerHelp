package io.otabek.peerhelp;

import java.util.Date;

public class Session {
    String name, details, link;
    Date timestamp;

    public Session() {
    }

    ;

    public Session(String name, String details, String link, Date timestamp) {
        this.name = name;
        this.details = details;
        this.link = link;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
