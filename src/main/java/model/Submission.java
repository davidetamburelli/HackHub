package model;

import java.time.LocalDateTime;

public class Submission {
    private long id;
    private Hackathon hackathon;
    private PartecipatingTeam partecipatingTeam;
    private String response;
    private String responseURL;
    private LocalDateTime updatedAt;
}
