package com.example.ccm.data;

public class SourceInfo {
    public final long id;
    public final String url;
    public final String checkedDate;   // ISO "2025-01-15"

    public SourceInfo(long id, String url, String checkedDate) {
        this.id = id;
        this.url = url;
        this.checkedDate = checkedDate;
    }
}